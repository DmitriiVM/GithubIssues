package com.example.githubissues.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.githubissues.pojo.Issue

@Dao
interface GitHubDao {

    @Query("SELECT * FROM issue")
    fun getIssueList() : LiveData<List<Issue>>

    @Insert
    fun insertIssueList(issue: List<Issue>)

    @Query("DELETE FROM issue")
    fun deleteAllIssues()
}