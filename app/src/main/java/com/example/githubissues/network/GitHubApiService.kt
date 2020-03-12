package com.example.githubissues.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GitHubApiService {

    private const val BASE_URL = "https://api.github.com"

    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun gitHubApiService() = getRetrofit().create(GithubApi::class.java)
}