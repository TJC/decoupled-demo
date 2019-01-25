package net.dryft.decoupled.uploader

import java.io.File
import java.security.InvalidParameterException
import java.util.*

data class ShopItem(
        val id: String,
        var title: String,
        var description: String,
        var image: String? = null,
        var thumbnail: String? = null
) {
    fun save() {
        // TODO
    }

    companion object {
        // remove leading and trailing whitespace:
        fun String.myStrip() : String = this.replace("(^\\s+)|(\\s+$)".toRegex(), "")

        fun find(id: String?): ShopItem? {
            if (id == null) return null
            return ShopItem(id = id, title = "Test", description = "Example")
        }

        fun create(title: String, description: String, baseImage: File): ShopItem {
            println("Creating new item. Title=$title\nDescription=$description\n")
            if (title.myStrip().isEmpty()) throw InvalidParameterException("Title must not be empty")
            if (baseImage.length() == 0L) throw InvalidParameterException("Item missing image")

            val id = UUID.randomUUID().toString()
            // TODO: Process images
            val image = "s3://"
            val thumbnail = "s3://"
            val item = ShopItem(id, title.myStrip(), description, image, thumbnail)
            item.save()
            return item
        }
    }

    // fun stripString(input: String): String = input.replace("\\s+".toRegex(), "")


}

