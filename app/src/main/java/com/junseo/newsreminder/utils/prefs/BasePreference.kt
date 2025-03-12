package com.junseo.newsreminder.utils.prefs

import com.junseo.newsreminder.JSApplication
import com.junseo.newsreminder.common.CommonInfo
import com.msinfotech.delivery.utils.prefs.SimplePrefs

class BasePreference : SimplePrefs(JSApplication.INSTANCE, CommonInfo.PACKAGE_NAME) {
    companion object {
        @Volatile private var instance: BasePreference? = null

        @JvmStatic fun getInstance(): BasePreference =
            instance ?: synchronized(this) {
                instance ?: BasePreference().also {
                    instance = it
                }
            }
    }
}