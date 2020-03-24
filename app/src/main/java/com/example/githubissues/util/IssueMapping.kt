package com.example.githubissues.util

import com.example.githubissues.data.database.IssueDbModel
import com.example.githubissues.data.network.IssueResponse
import com.example.githubissues.pojo.Issue

fun IssueResponse.toIssueDbModel() = IssueDbModel(
    body, closedAt, comments, createdAt, id, number, repositoryUrl, state, title, updatedAt, url
)

fun List<IssueResponse>.toIssueDbModelList() : List<IssueDbModel> {
    val issueDbModelList = arrayListOf<IssueDbModel>()
    forEach {
        issueDbModelList.add(it.toIssueDbModel())
    }
    return issueDbModelList
}

fun IssueDbModel.toIssue() = Issue(
    body, closedAt, comments, createdAt, id, number, repositoryUrl, state, title, updatedAt, url
)

fun List<IssueDbModel>.toIssueList() : List<Issue> {
    val issueList = arrayListOf<Issue>()
    forEach {
        issueList.add(it.toIssue())
    }
    return issueList
}