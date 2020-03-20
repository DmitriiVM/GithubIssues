package com.example.githubissues.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.example.githubissues.util.IssueDiffUtilCallback
import kotlinx.android.synthetic.main.issue_item.view.*

class IssueAdapter(
    var selectedIssue: Int,
    private val showSelection: Boolean
) : RecyclerView.Adapter<IssueAdapter.GitHubViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(selectedIssue: Int, issueId : Int)
    }

    private val issueList = arrayListOf<Issue>()

    private val listeners = arrayListOf<OnItemClickListener>()

    fun addListener(onItemClickListener: OnItemClickListener) {
        listeners.add(onItemClickListener)
    }

    fun addItems(newIssueList: List<Issue>) {
        Log.d("mmm", "IssueAdapter :  addItems --  ")
        val diffResult = DiffUtil.calculateDiff(
            IssueDiffUtilCallback(issueList, newIssueList)
        )
        diffResult.dispatchUpdatesTo(this)
        issueList.clear()
        issueList.addAll(newIssueList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubViewHolder {
        return GitHubViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.issue_item, parent, false)
        )
    }

    override fun getItemCount() = issueList.size

    override fun onBindViewHolder(holder: GitHubViewHolder, position: Int) {
        if (showSelection) {
            holder.itemView.isSelected = selectedIssue == position
        }
        val issue = issueList[position]
        holder.onBind(issue)
    }

    inner class GitHubViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun onBind(issue: Issue) {

            view.textViewTitle.text = issue.title

            view.setOnClickListener {

                if (showSelection) {
                    notifyItemChanged(selectedIssue)
                    selectedIssue = adapterPosition
                    itemView.isSelected = true
                    notifyItemChanged(adapterPosition)
                }

                listeners.forEach {
                    it.onItemClicked(adapterPosition, issue.id)
                }
            }
        }
    }
}