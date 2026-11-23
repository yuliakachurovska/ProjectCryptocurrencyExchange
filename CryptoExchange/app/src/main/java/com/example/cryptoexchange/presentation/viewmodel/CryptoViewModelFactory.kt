package com.example.cryptoexchange.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cryptoexchange.domain.usecase.ObserveTopCryptosUseCase
import com.example.cryptoexchange.presentation.state.SyncStatus
import kotlinx.coroutines.flow.Flow

class CryptoViewModelFactory(
    private val observeTopCryptosUseCase: ObserveTopCryptosUseCase,
    private val syncStatusFlow: Flow<SyncStatus>,
    private val triggerManualSync: () -> Unit
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            return CryptoViewModel(
                observeTopCryptosUseCase = observeTopCryptosUseCase,
                syncStatusFlow = syncStatusFlow,
                triggerManualSync = triggerManualSync
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}