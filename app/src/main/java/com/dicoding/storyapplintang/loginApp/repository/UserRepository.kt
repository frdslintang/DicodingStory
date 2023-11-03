package com.dicoding.storyapplintang.loginApp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.storyapplintang.loginApp.data.remote.ApiService
import com.dicoding.storyapplintang.loginApp.data.remote.LoginResponse
import com.dicoding.storyapplintang.loginApp.data.remote.LoginResult
import com.dicoding.storyapplintang.loginApp.data.remote.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.dicoding.storyapplintang.loginApp.utils.Result
import com.dicoding.storyapplintang.loginApp.utils.Result.*

class UserRepository private constructor(
    private val apiService: ApiService

) {
    private val loginResult = MediatorLiveData<Result<LoginResult>>()


    fun login(email: String, password: String): MediatorLiveData<Result<LoginResult>> {
        loginResult.value = Result.Loading

        val client = apiService.login(email, password)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginInfo = response.body()
                        if (loginInfo != null) {
                            loginResult.value = Result.Success(loginInfo.loginResult)
                        } else {
                            loginResult.value = Error("Login Info is null")
                            Log.e(TAG, "Failed: Login Info is null")
                        }
                    } else {
                        loginResult.value = Error(LOGIN_ERROR_MESSAGE)
                        Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                    }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Error(t.message.toString())
            }
        })

        return loginResult
    }




    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> {
        val registerResult = MediatorLiveData<Result<RegisterResponse>>()
        registerResult.value = Result.Loading

        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerInfo = response.body()
                    if (registerInfo != null) {
                        registerResult.value = Result.Success(registerInfo)
                    } else {
                        registerResult.value = Result.Error(REGISTER_ERROR_MESSAGE)
                        Log.e(TAG, "Failed: Login Info is null")
                    }
                } else {
                    registerResult.value = Result.Error(REGISTER_ERROR_MESSAGE)
                    Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                }

            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(REGISTER_ERROR_MESSAGE)
                Log.e(TAG, "Failed: Response Failure - ${t.message.toString()}")
            }
        })

        return registerResult
    }


    companion object {
        private val TAG = UserRepository::class.java.simpleName
        private const val LOGIN_ERROR_MESSAGE = "Login gagal, silakan coba lagi."
        private const val REGISTER_ERROR_MESSAGE = "Pendaftaran gagal, silakan coba lagi."

        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }

}






