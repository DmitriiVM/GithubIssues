package com.example.githubissues.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.githubissues.backgroundwork.BackgroundWorker
import com.example.githubissues.data.database.GitHubDatabase
import com.example.githubissues.data.network.GitHubApiService
import com.example.githubissues.pojo.Issue
import com.example.githubissues.util.*
import com.example.githubissues.util.OWNER
import com.example.githubissues.util.REPO
import com.example.githubissues.util.STATE_OPEN
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class IssueViewModel(context: Context) : ViewModel() {

    init {
        runWorkManager(context)
    }

    private var database = GitHubDatabase.getInstance(context.applicationContext).gitHubDao()
    var isDataLoaded = false

    private val _messageLiveData = MutableLiveData<String>()
    val messageLiveData: LiveData<String>
        get() = _messageLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData

    fun getDatabaseLiveData(): LiveData<List<Issue>> {
        return database.getIssueList()
    }

    fun fetchDataFromNetwork() {
        isDataLoaded = true
        _loadingLiveData.value = true

        GitHubApiService.gitHubApiService()
            .getIssues(OWNER, REPO, STATE_ALL).enqueue(object : Callback<List<Issue>> {

                override fun onFailure(call: Call<List<Issue>>, t: Throwable) {
                    _loadingLiveData.value = false
                    _messageLiveData.value = t.message
                }

                override fun onResponse(call: Call<List<Issue>>, response: Response<List<Issue>>) {
                    _loadingLiveData.value = false
                    if (response.isSuccessful) {
                        response.body()?.let { issueList ->

                            AppExecutors.diskIO.execute {
                                database.apply {
                                    deleteAllIssues()
                                    insertIssueList(issueList)
                                }
                            }
                            if (issueList.isEmpty()) {
                                _messageLiveData.value = "Список проблем пуст"
                            }
                        }
                    } else {
                        _messageLiveData.postValue(response.message())
                    }
                }
            })
    }

    private fun runWorkManager(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workerRequest = PeriodicWorkRequestBuilder<BackgroundWorker>(
            REFRESH_INTERVAL_IN_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(workerRequest)
    }

    companion object {
        private const val REFRESH_INTERVAL_IN_MINUTES = 15L
    }
}