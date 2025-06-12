package onl.ycode.kaptain.api

private val validName = Regex("^[a-z][a-z0-9]*$")

class PluginInfo(
    val name: String,
    val label: String,
    private val externalCss: List<String>,
    private val externalJs: List<String>
) {
    init {
        require(validName.matches(name)) { "Invalid plugin name: $name." }
    }

    fun register() = also { plugins.add(this) }

    val route = "/plugin/$name"

    fun resource(resource: String) = "static/plugins/$name/$resource".asResource

    val css get() = externalCss + (if (resource("style.css") != null) listOf("$route/style.css") else emptyList())

    val js get() = externalJs + (if (resource("script.js") != null) listOf("$route/script.js") else emptyList())

    companion object {
        private val plugins = mutableListOf<PluginInfo>()
        val all get() = plugins.toList()
    }
}


