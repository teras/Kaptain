package onl.ycode.vmm.plugins.overview

import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import onl.ycode.vmm.api.initPlugin

fun Route.overviewPlugin() {
    val plugin = initPlugin(
        "overview",
        "Overview",
        externalCss = listOf("https://unpkg.com/uplot/dist/uPlot.min.css"),
        externalJS = listOf("https://unpkg.com/uplot/dist/uPlot.iife.min.js")
    )

    route(plugin.route) {

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
