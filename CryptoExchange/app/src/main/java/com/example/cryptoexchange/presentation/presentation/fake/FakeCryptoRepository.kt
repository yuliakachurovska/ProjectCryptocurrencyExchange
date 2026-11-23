package com.example.cryptoexchange.presentation.fake

import com.example.cryptoexchange.domain.model.CryptoCurrency
import com.example.cryptoexchange.domain.model.SyncResult
import com.example.cryptoexchange.domain.repository.CryptoRepository
import com.example.cryptoexchange.presentation.state.SyncStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class FakeCryptoRepository : CryptoRepository {

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: Flow<SyncStatus> = _syncStatus.asStateFlow()

    private val _cryptos = MutableStateFlow(initialMockData())

    override fun observeTopCryptos(): Flow<List<CryptoCurrency>> = _cryptos.asStateFlow()

    override suspend fun syncTopCryptos(): SyncResult {
        _syncStatus.value = SyncStatus.Syncing
        delay(1_500L)

        val now = System.currentTimeMillis()
        _cryptos.value = _cryptos.value.map { coin ->
            val updatedPrice = coin.priceUsd?.let { current ->
                val delta = Random.nextDouble(-0.02, 0.02)
                current * (1.0 + delta)
            }
            coin.copy(
                priceUsd = updatedPrice,
                lastUpdatedMillis = now
            )
        }
        _syncStatus.value = SyncStatus.Success
        return SyncResult.Success
    }

    private fun initialMockData(): List<CryptoCurrency> {
        val now = System.currentTimeMillis()
        return listOf(
            CryptoCurrency(
                id = "bitcoin",
                rank = 1,
                name = "Bitcoin",
                symbol = "BTC",
                logoUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
                priceUsd = 67420.50,
                low24hUsd = 66100.00,
                high24hUsd = 68250.00,
                lastMarket = "CoinGecko",
                lastUpdatedMillis = now
            ),
            CryptoCurrency(
                id = "ethereum",
                rank = 2,
                name = "Ethereum",
                symbol = "ETH",
                logoUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
                priceUsd = 3485.15,
                low24hUsd = 3410.00,
                high24hUsd = 3540.80,
                lastMarket = "CoinGecko",
                lastUpdatedMillis = now
            ),
            CryptoCurrency(
                id = "tether",
                rank = 3,
                name = "Tether",
                symbol = "USDT",
                logoUrl = "https://assets.coingecko.com/coins/images/325/large/Tether.png",
                priceUsd = 1.0002,
                low24hUsd = 0.9995,
                high24hUsd = 1.0010,
                lastMarket = "CoinGecko",
                lastUpdatedMillis = now
            ),
            CryptoCurrency(
                id = "pepe",
                rank = 4,
                name = "Pepe",
                symbol = "PEPE",
                logoUrl = "https://assets.coingecko.com/coins/images/29850/large/pepe-token.jpeg",
                priceUsd = 0.00001184,
                low24hUsd = 0.00001090,
                high24hUsd = 0.00001230,
                lastMarket = "CoinGecko",
                lastUpdatedMillis = now
            ),
            CryptoCurrency(
                id = "test-no-price",
                rank = 5,
                name = "Offline Token",
                symbol = "OFF",
                logoUrl = null,
                priceUsd = null,
                low24hUsd = null,
                high24hUsd = null,
                lastMarket = null,
                lastUpdatedMillis = now
            )
        )
    }
}