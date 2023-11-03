package com.dicoding.storyapplintang.loginApp.view.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.repository.StoryRepository
import com.dicoding.storyapplintang.loginApp.di.Injection

class MainViewModelFactory private constructor(
    private val storyRepo: StoryRepository,
    private val loginPreferences: LoginPreference
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepo, loginPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: MainViewModelFactory? = null

        fun getInstance(
            context: Context,
            loginPreference: LoginPreference
        ): MainViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideStoryRepository(context),
                    loginPreference
                )
            }.also { instance = it }
    }
}