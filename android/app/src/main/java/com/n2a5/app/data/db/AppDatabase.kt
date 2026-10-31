package com.n2a5.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.n2a5.app.data.model.Server
import com.n2a5.app.data.model.Subscription
import com.n2a5.app.data.model.ConnectionHistory
import com.n2a5.app.data.model.AppLog

@Database(
    entities = [Server::class, Subscription::class, ConnectionHistory::class, AppLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun connectionHistoryDao(): ConnectionHistoryDao
    abstract fun appLogDao(): AppLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "n2a5_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
