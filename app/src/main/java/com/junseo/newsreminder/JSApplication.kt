package com.junseo.newsreminder

import android.app.Application
import com.junseo.newsreminder.common.CommonInfo
import com.junseo.newsreminder.utils.log.LogHelper
import com.msinfotech.delivery.utils.prefs.SimplePrefs

class JSApplication: Application() {

    lateinit var simplePrefs: SimplePrefs

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        simplePrefs = SimplePrefs(INSTANCE, CommonInfo.PREF_MAIN_KEY)

        // 로그파일 설정(파일 퍼미션이 수락되어 있어야 사용가능합니다.)
        if (BuildConfig.LOGFILE) LogHelper.Configure()

        //에러 핸들러 등록
        setupExceptionHandler()
    }

    companion object {
        lateinit var INSTANCE: JSApplication
    }

    private fun setupExceptionHandler() {
        Thread.getDefaultUncaughtExceptionHandler()?.let { handler ->
            val exceptionHandler = AppExceptionHandler(handler)
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
        }
    }
}