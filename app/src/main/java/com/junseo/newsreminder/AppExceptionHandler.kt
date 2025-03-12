package com.junseo.newsreminder

import com.junseo.newsreminder.utils.log.MLog
import kotlin.system.exitProcess

class AppExceptionHandler(delegate: Thread.UncaughtExceptionHandler) : Thread.UncaughtExceptionHandler{

    private val TAG = "AppExceptionHandler"

    var mDelegate: Thread.UncaughtExceptionHandler? = delegate

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if(BuildConfig.LOGFILE) {
            MLog.WriteLog(TAG, "강제 종료 Uncaught exception: ${ex.message}")
        } else {
            MLog.e(TAG, "강제 종료 Uncaught exception: ${ex.message}")
        }

        if(mDelegate != null) {
            mDelegate?.uncaughtException(thread, ex)
        } else {
            exitProcess(1)
        }
    }
}