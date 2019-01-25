package net.dryft.decoupled.pricing
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import com.beust.klaxon.Klaxon
import io.ktor.features.CallLogging
import io.ktor.features.CORS

/*
API endpoint: `/price?size={small|medium|large}`

Example output: `{"size": "medium", "price": 15.95}`
*/

data class PriceResult(val size: String, val price: Double)

fun Application.main() {
    PriceApi().apply { main() }
}

class PriceApi {

    fun getPrice(size: String?) : PriceResult {
        val price = when(size) {
            "small"  -> 12.00
            "medium" -> 15.50
            "large"  -> 17.95
            else -> throw IllegalArgumentException("Invalid size")
        }
        return PriceResult(size, price)
    }

    fun Application.main() {
        install(CallLogging)
        // install(CORS)
        // http://ktor.io/features/routing.html
        routing {
            options("/") {
                call.response.header("Access-Control-Allow-Origin", "*")
            }
            // Access-Control-Allow-Origin: *
            get("/") {
                call.respondText("Pricing API", ContentType.Text.Plain)
            }
            get("site_status") {
                call.respondText("System alive", ContentType.Text.Plain)
            }
            route("price") {
                param("size") {
                    get {
                        call.response.header("Access-Control-Allow-Origin", "*")
                        val size = call.parameters["size"]
                        val priceResult = getPrice(size)
                        val response = Klaxon().toJsonString(priceResult)
                        call.respondText(response, ContentType.Application.Json)
                    }
                }
            }
        }
    }
}
