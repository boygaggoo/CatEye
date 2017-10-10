
//file create by liunianliang 20130718~20130724

package com.rgk.factory.sdcard;

import java.io.File;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;

public class SdCard extends Activity implements View.OnClickListener  {
   
	public static String TAG = "Sdcard";
	private boolean hadSendBroadcast = false;
    private boolean in_SDCard = false;
	private boolean exter_SDCard = false;
	private Handler mHanler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.SDcard);
        setContentView(R.layout.sdcard);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.sdcard_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        ((Button)findViewById(R.id.sdcard_pass)).setOnClickListener(this);
        ((Button)findViewById(R.id.sdcard_fail)).setOnClickListener(this);
      
        getInternalSdcardSize();
        getExternalSdcardSize();
		mHanler.postDelayed(runnable, 1000);
    }

	private Runnable runnable = new Runnable() {
	        @Override
			public void run() {
	            try {
	                Thread.currentThread().sleep(1500);
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


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		   case R.id.sdcard_pass:
			   SendBroadcast(Config.PASS);
			   break;
		   case R.id.sdcard_fail:
			   SendBroadcast(Config.FAIL);
			   break;
		}
	}
	
	public void getInternalSdcardSize(){
        File root = Environment.getRootDirectory();
        StatFs mStatFs = new StatFs(/*root.getPath()*/Config.InternalSdcardUri);
        Log.e(TAG,"internal path="+root.getPath());
        long blockSize = mStatFs.getBlockSize();
        long blockCount = mStatFs.getBlockCount();
        long Available = mStatFs.getAvailableBlocks();
        
        ((TextView) findViewById(R.id.internal_total)).setText(getResources().getString(R.string.total)+blockSize*blockCount/(1024*1024)+"MB");
        ((TextView) findViewById(R.id.internal_free)).setText(getResources().getString(R.string.free)+blockSize*Available/(1024*1024)+"MB");
		if (blockCount > 0) {
            in_SDCard = true;
		} 

	}
	
	public void getExternalSdcardSize(){
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
	        /*File root = Environment.getExternalStorageDirectory();
	        StatFs mStatFs = new StatFs(root.getPath());
	        Log.e(TAG,"external path="+root.getPath());
	        long blockSize = mStatFs.getBlockSize();
	        long blockCount = mStatFs.getBlockCount();
	        long Available = mStatFs.getAvailableBlocks();
	        long free = mStatFs.getFreeBlocks();*/
	        
	        String root = Config.ExternalSdcardUri;
	        StatFs mStatFs = new StatFs(root);
	        
	        long blockSize = mStatFs.getBlockSize();
	        long blockCount = mStatFs.getBlockCount();
	        long Available = mStatFs.getAvailableBlocks();
	        if(blockCount !=0){
		        ((TextView) findViewById(R.id.external_sdcard)).setText(getResources().getString(R.string.inserted_externalsdcard));
		        ((TextView) findViewById(R.id.external_total)).setText(getResources().getString(R.string.total)+blockSize*blockCount/(1024*1024)+"MB");
		        ((TextView) findViewById(R.id.external_free)).setText(getResources().getString(R.string.free)+blockSize*Available/(1024*1024)+"MB");
				exter_SDCard = true;
	        }
		}
	}
	
	public void SendBroadcast(String result){
		mHanler.removeCallbacks(runnable);
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			finish();
		}
	}

}
