package com.example.githubissues.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "issue")
data class Issue(
    val body: String,
    @SerializedName("closed_at")
    @ColumnInfo(name = "closed_at")
    val closedAt: Any,
    val comments: Int,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @PrimaryKey
    val id: Int,
    val number: Int,
    @SerializedName("repository_url")
    @ColumnInfo(name = "repository_url")
    val repositoryUrl: String,
    val state: String,
    val title: String,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,
    val url: String
)