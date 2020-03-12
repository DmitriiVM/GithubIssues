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
    private var selectedPosition: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)


        if (savedInstanceState == null) {
            addIssueListFragment()
        } else {
            isRestored = true
            issueId = savedInstanceState.getInt(KEY_ISSUE_ID)
            selectedPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION)
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
//        Log.d("mmm", "IssueActivity :  addIssueListFragment --  $selectedPosition")
//        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueFragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, IssueFragment.newInstance(id, isRestored, selectedPosition))
                .commit()
//        }
    }

    override fun onItemClicked(id: Int, selectedPosition: Int) {
        issueId = id
        this.selectedPosition = selectedPosition
//        Log.d("mmm", "IssueActivity :  onItemClicked --  $id")
        addDetailFragment()
    }

    private fun addDetailFragment() {
        issueId?.let {

            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                isDetailFragmentOpen = true
                val displayHomeAsUpEnabled = supportActionBar?.setDisplayHomeAsUpEnabled(true)

                if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueDetailFragment) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, IssueDetailFragment.newInstance(it))
                        .addToBackStack("test")
                        .commit()
                } else {

                }

            } else {
//                if (supportFragmentManager.findFragmentById(R.id.fragmentContainerDetail) !is IssueDetailFragment){
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerDetail, IssueDetailFragment.newInstance(it))
                        .commit()
//                } else {
//
//                }

            }
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        issueId?.let {
            outState.putInt(KEY_ISSUE_ID, issueId!!)
        }
        outState.putBoolean(KEY_IS_DETAIL_FRAGMENT_OPEN, isDetailFragmentOpen)
        outState.putInt(KEY_SELECTED_POSITION, selectedPosition)
        super.onSaveInstanceState(outState)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        issueId = null
        isDetailFragmentOpen = false
        selectedPosition = 0
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onBackPressed()
    }

    companion object {
        private const val KEY_ISSUE_ID = "key_issue_id"
        private const val KEY_IS_DETAIL_FRAGMENT_OPEN = "key_is_detail_fragment_open"


        private const val KEY_SELECTED_POSITION = "key_selected_position"
    }
}
