package net.dryft.decoupled.uploader

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
// import io.ktor.gson.*
import com.beust.klaxon.Klaxon
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.http.content.*
import io.ktor.request.receiveMultipart
import org.jetbrains.exposed.sql.Database
import java.io.*
import java.security.InvalidParameterException
import java.util.*


fun Application.main() {
    DBManager.setup("uploader.db")
    Uploader().apply { main() }
}

class Uploader {
    // Can I hack this in like this? Rather than use ContentNegotiation plugin. More for interest's sake..
    suspend fun ApplicationCall.respondJson(input: Any) {
        val json = Klaxon().toJsonString(input)
        respondText(json, ContentType.Application.Json)
    }

    suspend fun ApplicationCall.respondJson404() {
        val notFoundMsg = "{}"
        respondText(notFoundMsg, ContentType.Application.Json, status = HttpStatusCode.NotFound)
    }

    fun Application.main() {
        install(CallLogging)
        install(AutoHeadResponse)
        // http://ktor.io/features/routing.html
        routing {
            get("/") {
                call.respondRedirect("app/")
            }
            get("site_status") {
                call.respondText("System alive", ContentType.Text.Plain)
            }
            itemRouting()
            staticRouting()
        }
    }

    fun Route.itemRouting() {
        route("item/{id}") {
            get {
                val id = call.parameters["id"]
                val item = ShopItem.find(id)
                when (item) {
                    null -> call.respondJson404()
                    else -> call.respondJson(item)
                }
            }
            put {
                val id = call.parameters["id"]
                call.respondText("Not yet implemented")
            }
        }
        route("item") {
            post {
                val newItem = handleFormSubmit(call.receiveMultipart())
                call.respondJson(newItem)
            }
        }
    }

    fun Route.staticRouting() {
        static("app") {
            staticRootFolder = File("www-app")
            files("css")
            files("js")
            files("html")
            default("index.html")
        }
    }

    suspend fun handleFormSubmit(multipart: MultiPartData) : ShopItem {
        println("In handleFormSubmit...")
        var title = ""
        var description = ""
        var imageFile: File? = null

        multipart.forEachPart { part ->
            println("I have a part...")
            when (part) {
                is PartData.FormItem -> {
                    println("form item value: ${part.name} = ${part.value}")
                    when (part.name) {
                        "title" -> title = part.value
                        "description" -> description = part.value
                    }
                }
                is PartData.FileItem -> {
                    println("Form file item: ${part.originalFileName}")
                    val tmpId = UUID.randomUUID().toString()
                    val ext = File(part.originalFileName).extension
                    val file = File("/tmp", "upload-$tmpId.$ext")
                    println("Writing image to temporary file: $file")
                    part.streamProvider().use { input -> file.outputStream().buffered().use { output -> input.copyTo(output) } }
                    imageFile = file
                }
                is PartData.BinaryItem -> {
                    println("received BinaryItem, uh oh!")
                }
                else -> println("received something else?!")
            }

            part.dispose()
        }

        if (imageFile != null) {
            return ShopItem.create(title, description, imageFile!!)
        }
        println("Uh oh, didn't find everything")
        throw InvalidParameterException("Missing required form fields")
    }

}
