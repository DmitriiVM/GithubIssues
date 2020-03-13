package com.example.githubissues.network

import com.example.githubissues.pojo.Issue
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("/repos/{owner}/{repo}/issues")
    fun getIssues(
        @Path("owner") owner : String,
        @Path("repo") repo : String,
        @Query("page") page: String
    ): Call<List<Issue>>
}