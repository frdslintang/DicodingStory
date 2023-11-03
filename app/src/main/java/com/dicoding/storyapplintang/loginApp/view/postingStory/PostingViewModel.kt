package com.dicoding.storyapplintang.loginApp.view.postingStory

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.repository.StoryRepository
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class PostingViewModel(
    private val storyRepository: StoryRepository,
    private val loginPreferences: LoginPreference
) : ViewModel() {
    fun postStory(token: String, imageFile: File, description: String) =
        storyRepository.postMyStory(token, imageFile, description)

    fun checkIfTokenAvailable(): LiveData<String> {
        return loginPreferences.getToken().asLiveData()
    }
}