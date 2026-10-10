package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transactions WHERE syncStatus = :status ORDER BY timestamp ASC")
    fun getTransactionsBySyncStatus(status: SyncStatus): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transactions WHERE syncStatus != 'SYNCED' ORDER BY timestamp ASC")
    suspend fun getPendingSyncTransactions(): List<TransactionRecord>

    @Query("SELECT COUNT(*) FROM transactions WHERE syncStatus != 'SYNCED'")
    fun getPendingSyncCount(): Flow<Int>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecord): Long

    @Query("UPDATE transactions SET syncStatus = :status, syncedAt = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: SyncStatus, syncedAt: Long?)

    @Query("UPDATE transactions SET syncStatus = :status, syncedAt = :syncedAt WHERE id IN (:ids)")
    suspend fun updateTransactionsSyncStatus(ids: List<Long>, status: SyncStatus, syncedAt: Long?)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>
}
