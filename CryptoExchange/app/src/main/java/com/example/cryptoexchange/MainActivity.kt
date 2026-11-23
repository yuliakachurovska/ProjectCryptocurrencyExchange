package com.example.cryptoexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoexchange.di.AppContainer
import com.example.cryptoexchange.presentation.ui.screens.CryptoExchangeScreen
import com.example.cryptoexchange.presentation.viewmodel.CryptoViewModel
import com.example.cryptoexchange.presentation.viewmodel.CryptoViewModelFactory
import com.example.cryptoexchange.ui.theme.CryptoExchangeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = AppContainer.getInstance(applicationContext)

        // Запускаємо періодичну синхронізацію (раз на 1 год) та початкове оновлення (політика KEEP)
        container.syncScheduler.schedulePeriodicSync()
        container.syncScheduler.triggerOneTimeSync()

        val factory = CryptoViewModelFactory(
            observeTopCryptosUseCase = container.observeTopCryptosUseCase,
            syncStatusFlow = container.syncScheduler.observeSyncStatus(),
            triggerManualSync = {
                container.syncScheduler.triggerOneTimeSync()
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