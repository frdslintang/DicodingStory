package com.dicoding.storyapplintang.loginApp.data.local.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Entity::class],
    version = 2,
    exportSchema = false
)
abstract class DatabaseStory : RoomDatabase() {
    abstract fun daoStory(): DaoStory

    companion object {
        private val MIGRATION_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // migrasi
                db.execSQL("CREATE TABLE IF NOT EXISTS `tbl_story` (`photoUrl` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `lon` REAL NOT NULL, `id` TEXT NOT NULL, `lat` REAL NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        @Volatile
        private var instance: DatabaseStory? = null

        fun getInstance(context: Context): DatabaseStory =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, DatabaseStory::class.java, "stories.db")
                    .addMigrations(MIGRATION_2) // Menggunakan migrasi versi 1 ke 2
                    .fallbackToDestructiveMigration()
                    .build()
            }.also { instance = it }
    }
}

