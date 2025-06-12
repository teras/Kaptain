package onl.ycode.kaptain.plugins.vmachines

import io.ktor.server.routing.*
import onl.ycode.kaptain.api.initPlugin

fun Route.vmachinesPlugin() {
    val plugin = initPlugin("vmachines", "Virtual Machines")
}
