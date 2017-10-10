package com.rgk.factory;

import android.graphics.Color;

//file create by liunianliang 20130718~20130724

public class Config  {
	
      public static String TAG = "config";
      
      public static byte[] VibratorTime = {'3','0','0','0','0'};
      public static String VibratorFile = "/sys/class/timed_output/vibrator/enable";
      
      public static String ResaultUri = "data/data/com.rgk.factory/";
      public static int BLACK = Color.BLACK;
      public static int RED = Color.RED;
      public static int BLUE = Color.BLUE;
	  public static int GREEN = Color.GREEN;
      public static String PASS = "pass";
      public static String FAIL = "fail";
      public static String NOTHING = "nothing";
      
      public static int MinBrightNess = 30;
      
      public static String InternalSdcardUri = "/storage/sdcard0";
      public static String ExternalSdcardUri = "/storage/sdcard1";
      
      public static int MainCamera = 0;
      public static int SubCamera = 1;
      
      public static float GsensorPassLimit = 5;
      
      public static boolean MoreCircle = false;
      
      public static String ItemOnClick = "ACTION_ITEM_HAS_CLICK";
      public static String ACTION_START_AUTO_TEST = "intent.action_auto_test";
}
