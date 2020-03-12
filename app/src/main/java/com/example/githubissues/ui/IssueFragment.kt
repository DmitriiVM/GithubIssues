package com.example.githubissues.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_issue.*

class IssueFragment : Fragment() {

    private lateinit var viewModel: IssueViewModel
    private lateinit var adapter: IssueAdapter

    var issueId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_issue, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val isActivityRestored = arguments?.getBoolean(KEY_ISSUE_FRAGMENT)
        issueId = arguments?.getInt(KEY_ISSUE_ID_FRAGMENT)

        viewModel = ViewModelProvider(requireActivity()).get(IssueViewModel::class.java)

        if (savedInstanceState == null) {
            if (isActivityRestored != null && !isActivityRestored) {
                viewModel.fetchIssues()
            }
        } else {
            if (viewModel.issuesLiveData.value == null &&
                viewModel.loadingLiveData.value == null &&
                viewModel.errorLiveData.value == null
            ) {
                (activity as IssueActivity).addFragments()
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val selectedPosition = arguments?.getInt(KEY_SELECTED_POSITION)

        setRecyclerView(selectedPosition ?: 0)
        setSwipeRefreshListener()
        subscribeObservers()
    }


    private fun setRecyclerView(selectedPosition: Int) {
        adapter =
            IssueAdapter(requireActivity() as IssueAdapter.OnItemClickListener, selectedPosition)

        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
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
        viewModel.issuesLiveData.observe(viewLifecycleOwner, Observer<List<Issue>> {issueList ->
            swipeRefreshLayout.isRefreshing = false
            if (issueList.isEmpty()) {
                showMessage(getString(R.string.message_empty_list))
            } else {
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && issueId == 0
                ) {
                    (activity as IssueActivity).onItemClicked(issueList[0].id, 0)
                }
                issueList.filter { it.state == "open" }
                adapter.setItems(issueList)
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
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {

        private const val KEY_ISSUE_FRAGMENT = "issue_fragment_key"
        private const val KEY_ISSUE_ID_FRAGMENT = "issue_detail_fragment_key"
        private const val KEY_SELECTED_POSITION = "selected_position"

        fun newInstance(id: Int?, isRestored: Boolean, selectedPosition: Int): Fragment {
            val fragment = IssueFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(KEY_ISSUE_FRAGMENT, isRestored)
                putInt(KEY_SELECTED_POSITION, selectedPosition)
                id?.let {
                    putInt(KEY_ISSUE_ID_FRAGMENT, id)
                }
            }
            return fragment
        }
    }
}
