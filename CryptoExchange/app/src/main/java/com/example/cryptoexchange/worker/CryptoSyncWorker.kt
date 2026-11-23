package com.example.cryptoexchange.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.cryptoexchange.di.AppContainer
import com.example.cryptoexchange.domain.model.SyncResult

class CryptoSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val container = AppContainer.getInstance(applicationContext)
        return when (val syncResult = container.syncTopCryptosUseCase()) {
            is SyncResult.Success -> Result.success()

            is SyncResult.NetworkError,
            is SyncResult.RateLimitError -> {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    val message = when (syncResult) {
                        is SyncResult.NetworkError -> syncResult.message
                        is SyncResult.RateLimitError -> syncResult.message
                        else -> "Помилка мережі"
                    }
                    Result.failure(workDataOf(KEY_ERROR_MESSAGE to message))
                }
            }

            is SyncResult.ApiAuthError -> {
                Result.failure(workDataOf(KEY_ERROR_MESSAGE to syncResult.message))
            }

            is SyncResult.UnknownError -> {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure(workDataOf(KEY_ERROR_MESSAGE to syncResult.message))
                }
            }
        }
    }

    companion object {
        const val KEY_ERROR_MESSAGE = "error_message"
    }
}