package com.example.githubissues.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.githubissues.pojo.Issue

@Dao
interface GitHubDao {

    @Query("SELECT * FROM issue ORDER BY created_at DESC ")
    fun getIssueList() : LiveData<List<Issue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIssueList(issue: List<Issue>)

    @Query("DELETE FROM issue")
    fun deleteAllIssues()
}