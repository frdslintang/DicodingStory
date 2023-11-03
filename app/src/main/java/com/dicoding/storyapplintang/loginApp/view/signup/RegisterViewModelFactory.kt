package com.dicoding.storyapplintang.loginApp.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapplintang.loginApp.repository.UserRepository
import com.dicoding.storyapplintang.loginApp.di.Injection

class RegisterViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: RegisterViewModelFactory? = null

        fun getInstance(): RegisterViewModelFactory =
            (instance ?: synchronized(this) {
                instance ?: RegisterViewModelFactory(Injection.provideUserRepository())
            }).also { instance = it }
    }
}


