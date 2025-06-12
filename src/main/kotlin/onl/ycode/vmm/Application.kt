package onl.ycode.vmm

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import kotlinx.html.a
import kotlinx.html.body
import onl.ycode.vmm.api.PluginInfo
import onl.ycode.vmm.api.asResource
import onl.ycode.vmm.api.response
import onl.ycode.vmm.plugins.about.aboutPlugin
import onl.ycode.vmm.plugins.vmachines.vmachinesPlugin

fun Application.module() {
    install(SSE)

    routing {
        staticResources("/static", "static")

        get("/") {
            "static/index.html".asResource?.response(call)
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

        aboutPlugin()
        vmachinesPlugin()
    }
}