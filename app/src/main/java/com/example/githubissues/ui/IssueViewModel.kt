package com.example.githubissues.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.githubissues.backgroundwork.BackgroundWorker
import com.example.githubissues.data.database.GitHubDatabase
import com.example.githubissues.data.network.GitHubApiService
import com.example.githubissues.pojo.Issue
import com.example.githubissues.util.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class IssueViewModel(context: Context) : ViewModel() {

    init {
        runWorkManager(context)
    }

    private var database = GitHubDatabase.getInstance(context.applicationContext).gitHubDao()

    private val _messageLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _messageLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData

    fun getLiveData(): LiveData<List<Issue>> = database.getIssueList()

    fun fetchDataFromNetwork() {

        _loadingLiveData.value = true

        GitHubApiService.gitHubApiService()
            .getIssues(OWNER, REPO, STATE).enqueue(object : Callback<List<Issue>> {

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

//        private const val OWNER = "square"
//        private const val REPO = "retrofit"
        const val STATE = "open"

        const val OWNER = "DmitriiVM"
        const val REPO = "Test"
    }
}