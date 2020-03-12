package com.example.githubissues.ui

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import kotlinx.android.synthetic.main.issue_item.view.*

class IssueAdapter(
    private val listener: OnItemClickListener,
    var selectedPosition: Int
) : RecyclerView.Adapter<IssueAdapter.GitHubViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(issueId: Int, selectedPosition: Int)
    }

    private val issueList = arrayListOf<Issue>()

    fun setItems(newIssueList: List<Issue>) {
        issueList.clear()
        issueList.addAll(newIssueList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GitHubViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.issue_item, parent, false),
            listener
        )

    override fun getItemCount() = issueList.size

    override fun onBindViewHolder(holder: GitHubViewHolder, position: Int) {
        if (holder.itemView.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            holder.itemView.isSelected = selectedPosition == position
        }

        val issue = issueList[position]
        holder.onBind(issue)
    }

    inner class GitHubViewHolder(
        private val view: View,
        private val listener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(view) {

        fun onBind(issue: Issue) {

            view.textViewTitle.text = issue.title

            view.setOnClickListener {

                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(adapterPosition)

                listener.onItemClicked(issue.id, adapterPosition)
            }
        }
    }
}