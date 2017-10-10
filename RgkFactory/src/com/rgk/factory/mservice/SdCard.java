
/**
 * dushiguang
 */

package com.rgk.factory.mservice;

import java.io.File;

import com.rgk.factory.Config;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.util.Log;
import android.os.Handler;

public class SdCard extends Service  {
   
	public static String TAG = "Sdcard";
	private boolean hadSendBroadcast = false;
    private boolean in_SDCard = false;
	private boolean exter_SDCard = false;
	private Handler mHanler = new Handler();
    @Override
    public void onCreate() {     
        getInternalSdcardSize();
        getExternalSdcardSize();
		mHanler.postDelayed(runnable,0);
    }

	private Runnable runnable = new Runnable() {
	        @Override
			public void run() {
	            try {
					if(in_SDCard && exter_SDCard) {
                        SendBroadcast(Config.PASS);
					} else {
                        SendBroadcast(Config.FAIL);
					}
				} catch (Exception e) {
	                e.printStackTrace();
				}
			}
		};

	
	public void getInternalSdcardSize(){
        File root = Environment.getRootDirectory();
        StatFs mStatFs = new StatFs(/*root.getPath()*/Config.InternalSdcardUri);
        Log.e(TAG,"internal path="+root.getPath());
        long blockCount = mStatFs.getBlockCount();
                
		if (blockCount > 0) {
            in_SDCard = true;
		} 

	}
	
	public void getExternalSdcardSize(){
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {        
	        String root = Config.ExternalSdcardUri;
	        StatFs mStatFs = new StatFs(root);
	        
	        long blockCount = mStatFs.getBlockCount();
	        if(blockCount !=0){		        
				exter_SDCard = true;
	        }
		}
	}
	
	public void SendBroadcast(String result){
		mHanler.removeCallbacks(runnable);
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			stopSelf();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
