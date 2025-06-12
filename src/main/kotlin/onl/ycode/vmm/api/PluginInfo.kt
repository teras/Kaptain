package onl.ycode.vmm.api

class PluginInfo(
    val name: String,
    val label: String,
) {
    fun register() = also { plugins.add(this) }

    val route = "/plugin/$name"

    fun resource(resource: String) = "static/plugins/$name/$resource".asResource

    companion object {
        private val plugins = mutableListOf<PluginInfo>()
        val all get() = plugins.toList()
    }
}


