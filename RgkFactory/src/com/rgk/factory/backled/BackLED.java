
//file create by liunianliang 20130718~20130724

package com.rgk.factory.backled;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.SeekBar;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.util.Log;
import android.os.Handler;
import android.content.DialogInterface;
import android.app.AlertDialog;
//implements View.OnClickListener,SeekBar.OnSeekBarChangeListener
public class BackLED extends Activity   {
    
	public static String TAG = "BackLED";

	private SeekBar mSeekbar;

	private int brightnessState = 0;
    private float mBrightness = 1.0f;
    private PowerManager mPowerManager;
    private LinearLayout mLinearLayout;
	private Handler mHandler = new Handler();
    WindowManager.LayoutParams mLayoutParams;

    private boolean hadSendBroadcast = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.BackLED);
        setContentView(R.layout.backled);
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.backled_layout);
        mLayout.setSystemUiVisibility(0x00002000);
       // findViewById(R.id.backled_pass).setOnClickListener(this);
        //findViewById(R.id.backled_fail).setOnClickListener(this);
         /*
        mSeekbar = (SeekBar) findViewById(R.id.seekbar);
        mSeekbar.setMax(225);
        
        mSeekbar.setOnSeekBarChangeListener(this);
        */
		//mLayoutParams = getWindow().getAttributes();
        //mLayoutParams.screenBrightness = 1;
        //mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
         start();
		 showDialog();
    }
    
	public void start() {

        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 1000);
    }
    /*
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.backled_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.backled_fail:
				SendBroadcast(Config.FAIL);
				break;
		}		
	}
	
	public void SendBroadcast(String result){
		ResultHandle.SaveResultToSystem(result, TAG);
		sendBroadcast(new Intent(Config.ItemOnClick));
		sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
		finish();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		setBrightness(progress + Config.MinBrightNess);
	}

	private void setBrightness(int brightness) {
        try {
            IPowerManager power = IPowerManager.Stub.asInterface(
                    ServiceManager.getService("power"));
            if (power != null) {
                if (power.isScreenOn()) {
                    power.setTemporaryScreenBrightnessSettingOverride(brightness);
					Settings.System.putInt(this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,brightness);
					Intent intent = new Intent();
					intent.setAction("intent.android.ACTION_BRIGHTNESS_CHANGED");
					sendBroadcast(intent);
                } else {
                    power.setTemporaryScreenBrightnessSettingOverride(-1);
                }
            }
        } catch (RemoteException doe) {
        	
        }
	}
            
       

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {	
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	*/
	private void showDialog() {

        new AlertDialog.Builder(this)

        .setTitle(R.string.choose)

        .setCancelable(false)

        .setPositiveButton(R.string.pass, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int i) {

                mHandler.removeCallbacks(mRunnable);
                SendBroadcast(Config.PASS);
            }
        })

        .setNegativeButton(R.string.fail, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int i) {

                mHandler.removeCallbacks(mRunnable);
                SendBroadcast(Config.FAIL);
            }
        }).show();
    }
  private Runnable mRunnable = new Runnable() {

         public void run() { 
        		 if(mBrightness == 1.0f){
					 Log.d("dsg", "1====="+mBrightness);
        		     mBrightness = 0.01f;
        		 }else {
					 Log.d("dsg", "2====="+mBrightness);
                     mBrightness = 1.0f; 
        		 }
                 mLayoutParams = getWindow().getAttributes();
                 mLayoutParams.screenBrightness = mBrightness;
                 getWindow().setAttributes(mLayoutParams);
				 mHandler.postDelayed(mRunnable, 1000);
         }
     };

	public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			finish();
		}
	}
	
}
