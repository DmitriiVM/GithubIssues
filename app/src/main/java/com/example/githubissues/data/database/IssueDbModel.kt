package com.example.githubissues.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issue")
class IssueDbModel (
    val body: String?,
    @ColumnInfo(name = "closed_at")
    val closedAt: String?,
    val comments: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @PrimaryKey
    val id: Int,
    val number: Int?,
    @ColumnInfo(name = "repository_url")
    val repositoryUrl: String?,
    val state: String?,
    val title: String?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    val url: String?
)