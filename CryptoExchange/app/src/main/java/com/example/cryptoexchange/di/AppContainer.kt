package com.example.cryptoexchange.di

import android.content.Context
import com.example.cryptoexchange.data.local.CryptoDatabase
import com.example.cryptoexchange.data.remote.RetrofitClient
import com.example.cryptoexchange.data.repository.CryptoRepositoryImpl
import com.example.cryptoexchange.domain.repository.CryptoRepository
import com.example.cryptoexchange.domain.usecase.ObserveTopCryptosUseCase
import com.example.cryptoexchange.domain.usecase.SyncTopCryptosUseCase
import com.example.cryptoexchange.worker.CryptoSyncScheduler

class AppContainer private constructor(context: Context) {

    private val appContext = context.applicationContext

    private val database: CryptoDatabase by lazy {
        CryptoDatabase.getInstance(appContext)
    }

    val repository: CryptoRepository by lazy {
        CryptoRepositoryImpl(
            api = RetrofitClient.api,
            dao = database.cryptoDao()
        )
    }

    val observeTopCryptosUseCase: ObserveTopCryptosUseCase by lazy {
        ObserveTopCryptosUseCase(repository)
    }

    val syncTopCryptosUseCase: SyncTopCryptosUseCase by lazy {
        SyncTopCryptosUseCase(repository)
    }

    val syncScheduler: CryptoSyncScheduler by lazy {
        CryptoSyncScheduler(appContext)
    }

    companion object {
        @Volatile
        private var INSTANCE: AppContainer? = null

        fun getInstance(context: Context): AppContainer {
            return INSTANCE ?: synchronized(this) {
                val instance = AppContainer(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}