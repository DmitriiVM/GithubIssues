package com.example.githubissues.backgroundwork

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.githubissues.data.database.GitHubDatabase
import com.example.githubissues.data.network.GitHubApiService
import com.example.githubissues.data.network.IssueResponse
import com.example.githubissues.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BackgroundWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        GitHubApiService.gitHubApiService().getIssues(OWNER, REPO, STATE_ALL)
            .enqueue(object : Callback<List<IssueResponse>> {

                override fun onFailure(call: Call<List<IssueResponse>>, t: Throwable) {}

                override fun onResponse(
                    call: Call<List<IssueResponse>>,
                    response: Response<List<IssueResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            AppExecutors.diskIO.execute {
                                with(GitHubDatabase.getInstance(applicationContext).gitHubDao()){
                                    deleteAllIssues()
                                    insertIssueList(it.toIssueDbModelList())
                                }
                            }
                        }
                    }
                }
            })

        return Result.success()
    }
}