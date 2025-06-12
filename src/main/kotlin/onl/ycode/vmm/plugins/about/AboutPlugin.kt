package onl.ycode.vmm.plugins.about

import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import onl.ycode.vmm.api.PluginInfo
import onl.ycode.vmm.api.response
import kotlin.time.Duration.Companion.seconds

fun Route.aboutPlugin() {
    val info = PluginInfo("about", "About").register()

    route(info.route) {
        get {
            info.resource("index.html")?.response(call)
        }

        sse("events") {
            heartbeat {
                period = 20.seconds
                event = ServerSentEvent(comments = "heartbeat")
            }
            while (true) {
                send(
                    ServerSentEvent(
                        data =
                            "<span id=\"about_cpu\">${currentCPUUsage()}</span>" +
                                    "<span id=\"about_mem\">${currentMemoryUsage()}</span>" +
                                    "<span id=\"about_disk\">${currentDiskUsage()}</span>" +
                                    "<span id=\"about_temp\">${currentTemperature()}</span>"
                    )
                )
                delay(1000)
            }
        }

        sse("graph") {
            try {
                while (true) {
                    val sec = System.currentTimeMillis()
                    val json = buildJsonObject {
                        put("ts", JsonPrimitive(sec))
                        put("cpu", JsonPrimitive(currentCPUUsage()))
                        put("mem", JsonPrimitive(currentMemoryUsage()))
                        put("disk", JsonPrimitive(currentDiskUsage()))
                        put("temp", JsonPrimitive(currentTemperature()))
                    }.toString()

                    send(ServerSentEvent(data = json))
                    delay(1000)
                }
            } catch (e: Exception) {
                // Handle any exceptions that may occur during the SSE stream
                println("Error in SSE stream: ${e.message}")
            }
        }


    }
}


private fun currentCPUUsage(): Int {
    val sec = System.currentTimeMillis() / 1000
    return (sec % 90 + 5).toInt()
}

private fun currentMemoryUsage(): Int {
    val sec = System.currentTimeMillis() / 1000
    return ((sec * 37) % 14000 + 1024).toInt()
}

private fun currentDiskUsage(): Int {
    val sec = System.currentTimeMillis() / 1000
    return ((sec * 91) % 230000 + 20000).toInt()
}

private fun currentTemperature(): Int {
    val sec = System.currentTimeMillis() / 1000
    return ((sec * 17) % 40 + 35).toInt()
}
