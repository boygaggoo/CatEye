
/**
 * @author shiguang.du 2014.06.11
 *
 */

package com.rgk.factory.vibrator;

import java.io.FileOutputStream;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.os.Vibrator;
import android.app.Service;
import android.os.Handler;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.LinearLayout;

public class Vibration extends Activity implements View.OnClickListener{

    private Handler mHandler = new Handler();
	public static String TAG = "Vibration";
    private Vibrator mVibrator;
    private int isrun = 3;
	/** Called when the activity is first created. */
    public Button buttonPass,buttonFail;
    private boolean hadSendBroadcast = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Vibrator);
        setContentView(R.layout.vibrator);
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.vibrator_layout);
        mLayout.setSystemUiVisibility(0x00002000);
	    buttonPass = ((Button)findViewById(R.id.telephony_pass));
        buttonPass.setOnClickListener(this);
        buttonFail= ((Button)findViewById(R.id.telephony_fail));
        buttonFail.setOnClickListener(this);
        mVibrator = (Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        startVibrationTest();
        //
    }

	public void startVibrationTest() {
		mHandler.removeCallbacks(runnable);
		mHandler.postDelayed(runnable, 1000);//ms
	}
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				  while(isrun > 0) {
				      StartVibrate();
				      Thread.currentThread().sleep(1500);
					  isrun --;
				  }	
    	          buttonPass.setVisibility(View.VISIBLE);
    	          buttonFail.setVisibility(View.VISIBLE);
			    //Thread.currentThread().sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};
	private void StartVibrate() {
	      try {	
	          mVibrator.vibrate(1000);
			  
		    }				
		   catch (Exception e) {			
		     Log.d(TAG , e.toString());
		  }	
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.telephony_pass:
				SendBroadcast(Config.PASS);
				mHandler.removeCallbacks(runnable);
				break;
			case R.id.telephony_fail:
				SendBroadcast(Config.FAIL);
				mHandler.removeCallbacks(runnable);
				break;
		}	
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
	public void finish() {  
	        super.finish();   
	}
	
	
	@Override    
	protected void onDestroy() {  
		  super.onDestroy();  
	}

}
