package com.example.githubissues.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.githubissues.R
import kotlinx.android.synthetic.main.activity_issue.*

class IssueActivity : AppCompatActivity(), IssueAdapter.OnItemClickListener {

    private var selectedIssue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)

        savedInstanceState?.let {
            selectedIssue = it.getInt(KEY_SELECTED_ISSUE)
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }

        val viewModel = ViewModelProvider(this).get(IssueViewModel::class.java)
        if (viewModel.issuesLiveData.value == null) {
            viewModel.fetchIssues()
        }

        if (fragmentContainerDetail == null) {
//            if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueFragment) {
                addFragment(IssueFragment.newInstance(selectedIssue), R.id.fragmentContainer)
//            }
        } else {
//            if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueFragment) {
                addFragment(IssueFragment.newInstance(selectedIssue), R.id.fragmentContainer)
//            }

            addFragment(IssueDetailFragment.newInstance(selectedIssue), R.id.fragmentContainerDetail)
        }
    }

    private fun addFragment(fragment: Fragment, container: Int) {
        supportFragmentManager.beginTransaction()
            .replace(container, fragment)
            .commit()
    }

    override fun onItemClicked(selectedIssue: Int) {
        this.selectedIssue = selectedIssue
        if (fragmentContainerDetail == null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right_to_left, R.anim.exit_from_right_to_left,
                    R.anim.enter_from_left_to_right, R.anim.exit_from_left_ti_right
                )
                .replace(R.id.fragmentContainer, IssueDetailFragment.newInstance(selectedIssue))
                .addToBackStack(null)
                .commit()
        } else {
            addFragment(IssueDetailFragment.newInstance(selectedIssue), R.id.fragmentContainerDetail)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onBackPressed()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_ISSUE, selectedIssue)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val KEY_SELECTED_ISSUE = "key_selected_issue"
    }
}
