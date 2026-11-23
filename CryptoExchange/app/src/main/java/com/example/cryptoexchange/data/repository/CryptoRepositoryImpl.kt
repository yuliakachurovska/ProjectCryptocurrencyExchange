package com.example.cryptoexchange.data.repository

import com.example.cryptoexchange.data.local.dao.CryptoDao
import com.example.cryptoexchange.data.local.entity.CryptoCurrencyEntity
import com.example.cryptoexchange.data.remote.api.CoinGeckoApi
import com.example.cryptoexchange.domain.model.CryptoCurrency
import com.example.cryptoexchange.domain.model.SyncResult
import com.example.cryptoexchange.domain.repository.CryptoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class CryptoRepositoryImpl(
    private val api: CoinGeckoApi,
    private val dao: CryptoDao
) : CryptoRepository {

    override fun observeTopCryptos(): Flow<List<CryptoCurrency>> {
        return dao.observeTopCryptos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncTopCryptos(): SyncResult {
        return try {
            val remoteCoins = api.getTopMarkets()
            val now = System.currentTimeMillis()

            val entities = remoteCoins.mapIndexed { index, dto ->
                CryptoCurrencyEntity(
                    id = dto.id,
                    rank = dto.marketCapRank ?: (index + 1),
                    name = dto.name,
                    symbol = dto.symbol,
                    logoUrl = dto.image,
                    priceUsd = dto.currentPrice,
                    low24hUsd = dto.low24h,
                    high24hUsd = dto.high24h,
                    lastMarket = "CoinGecko",
                    lastUpdatedMillis = now
                )
            }

            dao.replaceTopCryptos(entities)
            SyncResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            SyncResult.NetworkError("Помилка мережі: перевірте підключення до Інтернету")
        } catch (e: HttpException) {
            when (e.code()) {
                401, 403 -> SyncResult.ApiAuthError("Помилка доступу до API (${e.code()})")
                429 -> {
                    val retryAfter = e.response()?.headers()?.get("Retry-After")?.toLongOrNull()
                    SyncResult.RateLimitError(
                        message = "Забагато запитів до CoinGecko API. Спробуйте через хвилину",
                        retryAfterSeconds = retryAfter
                    )
                }
                else -> SyncResult.UnknownError("Помилка сервера (${e.code()}): спробуйте пізніше")
            }
        } catch (e: Exception) {
            SyncResult.UnknownError(e.localizedMessage ?: "Невідома помилка синхронізації")
        }
    }
}