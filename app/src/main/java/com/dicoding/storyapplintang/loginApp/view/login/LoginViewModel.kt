package com.dicoding.storyapplintang.loginApp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel(
    private val userRepository: UserRepository,
    private val loginPreferences: LoginPreference
) : ViewModel() {

    fun login(email: String, password: String) = userRepository.login(email, password)

    fun register(name: String, email: String, password: String) =
        userRepository.register(name, email, password)

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.saveToken(token)
        }
    }

    fun checkIfFirstTime(): LiveData<Boolean> {
        return loginPreferences.isFirstTime().asLiveData()
    }
}