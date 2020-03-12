package com.example.githubissues.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.githubissues.R

class IssueActivity : AppCompatActivity(), IssueAdapter.OnItemClickListener {

    private var issueId: Int? = null
    private var isRestored = false
    private var isDetailFragmentOpen = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)


        if (savedInstanceState == null) {
            addIssueListFragment()
        } else {
            isRestored = true
            issueId = savedInstanceState.getInt(KEY_ISSUE_ID)
            isDetailFragmentOpen = savedInstanceState.getBoolean(KEY_IS_DETAIL_FRAGMENT_OPEN)
            addFragments()
        }

    }

    fun addFragments() {
        addIssueListFragment()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE || isDetailFragmentOpen) {
            addDetailFragment()
        }
    }

    private fun addIssueListFragment() {
        var id = 0
        issueId?.let {
            id = it
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, IssueFragment.newInstance(id, isRestored))
            .commit()
    }

    override fun onItemClicked(id: Int) {
        issueId = id
        addDetailFragment()
    }

    private fun addDetailFragment() {
        issueId?.let{

            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                isDetailFragmentOpen = true
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, IssueDetailFragment.newInstance(it))
                    .addToBackStack("test")
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerDetail, IssueDetailFragment.newInstance(it))
                    .commit()
            }
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        issueId?.let {
            outState.putInt(KEY_ISSUE_ID, issueId!!)
        }
        outState.putBoolean(KEY_IS_DETAIL_FRAGMENT_OPEN, isDetailFragmentOpen)
        super.onSaveInstanceState(outState)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        issueId = null
        isDetailFragmentOpen = false
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onBackPressed()
    }

    companion object {
        private const val KEY_ISSUE_ID = "key_issue_id"
        private const val KEY_IS_DETAIL_FRAGMENT_OPEN = "key_is_detail_fragment_open"
    }
}
