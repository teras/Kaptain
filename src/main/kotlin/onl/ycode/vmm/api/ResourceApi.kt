package onl.ycode.vmm.api

private val cl = object {}.javaClass.classLoader

val String.asResource: ByteArray?
    get() = cl
        .getResourceAsStream(this)?.readBytes()