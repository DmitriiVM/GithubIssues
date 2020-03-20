package com.example.githubissues.util

import androidx.recyclerview.widget.DiffUtil
import com.example.githubissues.pojo.Issue

class IssueDiffUtilCallback(
    private val oldList: List<Issue>,
    private val newList: List<Issue>
) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size
}