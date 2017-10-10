package com.rgk.factory.flash;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
public class FlashlightManager extends Activity implements View.OnClickListener{
	public static String TAG = "Flashlight";
	private Camera camera = null;   
    private Parameters parameters = null;
    public Button buttonPass,buttonFail;
	private Handler mHandler = new Handler();
	private int isrun = 3;
	private boolean hadSendBroadcast = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setTitle(R.string.key_Flash);
		setContentView(R.layout.flashlight);
		LinearLayout mLayout = (LinearLayout) findViewById(R.id.flashlight_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
		buttonPass = ((Button)findViewById(R.id.flash_pass));
        buttonPass.setOnClickListener(this);
        buttonFail= ((Button)findViewById(R.id.flash_fail));
        buttonFail.setOnClickListener(this);
        try {
			if(null == camera) {
				try {
					camera = Camera.open();
					parameters = camera.getParameters();
				} catch (RuntimeException e) {
					Toast.makeText(this, getResources().getString(R.string.open_camera_error), 
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			
		}
		testFlashLight();


	}

	private void testFlashLight() {
		mHandler.removeCallbacks(runnable);
		mHandler.postDelayed(runnable, 1000);//ms
	}
    private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				  while(isrun > 0) {
				      startFlashlight();
				      Thread.currentThread().sleep(500);
					  closeFlashLight();
					  Thread.currentThread().sleep(500);
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
	private void closeFlashLight() {
		// TODO Auto-generated method stub
	    parameters.setFlashMode(Parameters.FLASH_MODE_OFF); 
        camera.setParameters(parameters);   
	}

	private void startFlashlight() {
		// TODO Auto-generated method stub
	    parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);    
        camera.setParameters(parameters);   
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.flash_pass:
				SendBroadcast(Config.PASS);
				mHandler.removeCallbacks(runnable);
				break;
			case R.id.flash_fail:
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
	protected void onDestroy() {
		super.onDestroy();
		if (null != camera) {
			camera.release();
			camera = null;
		}
		if(null != parameters) {
			parameters = null;
		}
		if(null != mHandler) {
			mHandler.removeCallbacks(runnable);
		}
	}
}
