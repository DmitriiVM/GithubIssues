package com.example.githubissues.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_issue.*

class IssueFragment : Fragment(R.layout.fragment_issue){

    private lateinit var viewModel: IssueViewModel
    private lateinit var adapter: IssueAdapter
    private var selectedIssue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(IssueViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        selectedIssue = arguments?.getInt(KEY_ISSUE_DETAIL_FRAGMENT) ?: 0

        setRecyclerView()
        setSwipeRefreshListener()
        subscribeObservers()
    }

    private fun setRecyclerView() {
        adapter = IssueAdapter(selectedIssue)
        if (requireActivity() is IssueAdapter.OnItemClickListener) {
            adapter.addListener(requireActivity() as IssueAdapter.OnItemClickListener)
        }

        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.hasFixedSize()
        recyclerView.adapter = adapter
    }

    private fun setSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            adapter.clearItems()
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



    companion object {

        private const val KEY_ISSUE_DETAIL_FRAGMENT = "issue_detail_fragment_key"

        fun newInstance(id: Int): Fragment {
            val fragment = IssueFragment()
            fragment.arguments = Bundle().apply {
                putInt(KEY_ISSUE_DETAIL_FRAGMENT, id)
            }
            return fragment
        }
    }
}