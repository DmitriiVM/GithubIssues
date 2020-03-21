package com.example.githubissues.pojo

import com.google.gson.annotations.SerializedName

data class Issue(
    val body: String,
    @SerializedName("closed_at")
    val closedAt: Any,
    val comments: Int,
    @SerializedName("created_at")
    val createdAt: String,
    val id: Int,
    val number: Int,
    @SerializedName("repository_url")
    val repositoryUrl: String,
    val state: String,
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val url: String
)