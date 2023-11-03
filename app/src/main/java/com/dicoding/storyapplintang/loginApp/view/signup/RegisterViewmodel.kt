package com.dicoding.storyapplintang.loginApp.view.signup

import androidx.lifecycle.ViewModel
import com.dicoding.storyapplintang.loginApp.repository.UserRepository


class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        userRepository.register(name, email, password)
}
