package com.example.cryptoexchange.domain.model

data class CryptoCurrency(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val logoUrl: String?,
    val priceUsd: Double?,
    val low24hUsd: Double? = null,
    val high24hUsd: Double? = null,
    val lastMarket: String? = null,
    val lastUpdatedMillis: Long
)