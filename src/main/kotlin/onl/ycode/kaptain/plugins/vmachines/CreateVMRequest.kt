package onl.ycode.kaptain.plugins.vmachines

import kotlinx.serialization.Serializable

@Serializable
data class CreateVMRequest(
    val name: String,
    val iso: String,
    val osType: String,
    val diskSize: Double,          // in GB
    val memSize: Double            // in GB
)
