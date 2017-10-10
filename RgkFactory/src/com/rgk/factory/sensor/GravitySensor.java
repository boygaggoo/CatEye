
//file create by liunianliang 20130718~20130724

package com.rgk.factory.sensor;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.Util;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;

@SuppressWarnings("deprecation")
public class GravitySensor extends Activity implements SensorListener,View.OnClickListener {
    /** Called when the activity is first created. */
    public static String TAG = "GravitySensor";
    
    private TextView X1,X2,Y1,Y2,Z1,Z2;
    private SensorManager sm = null;
    private boolean x1ok,x2ok,y1ok,y2ok,z1ok,z2ok;
    private boolean hadSendBroadcast = false;
	private Handler mHanler = new Handler();
	private boolean  b = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Gravity_Sensor);
        setContentView(R.layout.gravitysensor);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.gravitysensor_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        x1ok = false;
		x2ok = false;
		y1ok = false;
		y2ok = false;
		z1ok = false;
		z2ok = false;
        X1 = (TextView)findViewById(R.id.X1_text);
        X2 = (TextView)findViewById(R.id.X2_text);
        Y1 = (TextView)findViewById(R.id.Y1_text);
        Y2 = (TextView)findViewById(R.id.Y2_text);
        Z1 = (TextView)findViewById(R.id.Z1_text);
        Z2 = (TextView)findViewById(R.id.Z2_text);
        if(Util.gravitySensorNoZAxis) {
        	Z1.setVisibility(View.GONE);
        	Z2.setVisibility(View.GONE);
        	z1ok = true;
    		z2ok = true;
        }
        findViewById(R.id.gsensor_pass).setOnClickListener(this);
        findViewById(R.id.gsensor_fail).setOnClickListener(this);
        
        X1.setText("X+:"+getResources().getString(R.string.Gsensor_testing));
        X2.setText("X-:"+getResources().getString(R.string.Gsensor_testing));
        Y1.setText("Y+:"+getResources().getString(R.string.Gsensor_testing));
        Y2.setText("Y-:"+getResources().getString(R.string.Gsensor_testing));
        Z1.setText("Z+:"+getResources().getString(R.string.Gsensor_testing));
        Z2.setText("Z-:"+getResources().getString(R.string.Gsensor_testing));
		mHanler.postDelayed(runnable, 1);
    }
	private Runnable runnable = new Runnable() {
        @Override
		public void run() {
            try {
                //Thread.currentThread().sleep(1);
				b=true;
			} catch (Exception e) {
                e.printStackTrace();
			}
		}
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.gsensor_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.gsensor_fail:
				SendBroadcast(Config.FAIL);
				break;
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

	@Override
	public void onAccuracyChanged(int arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
	            float X = values[0];
	            float Y = values[1];
	            float Z = values[2];
	            Log.e(TAG,"X="+X+"   "+"Y="+Y+"   "+"Z="+Z);
	            if(X<-Config.GsensorPassLimit){
	            	X1.setText("X+:"+getResources().getString(R.string.Gsensor_ok));
	            	X1.setTextColor(Color.GREEN);
	            	x1ok = true;
	            }else if(X>Config.GsensorPassLimit){
	            	X2.setText("X-:"+getResources().getString(R.string.Gsensor_ok));
	            	X2.setTextColor(Color.GREEN);
	            	x2ok = true;
	            }
	            if(Y<-Config.GsensorPassLimit){
	            	Y1.setText("Y+:"+getResources().getString(R.string.Gsensor_ok));
	            	Y1.setTextColor(Color.GREEN);
	            	y1ok = true;
	            }else if(Y>Config.GsensorPassLimit){
	            	Y2.setText("Y-:"+getResources().getString(R.string.Gsensor_ok));
	            	Y2.setTextColor(Color.GREEN);
	            	y2ok = true;
	            }
	            if(Z<-Config.GsensorPassLimit){
	            	Z1.setText("Z+:"+getResources().getString(R.string.Gsensor_ok));
	            	Z1.setTextColor(Color.GREEN);
	            	z1ok = true;
	            }else if(Z>Config.GsensorPassLimit){
	            	Z2.setText("Z-:"+getResources().getString(R.string.Gsensor_ok));
	            	Z2.setTextColor(Color.GREEN);
	            	z2ok = true;
	            }
           }	
        if(b && (x1ok && x2ok && y1ok && y2ok && z1ok && z2ok)){
        	 SendBroadcast(Config.PASS);
			 finish();
        }   
	}
	
	 @Override
     protected void onResume() {
         super.onResume();
         sm.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
     }
	     
     @Override
     protected void onStop() {
         sm.unregisterListener(this);
         super.onStop();
     } 

}
