package com.tcl.util;
import android.util.Log;

public class DebugUtil {

	public static final boolean DEBUG = false;
	public static String TAG = "ODM:";

	public static void v(String tag, String msg) {
		if (DEBUG) {
			Log.v(TAG + tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(TAG + tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(TAG + tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			Log.w(TAG + tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(TAG + tag, msg);
		}
	}
}
