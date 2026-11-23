package com.example.cryptoexchange.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cryptoexchange.data.local.dao.CryptoDao
import com.example.cryptoexchange.data.local.entity.CryptoCurrencyEntity

@Database(
    entities = [CryptoCurrencyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CryptoDatabase : RoomDatabase() {

    abstract fun cryptoDao(): CryptoDao

    companion object {
        @Volatile
        private var INSTANCE: CryptoDatabase? = null

        fun getInstance(context: Context): CryptoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CryptoDatabase::class.java,
                    "crypto_exchange.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}