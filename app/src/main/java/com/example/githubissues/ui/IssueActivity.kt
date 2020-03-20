package com.example.githubissues.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.githubissues.R
import com.example.githubissues.util.IssueViewModelFactory
import com.example.githubissues.util.STATE_ALL
import kotlinx.android.synthetic.main.activity_issue.*

class IssueActivity : AppCompatActivity(), IssueAdapter.OnItemClickListener,
    IssueFragment.RadioButtonListener {

    private var selectedIssue = 0
    private var issueId = 0
    private var issueState = STATE_ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)

        savedInstanceState?.let {
            selectedIssue = it.getInt(KEY_SELECTED_ISSUE)
            issueId = it.getInt(KEY__ISSUE_ID)
            issueState = it.getString(KEY_STATE) ?: STATE_ALL
        }

        supportFragmentManager.popBackStack(
            BACK_STACK_DETAIL_FRAGMENT,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        val viewModelFactory = IssueViewModelFactory(this)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(IssueViewModel::class.java)
        if (!viewModel.isDataLoaded) {
            viewModel.fetchDataFromNetwork()
        }

        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueFragment) {
            addFragment(R.id.fragmentContainer, IssueFragment.newInstance(selectedIssue, issueState))
        }

        if (fragmentContainerDetail != null) {
            addFragment(R.id.fragmentContainerDetail, IssueDetailFragment.newInstance(issueId))
        }
    }

    private fun addFragment(container: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(container, fragment).commit()
    }

    override fun onResume() {
        (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as IssueFragment)
            .setRadioButtonListener(this)
        super.onResume()
    }

    override fun onItemClicked(selectedIssue: Int, issueId: Int) {
        val previousSelectedIssue = this.selectedIssue
        this.selectedIssue = selectedIssue
        this.issueId = issueId

        if (fragmentContainerDetail == null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right_to_left, R.anim.exit_from_right_to_left,
                    R.anim.enter_from_left_to_right, R.anim.exit_from_left_ti_right
                )
                .replace(R.id.fragmentContainer, IssueDetailFragment.newInstance(issueId))
                .addToBackStack(BACK_STACK_DETAIL_FRAGMENT)
                .commit()
        } else {
            val transaction = supportFragmentManager.beginTransaction()
            if (fragmentContainerDetail != null) {
                if (previousSelectedIssue < selectedIssue) {
                    transaction.setCustomAnimations(R.anim.enter_to_top, R.anim.enter_from_bottom)
                } else if (previousSelectedIssue > selectedIssue) {
                    transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.enter_to_bottom)
                }
            }
            transaction
                .replace(R.id.fragmentContainerDetail, IssueDetailFragment.newInstance(issueId))
                .commit()
        }
    }

    override fun onRadioButtonChange(issueId: Int, issueState: String) {
        this.issueState = issueState
        if (fragmentContainerDetail != null) {
            addFragment(R.id.fragmentContainerDetail, IssueDetailFragment.newInstance(issueId))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        issueId = 0
        selectedIssue = 0

        if (fragmentContainerDetail == null && supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            addFragment(R.id.fragmentContainer, IssueFragment.newInstance(selectedIssue, issueState))
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        with(outState) {
            putInt(KEY_SELECTED_ISSUE, selectedIssue)
            putInt(KEY__ISSUE_ID, issueId)
            putString(KEY_STATE, issueState)
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val KEY_SELECTED_ISSUE = "key_selected_issue"
        private const val KEY__ISSUE_ID = "key_issue_id"
        private const val BACK_STACK_DETAIL_FRAGMENT = "detail_fragment"
        private const val KEY_STATE = "key_state"
    }
}
