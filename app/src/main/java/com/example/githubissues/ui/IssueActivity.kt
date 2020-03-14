package com.example.githubissues.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.githubissues.R
import kotlinx.android.synthetic.main.activity_issue.*

class IssueActivity : AppCompatActivity(), IssueAdapter.OnItemClickListener,
    IssueFragment.OnFirstLoadListener, IssueFragment.OnAfterProcessDeathListener {

    private var issueId : Int? = null

    // эту переменную я передаю в фрагмент вот почему - если в портретном режиме, находясь во фрагменте с деталями, я переверну экран,
    // после поворота в этот контейнер мне нужно будет загрузить фрагмент со списком issue.
    // При этом saveInstanceState у нового фрагмента будет равен null. А я каким-то образом хочу понять,
    // нужно ли мне грузить данные с интернета снова, или нет. Поэтому проверяю, восттановлено ли состояние или нет через активити
    private var isRestored = false

    // после смери процесса хочу грузануть данные снова
    private var isAfterProcessDeath = false

    // нужно затем, что, если я в портретном режиме открою фрагмент с деталями,
    // потом переверну в лендскейп, потом обратно, открылся бы снова фрагмент с деталями
    private var isDetailFragmentOpen = false
    // выделенная позиция recycleView
    private var selectedPosition = 0

    private var isFirstLoad = true

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
            isFirstLoad = savedInstanceState.getBoolean(KEY_FIRST_LOAD, true)
            addFragments()
        }
    }

    private fun addFragments() {
        addIssueListFragment()
        if (fragmentContainerDetail != null || isDetailFragmentOpen) {
            addDetailFragment()
        }
    }

    private fun addIssueListFragment() {
        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueFragment || isAfterProcessDeath) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    IssueFragment.newInstance(isRestored, selectedPosition)
                )
                .commit()
        }
    }

    override fun onItemClicked(issueId: Int, selectedPosition: Int) {
        this.issueId = issueId
        this.selectedPosition = selectedPosition
        addDetailFragment()
    }

    private fun addDetailFragment() {
        issueId?.let {
            if (fragmentContainerDetail == null) {

                isDetailFragmentOpen = true
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) !is IssueDetailFragment) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.enter_from_right_to_left,
                            R.anim.exit_from_right_to_left,
                            R.anim.enter_from_left_to_right,
                            R.anim.exit_from_left_ti_right
                        )
                        .replace(R.id.fragmentContainer, IssueDetailFragment.newInstance(it))
                        .addToBackStack(null)
                        .commit()
                } else {
                }

            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerDetail, IssueDetailFragment.newInstance(it))
                    .commit()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        issueId?.let {
            outState.putInt(KEY_ISSUE_ID, it)
        }
        outState.putBoolean(KEY_IS_DETAIL_FRAGMENT_OPEN, isDetailFragmentOpen)
        outState.putInt(KEY_SELECTED_POSITION, selectedPosition)
        outState.putBoolean(KEY_FIRST_LOAD, isFirstLoad)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        isDetailFragmentOpen = false
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onBackPressed()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is IssueFragment) {
            fragment.setOnFirstLoadListener(this)
            fragment.setOnAfterProcessDeathListener(this)
        }
    }

    // если я приложение запускаю в landscape mode, то хочу загрузить детали первого элемента после загрузки данных
    override fun onFirstLoad(issueId: Int) {
        if (isFirstLoad){
            onItemClicked(issueId, 0)
        }
        isFirstLoad = false
    }

    override fun onAfterProcessDeath() {
        isAfterProcessDeath = true
        addFragments()
    }

    companion object {
        private const val KEY_ISSUE_ID = "key_issue_id"
        private const val KEY_IS_DETAIL_FRAGMENT_OPEN = "key_is_detail_fragment_open"
        private const val KEY_SELECTED_POSITION = "key_selected_position_activity"
        private const val KEY_FIRST_LOAD = "key_first_load"
    }
}
