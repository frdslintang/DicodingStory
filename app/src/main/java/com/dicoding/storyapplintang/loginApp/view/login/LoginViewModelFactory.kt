package com.dicoding.storyapplintang.loginApp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.repository.UserRepository
import com.dicoding.storyapplintang.loginApp.di.Injection

class LoginViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val loginPreferences: LoginPreference
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository, loginPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null
        fun getInstance(
            loginPreferences: LoginPreference
        ): LoginViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LoginViewModelFactory(
                  Injection.provideUserRepository()
                    ,loginPreferences
                )
            }
    }
}