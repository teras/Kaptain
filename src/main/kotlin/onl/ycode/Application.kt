package onlycode

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

val labels = ConcurrentHashMap<String, String>()

fun Application.module() {
    routing {
        staticResources("/static", "static")

        get("/") {
            val file = File(javaClass.classLoader.getResource("static/index.html")!!.toURI())
            call.respondFile(file)
        }

        post("/add-card") {
            val labelId = UUID.randomUUID().toString()
            labels[labelId] = "Click to edit"
            val bgColor = randomLightColor()

            call.respondHtml {
                body {
                    div("card m-2 p-2") {
                        style = "background-color: $bgColor"
                        div("card-content") {
                            p {
                                id = "label-$labelId"
                                classes = setOf("is-clickable")
                                attributes["hx-get"] = "/edit-label?id=$labelId"
                                attributes["hx-swap"] = "outerHTML"
                                +labels[labelId]!!
                            }
                        }
                    }
                }
            }
        }


        get("/edit-label") {
            val id = call.request.queryParameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val current = labels[id] ?: ""
            val showText = if (current == "Click to edit") "" else current

            call.respondHtml {
                body {
                    form {
                        attributes["hx-post"] = "/save-label?id=$id"
                        attributes["hx-swap"] = "outerHTML"
                        attributes["onsubmit"] = "return false;" // prevent reload
                        input(InputType.text) {
                            name = "label"
                            value = showText
                            attributes["autofocus"] = "true"
                            attributes["hx-post"] = "/save-label?id=$id"
                            attributes["hx-swap"] = "outerHTML"
                            attributes["hx-trigger"] = "blur, keyup[enter]"
                        }
                    }
                }
            }
        }




        post("/save-label") {
            val labelId = call.request.queryParameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            val updatedText = call.receiveParameters()["label"]?.trim().orEmpty()
            val finalText = if (updatedText.isBlank()) "Click to edit" else updatedText
            labels[labelId] = finalText

            call.respondHtml {
                body {
                    p {
                        id = "label-$labelId"
                        classes = setOf("is-clickable")
                        attributes["hx-get"] = "/edit-label?id=$labelId"
                        attributes["hx-swap"] = "outerHTML"
                        +finalText
                    }
                }
            }
        }
    }
}

fun randomLightColor(): String {
    fun channel() = (204..255).random().toString(16).padStart(2, '0')
    return "#${channel()}${channel()}${channel()}"
}

