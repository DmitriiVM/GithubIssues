package com.example.githubissues.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.example.githubissues.util.IssueViewModelFactory
import com.example.githubissues.util.STATE_ALL
import com.example.githubissues.util.STATE_CLOSED
import com.example.githubissues.util.STATE_OPEN
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_issue.*

class IssueFragment : Fragment(R.layout.fragment_issue), IssueAdapter.OnItemClickListener {

    private lateinit var viewModel: IssueViewModel
    private lateinit var adapter: IssueAdapter
    private var selectedIssue = 0
    private var isBeforeLoadFromInternet = true
    private var issueState = STATE_ALL
    private var issueList = arrayListOf<Issue>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        issueState = savedInstanceState?.getString(KEY_STATE) ?: STATE_ALL

        val viewModelFactory = IssueViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(IssueViewModel::class.java)
//        viewModel.fetchDataFromNetwork()

        selectedIssue = savedInstanceState?.getInt(KEY_ISSUE_POSITION)
            ?: (arguments?.getInt(KEY_ISSUE_POSITION) ?: 0)

        if (savedInstanceState != null) {
            isBeforeLoadFromInternet = false
        }

        setRecyclerView()
        setSwipeRefreshListener()
        subscribeObservers()

        setRadioGroup()
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
            swipeRefreshLayout.isRefreshing = true
            viewModel.fetchDataFromNetwork()
        }
    }

    private fun setRadioGroup() {
        radioButtonGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonAll -> issueState = STATE_ALL
                R.id.radioButtonOpen -> issueState = STATE_OPEN
                R.id.radioButtonClosed -> issueState = STATE_CLOSED
            }
            showIssues()
        }
    }

    private fun subscribeObservers() {
        swipeRefreshLayout.isRefreshing = true
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<List<Issue>> { issueList ->


            if (!isBeforeLoadFromInternet && issueList.isEmpty()) {
                showMessage(getString(R.string.message_empty_list))
            }
            this.issueList = issueList as ArrayList<Issue>
            showIssues()
            isBeforeLoadFromInternet = false
        })
        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            swipeRefreshLayout.isRefreshing = it
        })
        viewModel.errorLiveData.observe(viewLifecycleOwner, Observer<String> {
            showMessage(it)
        })
    }

    private fun showIssues() {

        val filteredList = when (issueState) {
            STATE_ALL -> issueList
            STATE_OPEN -> issueList.filter { it.state == STATE_OPEN }
            else -> issueList.filter { it.state == STATE_CLOSED }
        }
        adapter.addItems(filteredList)
    }

    private fun showMessage(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onItemClicked(selectedIssue: Int, issueId: Int) {
        this.selectedIssue = selectedIssue
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_ISSUE_POSITION, selectedIssue)
        outState.putString(KEY_STATE, issueState)
        super.onSaveInstanceState(outState)
    }

    companion object {

        private const val KEY_ISSUE_POSITION = "key_issue_position"
        private const val KEY_STATE = "key_state"

        fun newInstance(id: Int): Fragment {
            val fragment = IssueFragment()
            fragment.arguments = Bundle().apply {
                putInt(KEY_ISSUE_POSITION, id)
            }
            return fragment
        }
    }
}