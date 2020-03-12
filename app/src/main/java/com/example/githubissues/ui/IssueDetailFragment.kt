package com.example.githubissues.ui


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.githubissues.R
import com.example.githubissues.pojo.Issue
import kotlinx.android.synthetic.main.fragment_issue.*
import kotlinx.android.synthetic.main.fragment_issue_detail.*

class IssueDetailFragment : Fragment() {


    private var issueId : Int? = null
    private lateinit var viewModel: IssueViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_issue_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        issueId = arguments?.getInt(KEY_ISSUE_DETAIL_FRAGMENT)

        viewModel = ViewModelProvider(requireActivity()).get(IssueViewModel::class.java)

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.issuesLiveData.observe(viewLifecycleOwner, Observer<List<Issue>> {issueList ->

            issueList.forEach {
                if (it.id == issueId){
                    textViewNumber.text = it.number.toString()
                    textViewTitle.text = it.title
                    textViewBody.text = it.body
                }
            }
        })
    }


    companion object {

        private const val KEY_ISSUE_DETAIL_FRAGMENT = "issue_detail_fragment_key"

        fun newInstance(id: Int) : Fragment {
            val fragment = IssueDetailFragment()
            fragment.arguments = Bundle().apply {
                putInt(KEY_ISSUE_DETAIL_FRAGMENT, id)
            }
            return fragment
        }
    }


}
