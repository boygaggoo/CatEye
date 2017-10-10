
//file create by liunianliang 20130718~20130724

package com.rgk.factory.sensor;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LightSensor extends Activity implements View.OnClickListener,SensorEventListener {
    
	public static String TAG = "LightSensor";
	
	private TextView mValue;
	private SensorManager sm;
	private Sensor LightSensor;
	private boolean hadSendBroadcast = false;
	private int num = 0;
    private float change = 0;
	private Button mPass,mFail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Light_Sensor);
        setContentView(R.layout.lightsensor);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.lightsensor_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        LightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        
        mValue = (TextView)findViewById(R.id.Ls_text);
        
        if(LightSensor == null){
        	mValue.setText(R.string.no_Lsensor);
        }
        mPass = (Button)findViewById(R.id.Ls_pass);
        mPass.setOnClickListener(this);
	mFail = (Button)findViewById(R.id.Ls_fail);
        mFail.setOnClickListener(this);
      
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.Ls_pass:
				SendBroadcast(Config.PASS);
				finish();
				break;
			case R.id.Ls_fail:				
				SendBroadcast(Config.FAIL);
				finish();
				break;
		}
	}
	
	public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			change = 0;
		}
	}
	
	@Override
    protected void onResume() {
         super.onResume();
         sm.registerListener(this,LightSensor,SensorManager.SENSOR_DELAY_GAME);
    }
	     
    @Override
    protected void onStop() {
         sm.unregisterListener(this);
         super.onStop();
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	        float X = 0;
		if(event.sensor.getType()==Sensor.TYPE_LIGHT){
            X = event.values[0];
            float Y = event.values[1];
            float Z = event.values[2];
            Log.e(TAG,"X="+X+"   "+"Y="+Y+"   "+"Z="+Z);
            mValue.setText(getResources().getString(R.string.ls_value)+X);
			if(num == 0){
			    change = X;
			}
			num ++;
		}
		if(change != X) {
		}
		
	} 
     

}
