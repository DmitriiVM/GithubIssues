package com.example.githubissues.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import kotlinx.android.synthetic.main.issue_item.view.*

class IssueAdapter(
    var selectedIssue: Int,
    private val showSelection: Boolean
) : RecyclerView.Adapter<IssueAdapter.GitHubViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(selectedIssue: Int)
    }

    private val issueList = arrayListOf<Issue>()

    private val listeners = arrayListOf<OnItemClickListener>()

    fun addListener(onItemClickListener: OnItemClickListener) {
        listeners.add(onItemClickListener)
    }

    fun addItems(newIssueList: List<Issue>) {
        issueList.clear()
        issueList.addAll(newIssueList)
        notifyDataSetChanged()
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
                    it.onItemClicked(adapterPosition)
                }
            }
        }
    }
}