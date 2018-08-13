package io.brahmaos.setupwizard.util;

import android.util.Log;

public class BLog {
    private static boolean ENABLE = true;
    private static final String TAG = "SETUP_";
    public static void init(boolean isDebug) {
        BLog.ENABLE = isDebug;
    }

    public static void v(String tag, String message) {
        if (ENABLE) {
            Log.v(TAG + tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (ENABLE) {
            Log.d(TAG + tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (ENABLE) {
            Log.i(TAG + tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (ENABLE) {
            Log.w(TAG + tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (ENABLE) {
            Log.e(TAG + tag, message);
        }
    }

}
