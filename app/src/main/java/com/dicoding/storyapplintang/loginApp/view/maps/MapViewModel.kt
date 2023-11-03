package com.dicoding.storyapplintang.loginApp.view.maps

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.repository.StoryRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class MapViewModel(
    private val storyRepository: StoryRepository,
    private val loginPreference: LoginPreference
) : ViewModel() {

   fun getStoryWithLocation(token: String) = storyRepository.getStoriesWithLocation(token)

    fun checkIfTokenAvailable(): LiveData<String> {
        return loginPreference.getToken().asLiveData()
    }

}