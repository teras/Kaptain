package onl.ycode.vmm.plugins.vmachines

import io.ktor.http.*
import io.ktor.server.routing.*
import onl.ycode.vmm.api.PluginInfo
import onl.ycode.vmm.api.response

fun Route.vmachinesPlugin() {
    val info = PluginInfo("vmachines", "Virtual Machines").register()

    route(info.route) {
        get {
            info.resource("index.html")?.response(call, ContentType.Text.Html)
        }
    }
}
