package com.example.githubissues.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import com.example.githubissues.util.IssueViewModelFactory
import kotlinx.android.synthetic.main.activity_issue.*
import kotlinx.android.synthetic.main.fragment_issue_detail.*

class IssueDetailFragment : Fragment(R.layout.fragment_issue_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        Log.d("mmm", "IssueDetailFragment :  onViewCreated --  ")
        arguments?.getInt(KEY_ISSUE_DETAIL_FRAGMENT)?.let {
            subscribeObservers(it)
        }
    }

    private fun subscribeObservers(issueId: Int) {
        val viewModelFactory = IssueViewModelFactory(requireContext())
        val viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(IssueViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<List<Issue>> { issueList ->
//
//
//            if (requireActivity().fragmentContainerDetail != null){
//                Log.d("mmm", "IssueDetailFragment : issueList ${issueList.size} subscribeObservers --  $issueId        ${fragmentContainerDetail == null}      ${requireActivity().fragmentContainerDetail == null}   ")
            Log.d("mmm", "IssueDetailFragment : ${issueList.size} subscribeObservers --   $issueId")
            var id = issueId
            if (id == 0) {
                id = issueList[0].id
            }

            issueList.forEach { issue ->

                if (issue.id == id){
//                    Log.d("mmm", "IssueDetailFragment :  subscribeObservers -- 2 ${issue.id} ${issue.number} ${issue.title}    ${issue.body}"  )
                    textViewNumber.text = issue.number.toString()
                    textViewTitle.text = issue.title
                    textViewBody.text = issue.body
                }
            }

//            }
        })
    }

    companion object {

        private const val KEY_ISSUE_DETAIL_FRAGMENT = "issue_detail_fragment_key"

        fun newInstance(issueId: Int): Fragment {
//            Log.d("mmm", "IssueDetailFragment :  newInstance --  ")
            val fragment = IssueDetailFragment()
            fragment.arguments = Bundle().apply {
                putInt(KEY_ISSUE_DETAIL_FRAGMENT, issueId)
            }
            return fragment
        }
    }
}
