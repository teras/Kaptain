package onl.ycode.kaptain.plugins.overview

import java.io.File

data class CPULoad(val name: String, val usage: Double)

object CPUTracker {
    private var lastStats: Map<String, Pair<Long, Long>>? = null

    private fun readStats(): Map<String, Pair<Long, Long>> =
        File("/proc/stat").readLines()
            .filter { it.startsWith("cpu") && it[3].isDigit() }
            .associate {
                val parts = it.split("\\s+".toRegex())
                val name = parts[0]
                val fields = parts.drop(1).take(8).map(String::toLong)
                val total = fields.sum()
                val idle = fields[3] + fields[4]
                name to (total to idle)
            }

    fun getCPULoad(): List<CPULoad> {
        val current = readStats()
        val previous = lastStats
        lastStats = current

        if (previous == null) return emptyList()

        return current.mapNotNull { (cpu, currVals) ->
            val prevVals = previous[cpu] ?: return@mapNotNull null
            val dt = currVals.first - prevVals.first
            val di = currVals.second - prevVals.second
            val usage = if (dt == 0L) 0.0 else ((dt - di).toDouble() / dt * 100)
            CPULoad(cpu, "%.1f".format(usage).toDouble())
        }
    }
}
