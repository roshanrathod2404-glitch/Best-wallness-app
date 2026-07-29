package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ContactEntity::class, MessageEntity::class, ReminderEntity::class, HistoryEntity::class, ChatRoomEntity::class, CommunityEntity::class], version = 8, exportSchema = false)
abstract class SmartFitDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun reminderDao(): ReminderDao
    abstract fun historyDao(): HistoryDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun communityDao(): CommunityDao


    companion object {
        @Volatile
        private var INSTANCE: SmartFitDatabase? = null

        fun getDatabase(context: Context): SmartFitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartFitDatabase::class.java,
                    "smartfit_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val contactDao = database.contactDao()
                        if (contactDao.getContactCount() == 0) {
                            populateInitialData(database)
                        }
                    }
                }
            }

            suspend fun populateInitialData(database: SmartFitDatabase) {
                // Production Clean Onboarding: New user registrations start with a completely empty, clean local database.
                // No pre-existing test chats or test contacts bleed into new user accounts.
            }
        }
    }
}
