package com.dicoding.storyapplintang.loginApp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.storyapplintang.loginApp.data.local.Room.DaoStory
import com.dicoding.storyapplintang.loginApp.data.local.Room.Entity
import com.dicoding.storyapplintang.loginApp.data.remote.ApiService
import com.dicoding.storyapplintang.loginApp.data.remote.story.AddStoryResponse
import com.dicoding.storyapplintang.loginApp.data.remote.story.Executor
import com.dicoding.storyapplintang.loginApp.data.remote.story.ListStoryItem
import com.dicoding.storyapplintang.loginApp.data.remote.story.PostResponse
import com.dicoding.storyapplintang.loginApp.data.remote.story.StoryResponse
import com.dicoding.storyapplintang.loginApp.utils.Result
import com.dicoding.storyapplintang.loginApp.utils.Result.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val daoStory: DaoStory,
    private val executor: Executor
) {
    private val getStoriesResult = MediatorLiveData<Result<List<Entity>>>()
    private val postStoryResult = MediatorLiveData<Result<AddStoryResponse>>()
    private val storyWithLocationResult = MediatorLiveData<Result<List<ListStoryItem>>>()



    fun getMyStory(token: String): LiveData<Result<List<Entity>>> {
        getStoriesResult.value = Loading
        val client = apiService.getAllStories(token)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val story = response.body()?.listStory
                    val storyList = ArrayList<Entity>()

                    executor.diskIO.execute {
                        story?.forEach {
                            val stories = Entity(
                                it.photoUrl,
                                it.createdAt,
                                it.name,
                                it.description,
                                it.lon,
                                it.id,
                                it.lat
                            )
                            storyList.add(stories)
                        }
                        daoStory.deleteAll()
                        daoStory.insertStory(storyList)
                    }
                } else {
                    Log.e(TAG, "Failed: Get stories response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                getStoriesResult.value= Error(t.message.toString())
                Log.e(TAG, "Failed: Get stories response failure = ${t.message.toString()}")
            }
        })

        val data = daoStory.getAllStory()
        getStoriesResult.addSource( data) {
            getStoriesResult.value = Success(it)
        }

        return getStoriesResult
    }

    fun postMyStory(token: String, imageFile: File, description: String): MediatorLiveData<Result<AddStoryResponse>> {
        postStoryResult.postValue(Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaTypeOrNull()

        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            imageFile.asRequestBody(imageMediaType)
        )
        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)

        val client = apiService.postAllStory(token, imageMultiPart, descriptionRequestBody)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseInfo = response.body()
                    if (responseInfo != null) {
                        postStoryResult.postValue(Success(responseInfo))
                    } else {
                        postStoryResult.postValue(Error("Story Tidak Ditemukan"))
                        Log.e(TAG, "Gagal : Info tidak ditemukan")
                    }
                } else {
                    postStoryResult.postValue(Error("Posting Gagall"))
                    Log.e(TAG, "Gagal : Respon Story Tidak Ditemukan - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                postStoryResult.postValue(Error("Kegagalan respons posting"))
                Log.e(TAG, "Gagal: kegagalan respons postingan cerita - ${t.message.toString()}")
            }
        })

        return postStoryResult
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<List<ListStoryItem>>> {
        storyWithLocationResult.value = Result.Loading
        val finalToken = "Bearer $token"

        try {
            val client = apiService.getStoriesWithLocation(finalToken)
            client.enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                    if (response.isSuccessful) {
                        val listStory = response.body()?.listStory
                        storyWithLocationResult.value = Result.Success(listStory ?: emptyList())
                    } else {
                        storyWithLocationResult.value = Result.Error("Error fetching stories")
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    storyWithLocationResult.value = Result.Error(t.message.toString())
                }
            })
        } catch (e: Exception) {
            storyWithLocationResult.value = Result.Error(e.message.toString())
        }

        return storyWithLocationResult
    }



    companion object {
        private val TAG = StoryRepository::class.java.simpleName

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            daoStory: DaoStory,
            executor: Executor
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, daoStory, executor)
            }.also { instance = it }
    }
}

