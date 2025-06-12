package onl.ycode.vmm.api

import io.ktor.http.*
import io.ktor.server.routing.*

fun Route.initPlugin(
    name: String,
    label: String,
    externalCss: List<String> = emptyList(),
    externalJS: List<String> = emptyList()
): PluginInfo {
    val plugin = PluginInfo(name, label, externalCss, externalJS)
    plugin.register()
    route(plugin.route) {
        get {
            plugin.resource("index.html")?.decodeToString()?.let {
                "<div data-plugin=\"${name.capitalize()}\">\n$it</div>\n"
            }?.response(call)
        }

        get("/script.js") {
            plugin.resource("script.js").response(call, ContentType.Application.JavaScript)
        }

        get("/style.css") {
            plugin.resource("style.css").response(call, ContentType.Text.CSS)
        }
    }
    return plugin
}