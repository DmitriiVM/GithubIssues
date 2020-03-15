package com.example.githubissues.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubissues.network.GitHubApiService
import com.example.githubissues.pojo.Issue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IssueViewModel : ViewModel() {

    private val _issuesLiveData = MutableLiveData<List<Issue>>()
    val issuesLiveData: LiveData<List<Issue>>
        get() = _issuesLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData

    fun fetchIssues() {
        _loadingLiveData.value = true

        GitHubApiService.gitHubApiService()
            .getIssues(OWNER, REPO, STATE).enqueue(object : Callback<List<Issue>> {

                override fun onFailure(call: Call<List<Issue>>, t: Throwable) {
                    _loadingLiveData.postValue(false)
                    _errorLiveData.postValue(t.message)
                }

                override fun onResponse(call: Call<List<Issue>>, response: Response<List<Issue>>) {
                    _loadingLiveData.postValue(false)

                    if (response.isSuccessful) {
                        _issuesLiveData.postValue(response.body())
                    } else {
                        _errorLiveData.postValue(response.message())
                    }
                }
            })
    }

    companion object {
        private const val OWNER = "square"
//        private const val OWNER = "DmitriiVM"
        private const val REPO = "retrofit"
//        private const val REPO = "GithubIssues"

        private const val STATE = "open"
    }
}