package onl.ycode.vmm.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ByteArray?.response(call: ApplicationCall, contentType: ContentType = ContentType.Text.Html) =
    if (this != null) call.respondBytes(this, contentType)
    else call.respond(HttpStatusCode.NotFound)

suspend fun String?.response(call: ApplicationCall, contentType: ContentType = ContentType.Text.Html) =
    if (this != null) call.respondText(this, contentType)
    else call.respond(HttpStatusCode.NotFound)