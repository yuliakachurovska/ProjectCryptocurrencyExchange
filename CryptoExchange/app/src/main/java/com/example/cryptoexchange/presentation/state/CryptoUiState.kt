package com.example.cryptoexchange.presentation.state

import com.example.cryptoexchange.domain.model.CryptoCurrency

sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object WaitingForNetwork : SyncStatus
    data object Syncing : SyncStatus
    data object Success : SyncStatus
    data class Error(val message: String) : SyncStatus
}

data class CryptoUiState(
    val cryptos: List<CryptoCurrency> = emptyList(),
    val selectedCryptoId: String? = null,
    val isDetailOpen: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.Idle,
    val lastUpdatedMillis: Long? = null
) {
    val selectedCrypto: CryptoCurrency?
        get() = cryptos.find { it.id == selectedCryptoId } ?: cryptos.firstOrNull()

    val isInitialEmpty: Boolean
        get() = cryptos.isEmpty()

    val isRefreshing: Boolean
        get() = syncStatus is SyncStatus.Syncing || syncStatus is SyncStatus.WaitingForNetwork
}