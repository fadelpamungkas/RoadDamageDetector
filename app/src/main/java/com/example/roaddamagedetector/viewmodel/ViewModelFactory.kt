package com.example.roaddamagedetector.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roaddamagedetector.AddRoadActivity
import com.example.roaddamagedetector.AddRoadViewModel
import com.example.roaddamagedetector.data.AppRepository
import com.example.roaddamagedetector.di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

class ViewModelFactory(private val appRepository: AppRepository): ViewModelProvider.NewInstanceFactory()  {
    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory( Inject.provideRepository(application)).apply {
                    instance = this
                }
            }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddRoadActivity::class.java) -> {
                AddRoadViewModel() as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
}