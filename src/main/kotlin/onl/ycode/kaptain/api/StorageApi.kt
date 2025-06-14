package onl.ycode.kaptain.api

import onl.ycode.kaptain.api.OS.*
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import java.io.File

private val os: OS = System.getProperty("os.name").lowercase().let {
    when {
        it.startsWith("windows") -> WINDOWS
        it.startsWith("mac") -> MAC
        it.startsWith("linux") -> LINUX
        else -> OTHER
    }
}

private val cachedDir = when (os) {
    WINDOWS -> File(System.getenv("LOCALAPPDATA"), "FotoFixer")
    MAC -> File(System.getProperty("user.home"), "Library/Caches/com.fotofixer")
    else -> File(System.getProperty("user.home"), ".cache/fotofixer")
}.apply { if (!exists()) mkdirs() }


private enum class OS {
    WINDOWS, MAC, LINUX, OTHER
}

private val db = DBMaker.fileDB(File(cachedDir, "fotofix.db"))
    .fileMmapEnableIfSupported()
    .transactionEnable()
    .closeOnJvmShutdown()
    .cleanerHackEnable()
    .make()

val keysDb: HTreeMap<String, String> = db
    .hashMap("meta", Serializer.STRING, Serializer.STRING)
    .createOrOpen()