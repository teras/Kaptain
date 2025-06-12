package onl.ycode.kaptain.plugins.overview

import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import onl.ycode.kaptain.api.initPlugin
import onl.ycode.kaptain.plugins.overview.CPUTracker.getCPULoad
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries


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
                        put("mem", JsonPrimitive(currentMemoryUsage()))
                        put("disk", JsonPrimitive(currentDiskUsage()))
                        put("cpu", buildJsonArray {
                            getCPULoad().forEach {
                                add(buildJsonObject {
                                    put("name", JsonPrimitive(it.name))
                                    put("usage", JsonPrimitive(it.usage))
                                })
                            }
                        })

                        put("temp", buildJsonArray {
                            getThermalZones().forEach {
                                add(buildJsonObject {
                                    put("name", JsonPrimitive(it.name))
                                    put("temp", JsonPrimitive(it.temp))
                                })
                            }
                        })
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
    val load = java.lang.management.ManagementFactory.getOperatingSystemMXBean()
    return (load.systemLoadAverage / load.availableProcessors * 100).coerceIn(0.0, 100.0).toInt()
}

private fun currentMemoryUsage(): Double {
    val memInfo = File("/proc/meminfo").readLines()
    val total = memInfo.first { it.startsWith("MemTotal") }.split(Regex("\\s+"))[1].toLong()
    val free = memInfo.first { it.startsWith("MemAvailable") }.split(Regex("\\s+"))[1].toLong()
    val used = total - free
    return String.format("%.1f", used / 1024.0 / 1024).toDouble() // in GB
}

private val rootFile = File("/")

private fun currentDiskUsage(): Int {
    val used = rootFile.totalSpace - rootFile.freeSpace
    return (used / (1024 * 1024)).toInt()
}

private val tempFilePath: File? by lazy {
    File("/sys/devices/virtual/thermal/")
        .walkTopDown()
        .filter { it.isFile && it.name == "temp" && it.parentFile.name.startsWith("thermal_zone") }
        .firstOrNull { it.readText().trim().toIntOrNull() != null }
}

private fun currentTemperature(): Double {
    return tempFilePath
        ?.readText()
        ?.trim()
        ?.toIntOrNull()
        ?.div(1000.0)  // millidegrees → degrees
        ?.let { String.format("%.1f", it).toDouble() } ?: 0.0
}


private data class ThermalZone(val name: String, val temp: Double)
private data class MemoryUsage(val total: Double, val used: Double)
private data class DiskUsage(val name: String, val total: Long, val used: Long, val free: Long)


private fun getThermalZones(): List<ThermalZone> {
    val base = Path("/sys/class/thermal")
    return try {
        base.listDirectoryEntries("thermal_zone*")
            .mapNotNull { zone ->
                try {
                    val name = zone.resolve("type").toFile().readText().trim()
                    val raw = zone.resolve("temp").toFile().readText().trim().toDouble()
                    ThermalZone(name, raw / 1000.0)
                } catch (_: Exception) {
                    null
                }
            }
    } catch (_: Exception) {
        emptyList()
    }
}
