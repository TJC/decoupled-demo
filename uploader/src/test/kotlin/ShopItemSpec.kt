package net.dryft.decoupled.uploader

import io.kotlintest.*
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.specs.DescribeSpec
import java.io.File

class ShopItemSpec : DescribeSpec({
    fun getImage() : File {
        val imgSrc = this.javaClass.getResource("/lorem-ipsum.jpg").toURI()
        return File(imgSrc)
    }

    DBManager.setup(":memory")

    describe("ShopItem") {
        it("creates an item") {
            val item = ShopItem.create("test item #1", "Sample description", getImage())
            item.title shouldBe "test item #1"
        }

        it("can find a created item") {
            val item = ShopItem.create("test item #2", "Sample description", getImage())
            val item2 = ShopItem.find(item.id)
            item2 shouldNotBe null
            if (item2 != null) item2.title shouldBe "test item #2"
        }
    }
})