package onl.ycode.vmm

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import kotlinx.html.a
import kotlinx.html.body
import onl.ycode.vmm.api.PluginInfo
import onl.ycode.vmm.api.asResource
import onl.ycode.vmm.api.response
import onl.ycode.vmm.plugins.overview.overviewPlugin
import onl.ycode.vmm.plugins.vmachines.vmachinesPlugin

fun Application.module() {
    install(SSE)

    routing {
        get("/") {
            val baIndex = "static/index.html".asResource ?: return@get call.respond(HttpStatusCode.NotFound)
            val injected = PluginInfo.all.flatMap { p ->
                val css = p.css.map { """    <link rel="stylesheet" href="$it">""" }
                val js = p.js.map { """    <script src="$it"></script>""" }
                css + js
            }.joinToString("\n")

            baIndex
                .decodeToString()
                .replace("<!-- PLUGIN-INJECTIONS -->", injected)
                .response(call)
        }

        get("/style.css") {
            "static/style.css".asResource?.response(call, ContentType.Text.CSS)
        }

        get("/script.js") {
            "static/script.js".asResource?.response(call, ContentType.Text.JavaScript)
        }

        get("/backend-name") {
            call.respondText("<span>MyServer v1</span>", ContentType.Text.Html)
        }

        get("/sidebar") {
            call.respondHtml {
                body {
                    for (plugin in PluginInfo.all) {
                        a(href = "#") {
                            attributes["hx-get"] = plugin.route
                            attributes["hx-target"] = "#main"
                            attributes["hx-swap"] = "innerHTML"
                            +plugin.label
                        }
                    }
                }
            }
        }

        overviewPlugin()
        vmachinesPlugin()
    }
}