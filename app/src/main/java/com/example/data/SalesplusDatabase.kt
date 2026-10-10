package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [StoreProfile::class, TransactionRecord::class, CampaignRecord::class],
    version = 7,
    exportSchema = false
)
abstract class SalesplusDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun transactionDao(): TransactionDao
    abstract fun campaignDao(): CampaignDao

    companion object {
        @Volatile
        private var INSTANCE: SalesplusDatabase? = null

        fun getDatabase(context: Context): SalesplusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalesplusDatabase::class.java,
                    "salesplus_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
