package com.rgk.factory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class CitTestActivityReceiver extends BroadcastReceiver {
    String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";
	private static final String TAG = "CitTestActivityReceiver";

    public void onReceive(Context context, Intent intent) {
        String host = intent.getData() != null ? intent.getData().getHost() : null;
	Log.e(TAG, "CitTestActivityReceiver........");
        if (intent.getAction().equals(SECRET_CODE_ACTION)) {
            if ("363".equals(host)) {
            	Intent i = new Intent(Intent.ACTION_MAIN);
                i.setClass(context, ItemTestActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                Log.i(TAG, "Hello 363........");
                return;
            }
        }
    }
}
