package com.dicoding.storyapplintang.loginApp.data

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)

