package com.example.githubissues.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_issue.*

class IssueFragment : Fragment() {

    private lateinit var viewModel: IssueViewModel
    private lateinit var adapter: IssueAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_issue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setRecyclerView()
        setSwipeRefreshListener()

        viewModel = ViewModelProvider(this).get(IssueViewModel::class.java)

        if (savedInstanceState == null){
            viewModel.fetchIssues()
        }

        subscribeObservers()
    }

    private fun setRecyclerView() {
        adapter = IssueAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.hasFixedSize()
        recyclerView.adapter = adapter
    }

    private fun setSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchIssues()
        }
    }

    private fun subscribeObservers() {
        viewModel.issuesLiveData.observe(viewLifecycleOwner, Observer<List<Issue>> {
            swipeRefreshLayout.isRefreshing = false
            if (it.isEmpty()){
                showMessage("Список проблем пуст")
            } else {
                adapter.setItems(it)
            }
        })
        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            swipeRefreshLayout.isRefreshing = it
        })
        viewModel.errorLiveData.observe(viewLifecycleOwner, Observer<String> {
            swipeRefreshLayout.isRefreshing = false
            showMessage(it)
        })
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            view!!,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
