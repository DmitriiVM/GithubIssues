package com.example.githubissues.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.example.githubissues.util.IssueViewModelFactory
import kotlinx.android.synthetic.main.fragment_issue_detail.*

class IssueDetailFragment : Fragment(R.layout.fragment_issue_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getInt(KEY_ISSUE_DETAIL_FRAGMENT)?.let {
            subscribeObservers(it)
        }
    }

    private fun subscribeObservers(selectedIssue: Int) {
        val viewModelFactory = IssueViewModelFactory(requireContext())
        val viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(IssueViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<List<Issue>> { issueList ->
            textViewNumber.text = issueList[selectedIssue].number.toString()
            textViewTitle.text = issueList[selectedIssue].title
            textViewBody.text = issueList[selectedIssue].body
        })
    }

    companion object {

        private const val KEY_ISSUE_DETAIL_FRAGMENT = "issue_detail_fragment_key"

        fun newInstance(id: Int): Fragment {
            val fragment = IssueDetailFragment()
            fragment.arguments = Bundle().apply {
                putInt(KEY_ISSUE_DETAIL_FRAGMENT, id)
            }
            return fragment
        }
    }
}
