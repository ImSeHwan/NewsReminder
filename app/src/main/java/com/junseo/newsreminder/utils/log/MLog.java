package com.junseo.newsreminder.utils.log;

import android.util.Log;

import com.junseo.newsreminder.BuildConfig;

/**
 * Fitness 사용 로그
 * 로그캣 노출을 피하기 위해 꼭 MLog를 사용한다.
 */
public class MLog {

	public static void d(String tag, String msg) {
		if (BuildConfig.LOG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (BuildConfig.LOG) {
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (BuildConfig.LOG) {
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (BuildConfig.LOG) {
			Log.v(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (BuildConfig.LOG) {
			Log.w(tag, msg);
		}
	}

	public static void WriteLog(final String tag, final String msg) {
		//if (BuildConfig.LOGFILE && BuildConfig.DEBUG) {
		if (BuildConfig.LOGFILE) {
			LogHelper.WriteLog(tag, msg);
		} else {
			if (BuildConfig.LOG) {
				Log.i(tag, msg);
			}
		}
	}

}
