package net.dryft.decoupled.uploader

import net.dryft.decoupled.uploader.table.ShopItemTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object DBManager {
    fun setup(dbName: String) {
        println("Using db: $dbName")
        Database.connect("jdbc:sqlite:$dbName", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction { SchemaUtils.create(ShopItemTable) }
    }
}