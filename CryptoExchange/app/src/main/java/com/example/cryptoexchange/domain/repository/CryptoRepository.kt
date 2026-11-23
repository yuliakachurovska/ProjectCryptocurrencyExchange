package com.example.cryptoexchange.domain.repository

import com.example.cryptoexchange.domain.model.CryptoCurrency
import com.example.cryptoexchange.domain.model.SyncResult
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
    fun observeTopCryptos(): Flow<List<CryptoCurrency>>
    suspend fun syncTopCryptos(): SyncResult
}