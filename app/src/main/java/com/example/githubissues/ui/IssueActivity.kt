package com.example.githubissues.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.githubissues.R

class IssueActivity : AppCompatActivity(), IssueAdapter.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, IssueFragment())
                .commit()
        }
    }

    override fun onItemClicked(issueId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, IssueDetailFragment.newInstance(issueId))
            .addToBackStack("test")
            .commit()
    }
}
