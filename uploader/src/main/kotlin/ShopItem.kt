package net.dryft.decoupled.uploader

import java.io.File
import java.security.InvalidParameterException
import java.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import net.dryft.decoupled.uploader.table.ShopItemTable

// TODO: There is a lot of double-handling going on here; look into whether the Exposed DAO API
// is better, and/or could be substituted for this class.

data class ShopItem(
        val id: String,
        var title: String,
        var description: String,
        var image: String,
        var thumbnail: String
) {
    fun save() {
        val myself = this
        transaction {
            ShopItemTable.insert {
                it[id] = myself.id
                it[title] = myself.title
                it[description] = myself.description
                it[image] = myself.image
                it[thumbnail] = myself.thumbnail
            }
            // TODO: Emit Kinesis event here.
        }
    }

    companion object {
        // remove leading and trailing whitespace:
        fun String.myStrip() : String = this.replace("(^\\s+)|(\\s+$)".toRegex(), "")

        fun find(id: String?): ShopItem? {
            if (id == null) return null
            return transaction {
                val query = ShopItemTable.select { ShopItemTable.id eq id }.limit(1)
                val item = query.map {
                    ShopItem(
                            it[ShopItemTable.id],
                            it[ShopItemTable.title],
                            it[ShopItemTable.description],
                            it[ShopItemTable.image],
                            it[ShopItemTable.thumbnail]
                    )
                }
                item.firstOrNull()
            }
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

