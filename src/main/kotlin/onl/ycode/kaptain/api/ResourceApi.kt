package onl.ycode.kaptain.api

private val cl = object {}.javaClass.classLoader

val String.asResource: ByteArray?
    get() = cl
        .getResourceAsStream(this)?.readBytes()