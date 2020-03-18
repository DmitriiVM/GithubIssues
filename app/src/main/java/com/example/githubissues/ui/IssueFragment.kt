package com.example.githubissues.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_issue.*

class IssueFragment : Fragment(R.layout.fragment_issue), IssueAdapter.OnItemClickListener {

    private lateinit var viewModel: IssueViewModel
    private lateinit var adapter: IssueAdapter
    private var selectedIssue = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity()).get(IssueViewModel::class.java)

        selectedIssue = savedInstanceState?.getInt(KEY_ISSUE_POSITION)
            ?: (arguments?.getInt(KEY_ISSUE_POSITION) ?: 0)

        setRecyclerView()
        setSwipeRefreshListener()
        subscribeObservers()
    }

    private fun setRecyclerView() {
        val showSelection =
            requireActivity().findViewById<View>(R.id.fragmentContainerDetail) != null
        adapter = IssueAdapter(selectedIssue, showSelection)
        if (requireActivity() is IssueAdapter.OnItemClickListener) {
            adapter.addListener(requireActivity() as IssueAdapter.OnItemClickListener)
        }
        adapter.addListener(this)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun setSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchIssues()
        }
    }

    private fun subscribeObservers() {
        viewModel.issuesLiveData.observe(viewLifecycleOwner, Observer<List<Issue>> { issueList ->
            if (issueList.isEmpty()) {
                showMessage(getString(R.string.message_empty_list))
            } else {
                adapter.addItems(issueList)
            }
        })
        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            swipeRefreshLayout.isRefreshing = it
        })
        viewModel.errorLiveData.observe(viewLifecycleOwner, Observer<String> {
            showMessage(it)
        })
    }

    private fun showMessage(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onItemClicked(selectedIssue: Int) {
        this.selectedIssue = selectedIssue
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_ISSUE_POSITION, selectedIssue)
        super.onSaveInstanceState(outState)
    }

    companion object {

        private const val KEY_ISSUE_POSITION = "issue_issue_position"

        fun newInstance(id: Int): Fragment {
            val fragment = IssueFragment()
            fragment.arguments = Bundle().apply {
                putInt(KEY_ISSUE_POSITION, id)
            }
            return fragment
        }
    }
}