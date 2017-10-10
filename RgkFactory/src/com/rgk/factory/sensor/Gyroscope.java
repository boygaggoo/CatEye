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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Gyroscope extends Activity implements View.OnClickListener,SensorEventListener {
    
	public static String TAG = "Gyroscope";
	
	private TextView mTextView,mTestTitle;
	private SensorManager sm;
	private Sensor gyroscope;
	private boolean hadSendBroadcast = false;
	private Button mPass,mFail;
	private final static int GYROSCOPE_PASS_LIMIT = 5;
    int x_min,x_max,y_min,y_max,z_min,z_max,a,b,c;
    boolean x_min_ok,x_max_ok,y_min_ok,y_max_ok,z_min_ok,z_max_ok;
    String testResult,x_min_str,x_max_str,y_min_str,y_max_str,z_min_str,z_max_str;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.gyroscope);
        setContentView(R.layout.gyroscope);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.gyroscope_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroscope = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);        
        mTextView = (TextView)findViewById(R.id.gyroscope_test_result);
        mTestTitle = (TextView)findViewById(R.id.gyroscope_test_title);
        mTestTitle.setText(getString(R.string.gyroscope_test_title, GYROSCOPE_PASS_LIMIT));
        
        mPass = (Button)findViewById(R.id.gyroscope_pass);
        mPass.setOnClickListener(this);
        mFail = (Button)findViewById(R.id.gyroscope_fail);
        mFail.setOnClickListener(this);
     }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.gyroscope_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.gyroscope_fail:				
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
         sm.registerListener(this,gyroscope,SensorManager.SENSOR_DELAY_GAME);
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
        synchronized (this) {
        	if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        		a = (int)event.values[0];
        		b = (int)event.values[1];
  			    c = (int)event.values[2];
  			    if(a < x_min) {
  			    	x_min = a;
  			    	if(x_min <= -GYROSCOPE_PASS_LIMIT) {
  			    		x_min_ok = true;
  			    	}
  			    } else if(a > x_max) {
  			    	x_max = a;
  			    	if(x_max >= GYROSCOPE_PASS_LIMIT) {
  			    		x_max_ok = true;
  			    	}
				}
				
				if(b < y_min) {
					y_min = b;
					if(y_min <= -GYROSCOPE_PASS_LIMIT) {
						y_min_ok = true;
  			    	}
				}	else if(b > y_max) {
					y_max = b;
					if(y_max >= GYROSCOPE_PASS_LIMIT) {
						y_max_ok = true;
  			    	}
				}
				  
				if(c < z_min) {
				    z_min = c;
				    if(z_min <= -GYROSCOPE_PASS_LIMIT) {
				    	z_min_ok = true;
  			    	}
				}	else if(c > z_max) {
					z_max = c;
					if(z_max >= GYROSCOPE_PASS_LIMIT) {
						z_max_ok = true;
  			    	}
				}
				
				testResult = (x_max+"\n\n"+x_min+"\n\n"+y_max+"\n\n"+y_min+"\n\n"+z_max+"\n\n"+z_min);
                mTextView.setText(testResult);
                if(x_min_ok&&x_max_ok&&y_min_ok&&y_max_ok&&z_min_ok&&z_max_ok) {
                	SendBroadcast(Config.PASS);
                }                
            }
        }
	}
     

}
