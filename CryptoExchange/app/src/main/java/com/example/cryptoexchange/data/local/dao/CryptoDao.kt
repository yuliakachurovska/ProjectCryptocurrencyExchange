package com.example.cryptoexchange.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.cryptoexchange.data.local.entity.CryptoCurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoDao {

    @Query("SELECT * FROM cryptocurrencies ORDER BY rank ASC")
    fun observeTopCryptos(): Flow<List<CryptoCurrencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CryptoCurrencyEntity>)

    @Query("DELETE FROM cryptocurrencies WHERE id NOT IN (:activeIds)")
    suspend fun deleteNotIn(activeIds: List<String>)

    @Query("DELETE FROM cryptocurrencies")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceTopCryptos(items: List<CryptoCurrencyEntity>) {
        if (items.isEmpty()) {
            clearAll()
        } else {
            insertAll(items)
            deleteNotIn(items.map { it.id })
        }
    }
}