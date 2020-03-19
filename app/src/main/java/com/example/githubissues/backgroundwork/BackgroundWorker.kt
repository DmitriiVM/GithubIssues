package com.example.githubissues.backgroundwork

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.githubissues.data.database.GitHubDatabase
import com.example.githubissues.data.network.GitHubApiService
import com.example.githubissues.pojo.Issue
import com.example.githubissues.ui.IssueViewModel
import com.example.githubissues.util.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BackgroundWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        GitHubApiService.gitHubApiService().getIssues(IssueViewModel.OWNER, IssueViewModel.REPO, IssueViewModel.STATE)
            .enqueue(object : Callback<List<Issue>> {

                override fun onFailure(call: Call<List<Issue>>, t: Throwable) {}

                override fun onResponse(call: Call<List<Issue>>, response: Response<List<Issue>>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            AppExecutors.diskIO.execute {
                                with(GitHubDatabase.getInstance(applicationContext).gitHubDao()){
                                    deleteAllIssues()
                                    insertIssueList(it)
                                }
                            }
                        }
                    }
                }
            })

        return Result.success()
    }
}