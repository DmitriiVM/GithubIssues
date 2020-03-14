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
import androidx.recyclerview.widget.RecyclerView
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_issue.*
import kotlinx.android.synthetic.main.fragment_issue.*

class IssueFragment : Fragment(), IssueAdapter.OnItemClickListener {


    private lateinit var viewModel: IssueViewModel
    private lateinit var adapter: IssueAdapter
    private var page: Int = 1
    private lateinit var layoutManager: LinearLayoutManager
    private var isLoading = false
    private var selectedPosition = 0
    private lateinit var onAfterProcessDeathListener: OnAfterProcessDeathListener
    private lateinit var onFirstLoadListener: OnFirstLoadListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_issue, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val isActivityRestored = arguments?.getBoolean(KEY_ISSUE_FRAGMENT)

        viewModel = ViewModelProvider(requireActivity()).get(IssueViewModel::class.java)

        if (savedInstanceState == null) {
            if (isActivityRestored != null && !isActivityRestored) {
                isLoading = true
                viewModel.fetchIssues(page.toString())
            }
        } else {
            if (viewModel.issuesLiveData.value == null &&
                viewModel.loadingLiveData.value == null &&
                viewModel.errorLiveData.value == null
            ) {
                onAfterProcessDeathListener.onAfterProcessDeath()
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        selectedPosition = if (savedInstanceState?.getInt(KEY_SELECTED_POSITION) != null) {
            savedInstanceState.getInt(KEY_SELECTED_POSITION)

        } else {
            arguments?.getInt(KEY_SELECTED_POSITION) ?: 0
        }

        setRecyclerView(selectedPosition)
        setSwipeRefreshListener()
        subscribeObservers()
    }

    private fun setRecyclerView(selectedPosition: Int) {
        adapter = IssueAdapter(selectedPosition)
        if (requireActivity() is IssueAdapter.OnItemClickListener) {
            adapter.addListener(requireActivity() as IssueAdapter.OnItemClickListener)
        }
        adapter.addListener(this)

        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.hasFixedSize()
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading
                    && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                    && firstVisibleItemPosition >= 0
                ) {
                    page++
                    isLoading = true
                    viewModel.fetchIssues(page.toString())
                }
            }
        })
    }

    private fun setSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            adapter.clearItems()
            isLoading = true
            viewModel.fetchIssues(START_PAGE)
        }
    }

    private fun subscribeObservers() {
        viewModel.issuesLiveData.observe(viewLifecycleOwner, Observer<List<Issue>> { issueList ->

            issueList.filter { it.state == "open" }
            if (page == 1 && issueList.isEmpty()) {
                showMessage(getString(R.string.message_empty_list))
            } else {
                // если приложение запускаю в landscape mode, то хочу загрузить детали первого элемента
                if (requireActivity().fragmentContainerDetail != null) {
                    onFirstLoadListener.onFirstLoad(issueList[0].id)
                }
                adapter.addItems(issueList)
                isLoading = false
            }
        })
        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            swipeRefreshLayout.isRefreshing = it
        })
        viewModel.errorLiveData.observe(viewLifecycleOwner, Observer<String> {
            isLoading = false
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
        private const val KEY_SELECTED_POSITION = "selected_position"
        private const val START_PAGE = "1"

        fun newInstance(isRestored: Boolean, selectedPosition: Int): Fragment {
            val fragment = IssueFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(KEY_ISSUE_FRAGMENT, isRestored)
                putInt(KEY_SELECTED_POSITION, selectedPosition)
            }
            return fragment
        }
    }

    fun setOnFirstLoadListener(onFirstLoadListener: OnFirstLoadListener) {
        this.onFirstLoadListener = onFirstLoadListener
    }

    fun setOnAfterProcessDeathListener(onAfterProcessDeathListener: OnAfterProcessDeathListener) {
        this.onAfterProcessDeathListener = onAfterProcessDeathListener
    }

    interface OnFirstLoadListener {
        fun onFirstLoad(issueId: Int)
    }

    interface OnAfterProcessDeathListener {
        fun onAfterProcessDeath()
    }

    override fun onItemClicked(issueId: Int, selectedPosition: Int) {
        this.selectedPosition = selectedPosition
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_POSITION, selectedPosition)
        super.onSaveInstanceState(outState)
    }
}
