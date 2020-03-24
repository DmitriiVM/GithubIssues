package com.example.githubissues.pojo

data class Issue(
    val body: String?,
    val closedAt: String?,
    val comments: Int?,
    val createdAt: String?,
    val id: Int,
    val number: Int?,
    val repositoryUrl: String?,
    val state: String?,
    val title: String?,
    val updatedAt: String?,
    val url: String?
)