package com.example.githubissues.ui

import android.content.Context
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

class IssueFragment : Fragment(R.layout.fragment_issue), IssueAdapter.OnItemClickListener{

    private lateinit var viewModel: IssueViewModel
    private lateinit var adapter: IssueAdapter
    private lateinit var radioButtonListener : RadioButtonListener
    private var issueList = arrayListOf<Issue>()
    private var selectedIssue = 0
    private var issueState = STATE_ALL
    private var isBeforeLoadFromInternet = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewModelFactory = IssueViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(IssueViewModel::class.java)

        selectedIssue = savedInstanceState?.getInt(KEY_SELECTED_ISSUE)
            ?: arguments?.getInt(KEY_SELECTED_ISSUE) ?: 0
        issueState = savedInstanceState?.getString(KEY_STATE)
            ?: arguments?.getString(KEY_STATE) ?: STATE_ALL

        if (savedInstanceState != null) {
            isBeforeLoadFromInternet = false
        }
        Log.d("mmm", "IssueFragment :  onViewCreated --  ${this.hashCode()}")
        setRecyclerView()
        setSwipeRefreshListener()
        subscribeObservers()
        setRadioGroup()
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        initAdapter(selectedIssue)
    }

    private fun initAdapter(selectedIssue: Int) {
        adapter = IssueAdapter(selectedIssue, isDetailContainerAvailable())
        if (requireActivity() is IssueAdapter.OnItemClickListener) {
            adapter.addListener(requireActivity() as IssueAdapter.OnItemClickListener)
        }
        adapter.addListener(this)
        recyclerView.adapter = adapter
    }

    private fun setSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            viewModel.fetchDataFromNetwork()
        }
    }

    private fun setRadioGroup() {
        setRadioButtonChecked()

        radioButtonGroup.setOnCheckedChangeListener { _, checkedId ->
            setIssueState(checkedId)
            if (isDetailContainerAvailable()){
                initAdapter(0)
                if (filterList().isNotEmpty()){
                    notifyRadioButtonChanged(filterList()[0].id)
                } else {
                    notifyRadioButtonChanged(null)
                }
            } else {
                notifyRadioButtonChanged(0)
            }
            showIssues()
        }
    }

    private fun setRadioButtonChecked() {
        when (issueState) {
            STATE_ALL -> radioButtonGroup.check(R.id.radioButtonAll)
            STATE_OPEN -> radioButtonGroup.check(R.id.radioButtonOpen)
            STATE_CLOSED -> radioButtonGroup.check(R.id.radioButtonClosed)
        }
    }

    private fun setIssueState(checkedId: Int) {
        issueState = when (checkedId) {
            R.id.radioButtonAll -> STATE_ALL
            R.id.radioButtonOpen -> STATE_OPEN
            else -> STATE_CLOSED
        }
    }

    private fun isDetailContainerAvailable() =
        requireActivity().findViewById<View>(R.id.fragmentContainerDetail) != null

    private fun filterList() = when (issueState) {
        STATE_ALL -> issueList
        STATE_OPEN -> issueList.filter { it.state == STATE_OPEN }
        else -> issueList.filter { it.state == STATE_CLOSED }
    }

    private fun notifyRadioButtonChanged(issueId: Int?){
        Log.d(
            "mmm",
            "IssueFragment :  notifyRadioButtonChanged --  ${::radioButtonListener.isInitialized}"
        )
        if (::radioButtonListener.isInitialized) {
            radioButtonListener.onRadioButtonChange(issueId, issueState)
        }
    }

    private fun subscribeObservers() {
        swipeRefreshLayout.isRefreshing = true
        viewModel.getDatabaseLiveData().observe(viewLifecycleOwner, Observer<List<Issue>> { issueList ->
            this.issueList = issueList as ArrayList<Issue>
            showIssues()
            isBeforeLoadFromInternet = false
        })
        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            swipeRefreshLayout.isRefreshing = it
        })
        viewModel.messageLiveData.observe(viewLifecycleOwner, Observer<String> {
            showMessage(it)
        })
    }

    private fun showIssues() {
        if (!isBeforeLoadFromInternet && filterList().isEmpty()) {
            showMessage(getString(R.string.message_empty_list))
        }
        adapter.addItems(filterList())
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
        with(outState){
            putInt(KEY_SELECTED_ISSUE, selectedIssue)
            putString(KEY_STATE, issueState)
        }
        super.onSaveInstanceState(outState)
    }

    interface RadioButtonListener{
        fun onRadioButtonChange(issueId: Int?, issueState : String)
    }

    fun setRadioButtonListener(radioButtonListener : RadioButtonListener){
        Log.d("mmm", "IssueFragment :  setRadioButtonListener --  ")
        this.radioButtonListener = radioButtonListener
    }


    companion object {

        private const val KEY_SELECTED_ISSUE = "key_issue_position"
        private const val KEY_STATE = "key_state"

        fun newInstance(id: Int, issueState: String): Fragment {
            val fragment = IssueFragment()
            fragment.arguments = Bundle().apply {
                putInt(KEY_SELECTED_ISSUE, id)
                putString(KEY_STATE, issueState)
            }
            return fragment
        }
    }
}