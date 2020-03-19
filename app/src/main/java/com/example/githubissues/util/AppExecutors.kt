package com.example.githubissues.util

import java.util.concurrent.Executor
import java.util.concurrent.Executors

object AppExecutors {

    val diskIO = DiskIOThreadExecutors()

    class DiskIOThreadExecutors : Executor {
        private val diskIO = Executors.newSingleThreadExecutor()
        override fun execute(command: Runnable) {
            diskIO.execute(command)
        }
    }
}