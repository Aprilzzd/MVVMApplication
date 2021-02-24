package com.example.basemodule.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object AppExecutors{

    private val singleExecutor= Executors.newSingleThreadExecutor()
    private val mainHandler= Handler(Looper.getMainLooper())

    fun singleExecutor(): ExecutorService{
        return singleExecutor
    }

    fun mainHandler():Handler{
        return mainHandler
    }
}