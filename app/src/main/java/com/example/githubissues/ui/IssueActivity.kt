package com.example.githubissues.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.githubissues.R

class IssueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, IssueFragment())
                .commit()
        }
    }
}
