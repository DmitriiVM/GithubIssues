package com.example.githubissues.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.githubissues.pojo.Issue

@Database(entities = [Issue::class], version = 2, exportSchema = false)
abstract class GitHubDatabase : RoomDatabase() {

    abstract fun gitHubDao(): GitHubDao

    companion object {

        private const val DB_NAME = "issue_database"

        @Volatile
        private var database: GitHubDatabase? = null

        private val LOCK = Any()

        fun getInstance(context: Context): GitHubDatabase = database ?: synchronized(LOCK) {
            database ?: buildDatabase(context).also {
                database = it
            }
        }

        private fun buildDatabase(context: Context): GitHubDatabase = Room.databaseBuilder(
            context.applicationContext,
            GitHubDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }
}