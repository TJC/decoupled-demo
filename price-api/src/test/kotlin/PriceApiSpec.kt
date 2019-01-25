package net.dryft.decoupled.pricing
import com.beust.klaxon.Klaxon
import io.kotlintest.*
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.specs.DescribeSpec
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
// https://github.com/kotlintest/kotlintest/blob/master/doc/reference.md

class PriceApiSpec : DescribeSpec({
    describe("check prices") {
        it("returns price for small item") {
            val pr = PriceApi().getPrice("small")
            pr.size shouldBe "small"
            pr.price shouldBe 12.00
        }

        it("raises exception on null size") {
            shouldThrow<IllegalArgumentException> {
                PriceApi().getPrice(null)
            }
        }
    }

    describe("web api calls") {
        it("responds to GET /") {
            withTestApplication(Application::main) {
                with(handleRequest(HttpMethod.Get, "/")) {
                    response.status() shouldBe HttpStatusCode.OK
                }
            }
        }

        it("returns a price for large size") {
            withTestApplication(Application::main) {
                with(handleRequest(HttpMethod.Get, "/price?size=large")) {
                    response.status() shouldBe HttpStatusCode.OK
                    assertNotNull(response.content)
                    val body = response.content.toString()
                    body shouldStartWith "{" // ie. it's JSON?
                    val result = Klaxon().parse<PriceResult>(body)
                    assertNotNull(result)
                    result.size shouldBe "large"
                    result.price shouldBe 17.95
                }
            }
        }
    }
})