package com.example.roaddamagedetector.di

import android.content.Context
import com.example.roaddamagedetector.data.AppRepository
import com.example.roaddamagedetector.data.local.AppDatabase
import com.example.roaddamagedetector.data.local.LocalSource
import com.example.roaddamagedetector.data.remote.RemoteSource
import com.example.roaddamagedetector.utils.AppExecutors

object Inject {
    fun provideRepository(context: Context): AppRepository {
        val db = AppDatabase.getDatabase(context)

        return AppRepository.getInstance(
            RemoteSource.getInstance(),
            LocalSource.getInstance(db.appDAO()),
            AppExecutors()
        )
    }
}