package com.example.githubissues.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GitHubDao {

    @Query("SELECT * FROM issue ORDER BY created_at DESC ")
    fun getIssueList() : LiveData<List<IssueDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIssueList(issue: List<IssueDbModel>)

    @Query("DELETE FROM issue")
    fun deleteAllIssues()
}