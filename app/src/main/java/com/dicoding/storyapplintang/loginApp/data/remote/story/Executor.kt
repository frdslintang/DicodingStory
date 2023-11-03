package com.dicoding.storyapplintang.loginApp.data.remote.story

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Executor {
    val diskIO: Executor = Executors.newSingleThreadExecutor()
}