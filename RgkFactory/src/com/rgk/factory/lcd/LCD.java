
/**
 * @author shiguang.du
 * @date 2014.06.17
 */

package com.rgk.factory.lcd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

public class LCD extends Activity {
	
	public static String TAG = "LCD";

    private Handler mHandler = new Handler();
    private int brightnessState = 0, imgId = 0;
    private float mBrightness = 1.0f;
    float x = 0, y = 0;
    WindowManager.LayoutParams mLayoutParams;
    private LinearLayout mLinearLayout;
    private long oldTime =0;
    private final long SHOTEST_ALTERNATION = 2000;
    private boolean hadSendBroadcast = false;

    private int[] mTestImg = {
            R.drawable.lcd_red, R.drawable.lcd_green, R.drawable.lcd_blue, R.drawable.lcd_white,
            R.drawable.lcd_black
    };


    @Override
    public void finish() {

        super.finish();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.lcd);
        
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.lcd_layout);
        mLayout.setSystemUiVisibility(0x00002000);
        
        mLayoutParams = getWindow().getAttributes();
        mLayoutParams.screenBrightness = 1;
        getWindow().setAttributes(mLayoutParams);

        mLinearLayout = (LinearLayout) findViewById(R.id.lcd_layout);
        start();
    }

    public void start() {

        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 0);
    }

    private Runnable mRunnable = new Runnable() {

        public void run() {
        
            if (brightnessState == 0) {
                mBrightness = 0.01f;
                brightnessState = 1;

            } else {
                mBrightness = 1.0f;
                brightnessState = 0;
            }
            try {
                mLinearLayout.setBackgroundResource(0);
                mLinearLayout.setBackgroundResource(mTestImg[imgId]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mLayoutParams = getWindow().getAttributes();
            mLayoutParams.screenBrightness = mBrightness;
            getWindow().setAttributes(mLayoutParams);
            if (brightnessState == 0) {
            	
                if (imgId <= mTestImg.length-1) {
           					 imgId++;
                } else {
                    imgId = 0;
                    showDialog();
                }
            }
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int mAction = event.getAction();
        if ((mAction == MotionEvent.ACTION_UP)) {
        	if(nextItem()){
            imgId++;
        	}

        }
        return true;
    }
  
    private boolean nextItem(){
    	 long newTime = SystemClock.elapsedRealtime();
    	 if(newTime - oldTime < SHOTEST_ALTERNATION){

    		    return false;
    	 }
    	 oldTime = SystemClock.elapsedRealtime();
    	 return true;
    }
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
    
	public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			finish();
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHandler.removeCallbacks(mRunnable);

    }
    
    void logd(Object d) {

        Log.d(TAG, "" + d);
    }

    void loge(Object e) {

        Log.e(TAG, "" + e);
    }
    public void toast(Object s) {

        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }
}
