
//file create by liunianliang 20130718~20130724

package com.rgk.factory.sensor;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
//import android.os.IPowerManager;
//import android.os.ServiceManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DistanceSensor extends Activity implements View.OnClickListener,SensorEventListener {
    
	public static String TAG = "DistanceSensor";
	
	private TextView mValue;
	private SensorManager sm;
	private Sensor DistanceSensor;
	WindowManager.LayoutParams mLayoutParams;
    private float mBrightness = 1.0f;
    private boolean hadSendBroadcast = false;
    private Button mPass;
    private final static int INIT_VALUE = 10;
    private float value = INIT_VALUE;
    private float pre_value = INIT_VALUE;
    private int count = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Distance_Sensor);
        setContentView(R.layout.distancesensor);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.distancesensor_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        DistanceSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        
        mValue = (TextView)findViewById(R.id.distancesensor_text);
        
        if(DistanceSensor == null){
        	mValue.setText(R.string.no_DSensor);
        }
        
        mLayoutParams = getWindow().getAttributes();
        mLayoutParams.screenBrightness = 1;
        getWindow().setAttributes(mLayoutParams);
        mPass = (Button)findViewById(R.id.distancesensor_pass);
        mPass.setOnClickListener(this);
        ((Button)findViewById(R.id.distancesensor_fail)).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.distancesensor_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.distancesensor_fail:
				SendBroadcast(Config.FAIL);
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
     protected void onResume() {
         super.onResume();
         sm.registerListener(this,DistanceSensor,SensorManager.SENSOR_DELAY_GAME);
     }
	     
     @Override
     protected void onStop() {
         sm.unregisterListener(this);
         super.onStop();
     }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
			value = event.values[0];
			mValue.setText(getResources().getString(R.string.DS_value)+value);            
			            
            if(value == 1.0){
            	mBrightness = 1.0f;
            }else{
            	mBrightness = 0.01f;
				//mPass.setEnabled(true);
				//SendBroadcast(Config.PASS);// Delete by sunjie
            }
            mLayoutParams = getWindow().getAttributes();
            mLayoutParams.screenBrightness = mBrightness;
            getWindow().setAttributes(mLayoutParams);
            
            if (value != pre_value) {
                count++;
            }
            if (count >= 2) {
            	handler.sendEmptyMessageDelayed(1, 1000);            	
            }
            pre_value = value;
		}
	} 
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			SendBroadcast(Config.PASS);
			super.handleMessage(msg);
		}    	
    };    
}
