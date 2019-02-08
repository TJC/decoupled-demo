package net.dryft.decoupled.uploader

import io.kotlintest.*
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.specs.StringSpec
import java.io.File

class ShopItemSpec : StringSpec()
{
    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        DBManager.setup("test.db")
    }

    fun getImage() : File {
        val imgSrc = this.javaClass.getResource("/lorem-ipsum.jpg").toURI()
        return File(imgSrc)
    }

    init {
        "creates an item" {
            val item = ShopItem.create("test item #1", "Sample description", getImage())
            item.title shouldBe "test item #1"
        }

         "can find a created item" {
            val item = ShopItem.create("test item #2", "Sample description", getImage())
            val item2 = ShopItem.find(item.id)
            item2 shouldNotBe null
            if (item2 != null) item2.title shouldBe "test item #2"
        }
    }
}