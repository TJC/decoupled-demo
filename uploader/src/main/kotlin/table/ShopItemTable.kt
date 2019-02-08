package net.dryft.decoupled.uploader.table
import org.jetbrains.exposed.sql.*

object ShopItemTable : Table("shop_item") {
    val id = varchar("id", 64).primaryKey()
    val title = varchar("title", 128)
    val description = varchar("description", 1024).default("")
    val image = varchar("image", 256).default("")
    val thumbnail = varchar("thumbnail", 256).default("")
}