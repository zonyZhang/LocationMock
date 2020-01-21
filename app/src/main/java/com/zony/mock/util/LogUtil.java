package com.zony.mock.util;

import android.util.Log;

/**
 * 更改OPEN_LOG的布尔值来开启或关闭Log信息的显示，或者通过改变LOG_LEVEL,改变Log的级别并打印出来
 *
 * @author zony
 * @time 18-5-7
 */
public class LogUtil {

    /**
     * log level
     */
    private static final int LOG_LEVEL = 5;

    /**
     * true or false
     */
    private static final boolean OPEN_LOG = true;

    /**
     * common log
     */
    private static final String MSG_COMMON = "LocationMock-";
  
    public static void i(String tag, String msg) {  
        if (LOG_LEVEL > 2 && OPEN_LOG) {
            Log.i(tag, MSG_COMMON + msg);
        }  
    }
  
    public static void e(String tag, String msg) {  
        if (LOG_LEVEL > 0 && OPEN_LOG) {  
            Log.e(tag, MSG_COMMON + msg);
        }  
    }
  
    public static void w(String tag, String msg) {  
        if (LOG_LEVEL > 1 && OPEN_LOG) {  
            Log.w(tag, MSG_COMMON + msg);
        }  
    }
  
    public static void d(String tag, String msg) {  
        if (LOG_LEVEL > 3 && OPEN_LOG) {  
            Log.d(tag, MSG_COMMON + msg);
        }  
    }
  
    public static void v(String tag, String msg) {  
        if (LOG_LEVEL > 4 && OPEN_LOG) {  
            Log.v(tag, MSG_COMMON + msg);
        }  
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL > 3 && OPEN_LOG) {
            Log.d(tag, MSG_COMMON + msg, tr);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL > 1 && OPEN_LOG) {
            Log.w(tag, MSG_COMMON + msg, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL > 0 && OPEN_LOG) {
            Log.e(tag, MSG_COMMON + msg, tr);
        }
    }
}  
