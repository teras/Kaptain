package onl.ycode.kaptain.plugins.vmachines

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import onl.ycode.kaptain.api.initPlugin

fun Route.vmachinesPlugin() {
    val plugin = initPlugin("vmachines", "Virtual Machines")

    route(plugin.route) {
        post("create") {
            val req = call.receive<CreateVMRequest>()
            // Provision VM via qemu/libvirt
            call.respond(HttpStatusCode.Accepted)
        }
    }
}
