package com.dicoding.storyapplintang.loginApp.view.postingStory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.repository.StoryRepository
import com.dicoding.storyapplintang.loginApp.di.Injection

class PostingViewModelFactory private constructor(
    private val storyRepository: StoryRepository,
    private val loginPreference: LoginPreference
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostingViewModel::class.java)) {
            return PostingViewModel(storyRepository, loginPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: PostingViewModelFactory? = null

        fun getInstance(
            context: Context,
            loginPreference: LoginPreference
        ): PostingViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: PostingViewModelFactory(
                    Injection.provideStoryRepository(context),
                    loginPreference
                )
            }.also { instance = it }
    }
}