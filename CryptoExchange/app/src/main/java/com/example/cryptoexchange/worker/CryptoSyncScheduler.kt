package com.example.cryptoexchange.worker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.cryptoexchange.presentation.state.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit

class CryptoSyncScheduler(context: Context) {

    private val appContext = context.applicationContext
    private val workManager = WorkManager.getInstance(appContext)

    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedulePeriodicSync() {
        val periodicRequest = PeriodicWorkRequestBuilder<CryptoSyncWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(networkConstraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    fun triggerOneTimeSync() {
        val oneTimeRequest = OneTimeWorkRequestBuilder<CryptoSyncWorker>()
            .setConstraints(networkConstraints)
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeRequest
        )
    }

    fun observeSyncStatus(): Flow<SyncStatus> {
        val oneTimeFlow = workManager.getWorkInfosForUniqueWorkFlow(UNIQUE_ONE_TIME_WORK_NAME)
        val periodicFlow = workManager.getWorkInfosForUniqueWorkFlow(UNIQUE_PERIODIC_WORK_NAME)

        return combine(oneTimeFlow, periodicFlow) { oneTimeInfos, periodicInfos ->
            val activeOneTime = oneTimeInfos.firstOrNull()
            val activePeriodic = periodicInfos.firstOrNull()

            when {
                activeOneTime?.state == WorkInfo.State.RUNNING ||
                        activePeriodic?.state == WorkInfo.State.RUNNING -> SyncStatus.Syncing

                activeOneTime?.state == WorkInfo.State.ENQUEUED -> {
                    if (isNetworkAvailable()) {
                        SyncStatus.Syncing
                    } else {
                        SyncStatus.WaitingForNetwork
                    }
                }

                activeOneTime?.state == WorkInfo.State.FAILED -> {
                    val errorMsg = activeOneTime.outputData
                        .getString(CryptoSyncWorker.KEY_ERROR_MESSAGE)
                        ?: "Не вдалося оновити курси"
                    SyncStatus.Error(errorMsg)
                }

                activeOneTime?.state == WorkInfo.State.SUCCEEDED -> SyncStatus.Success

                else -> SyncStatus.Idle
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        const val UNIQUE_ONE_TIME_WORK_NAME = "crypto_one_time_sync"
        const val UNIQUE_PERIODIC_WORK_NAME = "crypto_periodic_sync"
    }
}