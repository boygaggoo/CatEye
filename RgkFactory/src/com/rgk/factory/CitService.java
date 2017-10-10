
package com.rgk.factory;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


public class CitService extends Service {
    /** Called when the activity is first created. */
	private static String TAG = "CitService";
	private Handler mHandler;
	private int a = 0;
	
	public void onCreate()
	{
		Log.e(TAG,"service created");
		super.onCreate();
		a = 0;
		mHandler = new Handler();
        mHandler.post(update);
	}

	private Runnable update = new Runnable() {
		
		@Override
		public void run() {
			sendBroadcast(new Intent("start_in_factory_mode"));
			//Log.e(TAG,"-------------send----------------a="+(a++));
			mHandler.postDelayed(update, 2);
		}
	};

	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class LocalBinder extends Binder
	{
		public CitService getService()
		{
			return CitService.this;
		}
	}
	
	@Override
	public void onDestroy() {
		mHandler.removeCallbacks(update);
		super.onDestroy();
	}

}
