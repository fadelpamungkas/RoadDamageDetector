package com.example.roaddamagedetector.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoadDataEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun appDAO(): AppDAO

    companion object{
        @Volatile
        private var INSTANCE : AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appdatabase")
                    .build()
                    .apply {
                        INSTANCE = this
                    }
            }
    }
}