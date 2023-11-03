package com.dicoding.storyapplintang.loginApp.view.maps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapplintang.loginApp.di.Injection
import com.dicoding.storyapplintang.loginApp.repository.StoryRepository
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.view.main.MainViewModelFactory

class MapViewModelFactory private constructor(
    private val storyRepository: StoryRepository,
    private val loginPreferences: LoginPreference
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(storyRepository, loginPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: MapViewModelFactory? = null

        fun getInstance(
            context: Context,
            loginPreference: LoginPreference
        ): MapViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MapViewModelFactory(
                    Injection.provideStoryRepository(context),
                    loginPreference
                )
            }.also { instance = it }
    }
}
