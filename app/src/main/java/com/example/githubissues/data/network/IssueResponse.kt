package com.example.githubissues.data.network

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

class IssueResponse (
    val body: String?,
    @SerializedName("closed_at")
    val closedAt: String?,
    val comments: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @PrimaryKey
    val id: Int,
    val number: Int?,
    @SerializedName("repository_url")
    val repositoryUrl: String?,
    val state: String?,
    val title: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val url: String?
)