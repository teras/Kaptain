package onl.ycode.vmm.plugins.vmachines

import io.ktor.server.routing.*
import onl.ycode.vmm.api.initPlugin

fun Route.vmachinesPlugin() {
    val plugin = initPlugin("vmachines", "Virtual Machines")
}
