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
            if (isActivityRestored == null) {
                Log.d("mmm", "IssueFragment :  onCreate --  1")
                viewModel.fetchIssues()
            } else {
                if (!isActivityRestored && issueId != null) {
                    Log.d("mmm", "IssueFragment :  onCreate --  2")
                    viewModel.fetchIssues()
                } else {
                    Log.d("mmm", "IssueFragment :  onCreate --  3    $issueId")
                    if (issueId == -1) {
                        viewModel.fetchIssues()
                        Log.d("mmm", "IssueFragment :  onCreate --  4")
                    }
                }
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

        setRecyclerView()
        setSwipeRefreshListener()

        subscribeObservers()
    }


    private fun setRecyclerView() {
        adapter = IssueAdapter(requireActivity() as IssueAdapter.OnItemClickListener)
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
            if (it.isEmpty()) {
                showMessage("Список проблем пуст")
            } else {
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    && (issueId == 0)
                ) {
                    (activity as IssueActivity).onItemClicked(it[0].id)
                }
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

    companion object {

        private const val KEY_ISSUE_FRAGMENT = "issue_fragment_key"
        private const val KEY_ISSUE_ID_FRAGMENT = "issue_detail_fragment_key"

        fun newInstance(id: Int?, isRestored: Boolean): Fragment {
            val fragment = IssueFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(KEY_ISSUE_FRAGMENT, isRestored)
                id?.let {
                    putInt(KEY_ISSUE_ID_FRAGMENT, id)
                }
            }
            return fragment
        }
    }


}
