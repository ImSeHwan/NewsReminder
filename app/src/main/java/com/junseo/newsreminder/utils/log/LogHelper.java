package com.junseo.newsreminder.utils.log;

import android.os.Build;
import android.os.Environment;

import com.junseo.newsreminder.BuildConfig;
import com.junseo.newsreminder.JSApplication;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 파일로그 클래스
 */
public class LogHelper {

    private static Logger mLog = null;
    private static LogConfigurator _logConfigurator = null;

    private static boolean bLogException = false;

    public static void Configure() {

        try {
            if (BuildConfig.LOGFILE) {

                _logConfigurator = new LogConfigurator();

                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
                String today = DATE_FORMAT.format(new Date());
                String fileName;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {// 11 Migration
                    fileName = JSApplication.INSTANCE.getExternalFilesDir("news_reminder_log") +"/"+today+ ".txt";
                } else {
                    fileName = Environment.getExternalStorageDirectory() + "/news_reminder_log/" + today + ".txt";
                }
//				String filePattern = "%d - [%c] - %p : %m%n";
                String filePattern = "%d : %m%n";
                int maxBackupSize = 10;
                long maxFileSize = 1024 * 1024;

                // set the name of the log file
                _logConfigurator.setFileName(fileName);

                // set output format of the log line
                // see :
                // http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
                _logConfigurator.setFilePattern(filePattern);

                // set immediateFlush = true, if you want output stream will be
                // flushed
                // at the end of each append operation
                // default value is true
                // _logConfigurator.setImmediateFlush(immediateFlush);

                // set output format of the LogCat line
                // see :
                // http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
                // _logConfigurator.setLogCatPattern(logCatPattern);

                // Maximum number of backed up log files
                _logConfigurator.setMaxBackupSize(maxBackupSize);

                // Maximum size of log file until rolling
                _logConfigurator.setMaxFileSize(maxFileSize);

                // set true to appends log events to a file, otherwise set false
                // default value is true
                // _logConfigurator.setUseFileAppender(useFileAppender);

                // set true to appends log events to a LogCat, otherwise set false
                // default value is true
                // _logConfigurator.setUseLogCatAppender(useLogCatAppender);

                // configure
                _logConfigurator.configure();
            }
        } catch (Exception e) {
            bLogException = true;
        }

    }

    public static void error(String TAG, Throwable ex) {
        try {
            if (BuildConfig.LOGFILE && !bLogException) {
                mLog = Logger.getLogger(TAG);
                mLog.error(ex.toString(), ex);
            } else {
                MLog.e(TAG, ex.toString());
            }
        } catch (Exception e) {
            MLog.e(TAG, ex.toString());
        }
    }

    public static void debug(String TAG, Throwable ex) {
        try {
            if (BuildConfig.LOGFILE && !bLogException) {
                mLog = Logger.getLogger(TAG);
                mLog.debug(ex.toString(), ex);
            } else {
                MLog.d(TAG, ex.toString());
            }
        } catch (Exception e) {
            MLog.e(TAG, ex.toString());
        }
    }

    public static void WriteLog(String TAG, String sLog) {
        try {
            if (BuildConfig.LOGFILE && !bLogException) {
                mLog = Logger.getLogger(TAG);
                mLog.debug("[" + TAG + "] " + sLog);
            } else {
                MLog.i(TAG, sLog);
            }
        } catch (Exception e) {
            MLog.e(TAG, e.toString());
        }
    }
}
