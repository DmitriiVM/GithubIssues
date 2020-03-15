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

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }

        val viewModel = ViewModelProvider(this).get(IssueViewModel::class.java)
        if (viewModel.issuesLiveData.value == null) {
            viewModel.fetchIssues()
        }

        if (fragmentContainerDetail == null) {
            if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueFragment) {
                addFragment(IssueFragment(), R.id.fragmentContainer)
            }
        } else {
            if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueFragment) {
                addFragment(IssueFragment(), R.id.fragmentContainer)
            }

            addFragment(IssueDetailFragment.newInstance(selectedIssue), R.id.fragmentContainerDetail)
        }
    }

    private fun addFragment(fragment: Fragment, container: Int) {
        supportFragmentManager.beginTransaction()
            .replace(container, fragment)
            .commit()
    }

    override fun onItemClicked(selectedIssue: Int) {
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
}
