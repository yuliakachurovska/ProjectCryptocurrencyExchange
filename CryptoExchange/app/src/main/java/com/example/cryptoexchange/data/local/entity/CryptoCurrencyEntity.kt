package com.example.cryptoexchange.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cryptoexchange.domain.model.CryptoCurrency

@Entity(tableName = "cryptocurrencies")
data class CryptoCurrencyEntity(
    @PrimaryKey
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val logoUrl: String?,
    val priceUsd: Double?,
    val low24hUsd: Double?,
    val high24hUsd: Double?,
    val lastMarket: String?,
    val lastUpdatedMillis: Long
) {
    fun toDomain(): CryptoCurrency = CryptoCurrency(
        id = id,
        rank = rank,
        name = name,
        symbol = symbol,
        logoUrl = logoUrl,
        priceUsd = priceUsd,
        low24hUsd = low24hUsd,
        high24hUsd = high24hUsd,
        lastMarket = lastMarket,
        lastUpdatedMillis = lastUpdatedMillis
    )
}