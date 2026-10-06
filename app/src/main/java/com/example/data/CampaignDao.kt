package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY timestamp DESC")
    fun getAllCampaigns(): Flow<List<CampaignRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: CampaignRecord): Long

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteCampaign(id: Long)

    @Query("DELETE FROM campaigns")
    suspend fun deleteAllCampaigns()
}
