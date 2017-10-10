package com.rgk.factory.maincamera;

import android.util.Log;

/**
 * 
 * @author ruilong.hu
 *
 */
public class LogUtils {

        static boolean logs = false;
	public static void logD(String tag, Object msg) {
             if(logs)
		Log.d("hrl", tag +" <###> " +  msg);
	}

	public static void logE(String tag, Object msg) {
             if(logs)
		Log.e("hrl", tag +" <+++> " +  msg);
	}

	public static void logV(String tag, Object msg) {
             if(logs)
		Log.v("hrl", tag +" <---> " +  msg);
	}

	public static void logW(String tag, Object msg) {
             if(logs)
		Log.w("hrl", tag +" <@@@> " +  msg);
	}
	
	public static void systemLog(String tag, Object msg){
             if(logs)
		System.out.println(tag + " <***> " + msg);
	}
}
