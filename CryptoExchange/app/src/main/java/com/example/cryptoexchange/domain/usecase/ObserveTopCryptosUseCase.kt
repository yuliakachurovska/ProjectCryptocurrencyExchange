package com.example.cryptoexchange.domain.usecase

import com.example.cryptoexchange.domain.model.CryptoCurrency
import com.example.cryptoexchange.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class ObserveTopCryptosUseCase(
    private val repository: CryptoRepository
) {
    operator fun invoke(): Flow<List<CryptoCurrency>> {
        return repository.observeTopCryptos()
    }
}