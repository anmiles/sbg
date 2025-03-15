package net.anmiles.sbg;

import android.util.Log;

public class Logger {
    private static String tag = "SBG";

    static void error(String message) {
        Log.e(Logger.tag, message);
    }

    static void warn(String message) {
        Log.w(Logger.tag, message);
    }

    static void info(String message) {
        Log.i(Logger.tag, message);
    }

    static void debug(String message) {
        Log.d(Logger.tag, message);
    }
}
