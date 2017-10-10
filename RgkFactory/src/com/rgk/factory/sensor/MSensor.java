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

public class MSensor extends Activity implements View.OnClickListener,SensorEventListener {
    
	public static String TAG = "MSensor";
	
	private TextView mTextView;
	private SensorManager sm;
	private Sensor magnetic;
	private boolean hadSendBroadcast = false;
	private Button mPass,mFail;
    private final static String INIT_VALUE = "";
    private static String value = INIT_VALUE;
    private static String pre_value = INIT_VALUE;
    private int count = 0;
    private final int MIN_COUNT = 50;
    private final static int MSG_SUCCESS = 1;
    private Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_SUCCESS:
				SendBroadcast(Config.PASS);
				break;
			}
			super.handleMessage(msg);
		}    	
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.magnetic);
        setContentView(R.layout.magnetic);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.magnetic_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        magnetic = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);        
        mTextView = (TextView)findViewById(R.id.magnetic_content);
        
        mPass = (Button)findViewById(R.id.magnetic_pass);
        mPass.setOnClickListener(this);
        mFail = (Button)findViewById(R.id.magnetic_fail);
        mFail.setOnClickListener(this);
     }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.magnetic_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.magnetic_fail:				
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
         sm.registerListener(this,magnetic,SensorManager.SENSOR_DELAY_GAME);
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
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                value = "(" + event.values[0] + ", " + event.values[1] + ", "
                        + event.values[2] + ")";
                mTextView.setText(getResources().getString(R.string.ls_value)+value);
                if (value != pre_value)
                    count++;
                if (count >= MIN_COUNT) {
                	myHandler.sendEmptyMessageDelayed(MSG_SUCCESS, 2000);
                }
                	
                pre_value = value;
            }
        }
	}
     

}
