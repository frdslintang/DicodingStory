package com.dicoding.storyapplintang.loginApp.data.local.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoStory {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStory(story: List<Entity>)

    @Query("SELECT * FROM tbl_story")
    fun getAllStory(): LiveData<List<Entity>>

    @Query("DELETE FROM tbl_story")
    fun deleteAll()
}
