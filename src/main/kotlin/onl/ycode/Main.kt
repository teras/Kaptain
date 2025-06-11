package onl.ycode

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import onlycode.module

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
