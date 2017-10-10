
//dushiguang 2014.08.21

package com.rgk.factory.mservice;
import com.rgk.factory.Config;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.content.Intent;
import android.content.Context;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;

import java.io.FileReader;
import java.io.BufferedReader;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.os.Handler;

public class Memory extends Service {
	
    public static String TAG = "Memory";
    private boolean hadSendBroadcast = false;
	private long memory=0;
	private Handler mHandler = new Handler();
    @Override
    public void onCreate() {
        Log.d("dsg", "=====Memory==>>onCreate()");
        this.getTotalMemory();
        this.getAvailMemory();
		startMenoryTest();
    }

    public void startMenoryTest() {
		mHandler.removeCallbacks(runnable);
		mHandler.postDelayed(runnable,0);//ms
	}
    private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				 if (memory > 0) {
				 	SendBroadcast(Config.PASS);
				 } else {
                    SendBroadcast(Config.FAIL);
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};
	
	private String getAvailMemory() {
    	
    	ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mMemoryInfo = new MemoryInfo();
        mActivityManager.getMemoryInfo(mMemoryInfo);
    	return Formatter.formatFileSize(getBaseContext(), mMemoryInfo.availMem);
    }
    
    private String getTotalMemory() {
    	String mfileStr = "/proc/meminfo";
    	String str2;
    	String[] arrayOfString = null;
		long initial_memory = 0;
    	FileReader mFileReader;
    	BufferedReader mfileBuff;
    	try {
			mFileReader = new FileReader(mfileStr);
			mfileBuff = new BufferedReader(mFileReader);
			str2 = mfileBuff.readLine();
			
			arrayOfString = str2.split("\\s+");
			for(String num:arrayOfString) {
				Log.i(str2,num+"\t");
			}
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue()*1024;
			memory = initial_memory;
			mfileBuff.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
    	return Formatter.formatFileSize(getBaseContext(), initial_memory);
    }

		
	public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
		    mHandler.removeCallbacks(runnable);
			ResultHandle.SaveResultToSystem(result, TAG);			
			mHandler.removeCallbacks(runnable);
			stopSelf();
		}
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d("dsg", "=====Memory==>>onDestroy()");			
		Intent serviceIntent = new Intent(Memory.this, SdCard.class);
		startService(serviceIntent);
		Log.d("dsg", "=====SdCard==>>>startSdCardService()");
		super.onDestroy();
	}
}
