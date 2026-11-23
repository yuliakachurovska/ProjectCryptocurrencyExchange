package com.example.cryptoexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoexchange.domain.usecase.ObserveTopCryptosUseCase
import com.example.cryptoexchange.domain.usecase.SyncTopCryptosUseCase
import com.example.cryptoexchange.presentation.fake.FakeCryptoRepository
import com.example.cryptoexchange.presentation.ui.screens.CryptoExchangeScreen
import com.example.cryptoexchange.presentation.viewmodel.CryptoViewModel
import com.example.cryptoexchange.presentation.viewmodel.CryptoViewModelFactory
import com.example.cryptoexchange.ui.theme.CryptoExchangeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fakeRepository = FakeCryptoRepository()
        val observeUseCase = ObserveTopCryptosUseCase(fakeRepository)
        val syncUseCase = SyncTopCryptosUseCase(fakeRepository)

        val factory = CryptoViewModelFactory(
            observeTopCryptosUseCase = observeUseCase,
            syncStatusFlow = fakeRepository.syncStatus,
            triggerManualSync = {
                lifecycleScope.launch {
                    syncUseCase()
                }
            }
        )

        setContent {
            CryptoExchangeTheme {
                val cryptoViewModel: CryptoViewModel = viewModel(factory = factory)
                CryptoExchangeScreen(viewModel = cryptoViewModel)
            }
        }
    }
}