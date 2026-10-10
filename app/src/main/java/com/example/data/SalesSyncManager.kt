package com.example.data

import com.example.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

sealed class SyncState {
    object Idle : SyncState()
    data class Syncing(val pendingCount: Int) : SyncState()
    data class Success(val count: Int, val timestamp: Long) : SyncState()
    data class Error(val message: String) : SyncState()
}

data class SyncReport(
    val totalPending: Int = 0,
    val lastSyncTimestamp: Long? = null,
    val isAutoSyncActive: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncMessage: String = "Ready"
)

class SalesSyncManager(
    private val transactionDao: TransactionDao,
    private val networkMonitor: NetworkMonitor
) {
    private val syncMutex = Mutex()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow<Long?>(null)
    val lastSyncTimestamp: StateFlow<Long?> = _lastSyncTimestamp.asStateFlow()

    val pendingCountFlow: Flow<Int> = transactionDao.getPendingSyncCount()

    fun startAutoSync(scope: CoroutineScope) {
        scope.launch {
            networkMonitor.isOnline.collectLatest { isOnline ->
                if (isOnline) {
                    // Reconnected to internet: automatically sync all offline-entered sales!
                    syncPendingTransactions()
                }
            }
        }
    }

    suspend fun syncPendingTransactions(): Int = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            if (!networkMonitor.isConnected()) {
                _syncState.value = SyncState.Error("Device is offline. Will sync once connection is restored.")
                return@withContext 0
            }

            val pending = transactionDao.getPendingSyncTransactions()
            if (pending.isEmpty()) {
                _syncState.value = SyncState.Idle
                return@withContext 0
            }

            _syncState.value = SyncState.Syncing(pending.size)

            try {
                val pendingIds = pending.map { it.id }
                transactionDao.updateTransactionsSyncStatus(
                    ids = pendingIds,
                    status = SyncStatus.SYNCING,
                    syncedAt = null
                )

                // Simulate secure cloud endpoint batch transmission with slight network latency
                delay(600)

                val syncedTime = System.currentTimeMillis()
                transactionDao.updateTransactionsSyncStatus(
                    ids = pendingIds,
                    status = SyncStatus.SYNCED,
                    syncedAt = syncedTime
                )

                _lastSyncTimestamp.value = syncedTime
                _syncState.value = SyncState.Success(pending.size, syncedTime)
                return@withContext pending.size
            } catch (e: Exception) {
                // If sync failed, restore to FAILED so auto-sync retries next time
                val pendingIds = pending.map { it.id }
                transactionDao.updateTransactionsSyncStatus(
                    ids = pendingIds,
                    status = SyncStatus.FAILED,
                    syncedAt = null
                )
                _syncState.value = SyncState.Error(e.message ?: "Sync failed, will retry")
                return@withContext 0
            }
        }
    }
}
