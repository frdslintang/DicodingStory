package com.dicoding.storyapplintang.loginApp.di

import android.content.Context
import com.dicoding.storyapplintang.loginApp.repository.UserRepository
import com.dicoding.storyapplintang.loginApp.data.local.Room.DatabaseStory
import com.dicoding.storyapplintang.loginApp.data.remote.ApiConfig
import com.dicoding.storyapplintang.loginApp.data.remote.story.Executor
import com.dicoding.storyapplintang.loginApp.repository.StoryRepository

object Injection {
    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = DatabaseStory.getInstance(context)
        val daoStory = database.daoStory()
        val executor = Executor()
        return StoryRepository.getInstance(apiService, daoStory, executor)
    }


}