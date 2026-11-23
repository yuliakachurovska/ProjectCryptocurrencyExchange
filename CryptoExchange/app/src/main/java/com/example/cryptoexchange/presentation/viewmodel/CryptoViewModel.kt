package com.example.cryptoexchange.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoexchange.domain.usecase.ObserveTopCryptosUseCase
import com.example.cryptoexchange.presentation.state.CryptoUiState
import com.example.cryptoexchange.presentation.state.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CryptoViewModel(
    observeTopCryptosUseCase: ObserveTopCryptosUseCase,
    syncStatusFlow: Flow<SyncStatus>,
    private val triggerManualSync: () -> Unit
) : ViewModel() {

    private val _selectedCryptoId = MutableStateFlow<String?>(null)
    private val _isDetailOpen = MutableStateFlow(false)

    val uiState: StateFlow<CryptoUiState> = combine(
        observeTopCryptosUseCase(),
        _selectedCryptoId,
        _isDetailOpen,
        syncStatusFlow
    ) { cryptos, selectedId, isDetailOpen, syncStatus ->
        val latestTimestamp = cryptos.maxOfOrNull { it.lastUpdatedMillis }
        CryptoUiState(
            cryptos = cryptos,
            selectedCryptoId = selectedId,
            isDetailOpen = isDetailOpen,
            syncStatus = syncStatus,
            lastUpdatedMillis = latestTimestamp
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CryptoUiState()
    )

    fun onRefreshClick() {
        triggerManualSync()
    }

    fun onCryptoSelect(cryptoId: String) {
        _selectedCryptoId.value = cryptoId
        _isDetailOpen.value = true
    }

    fun onCloseDetail() {
        _isDetailOpen.value = false
    }
}