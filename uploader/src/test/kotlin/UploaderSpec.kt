package net.dryft.decoupled.uploader
import com.beust.klaxon.Klaxon
import io.kotlintest.*
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.specs.DescribeSpec
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
// https://github.com/kotlintest/kotlintest/blob/master/doc/reference.md

class UploaderSpec : DescribeSpec({
    describe("web api calls") {
        it("responds to GET /site_status") {
            withTestApplication(Application::main) {
                with(handleRequest(HttpMethod.Get, "/site_status")) {
                    response.status() shouldBe HttpStatusCode.OK
                }
            }
        }
    }
})