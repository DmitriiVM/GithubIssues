package com.example.githubissues.data.network

import com.example.githubissues.pojo.Issue
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("/repos/{owner}/{repo}/issues?per_page=100")
    fun getIssues(
        @Path("owner") owner : String,
        @Path("repo") repo : String,
        @Query("state") state : String
    ): Call<List<IssueResponse>>
}