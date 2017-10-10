
//file create by liunianliang 20130718~20130724

package com.rgk.factory.battery;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Battery extends Activity implements View.OnClickListener {
    
	public static String TAG = "Battery";
	private TextView mBattery_status,mBattery_plug,mBattery_level,mBattery_health,mBattery_v,mBattery_temp,
	                  mBattery_brand,mBattery_scale;
	private int mstatus,mhealth,mlevle,mscale,mplugType,mvoltage,mtemp;
	private String technology;
	private boolean hadSendBroadcast = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.battery);
        setContentView(R.layout.battery);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.mBattery_layout);
        mLayout.setSystemUiVisibility(0x00002000);
        mBattery_status = (TextView) findViewById(R.id.battery_info_status);
        mBattery_plug = (TextView) findViewById(R.id.battery_info_plug);
        mBattery_level = (TextView) findViewById(R.id.battery_info_level);
        mBattery_health = (TextView) findViewById(R.id.battery_info_health);
        mBattery_v = (TextView) findViewById(R.id.battery_info_v);
        mBattery_temp = (TextView) findViewById(R.id.battery_info_temp);
        mBattery_brand = (TextView) findViewById(R.id.battery_info_brand);
        mBattery_scale = (TextView) findViewById(R.id.battery_info_scale);
        
        findViewById(R.id.battery_pass).setOnClickListener(this);
        findViewById(R.id.battery_fail).setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
        
    }
    
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent){
    		Log.e(TAG,"action="+intent.getAction());
            if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
            	int status = intent.getIntExtra("status", 0);
            	int health = intent.getIntExtra("health", 0);
            	int levle = intent.getIntExtra("level", 0);
            	int scale = intent.getIntExtra("scale", 0);
            	int plugType = intent.getIntExtra("plugged", 0);
            	int voltage = intent.getIntExtra("voltage", 0);
            	String technology = intent.getStringExtra("technology");
            	String temp = tenthsToFixedString(intent.getIntExtra("temperature", 0));
            	
            	Log.e(TAG,"status="+status+"  "+"health="+health+"  "+"levle="+levle+"  "+"scale="+scale);
            	Log.e(TAG,"plugType="+plugType+"  "+"voltage="+voltage+"  "+"technology="+technology+"  "+"temp="+temp);
            	
            	HandleBatteryInfoText(status,health,levle,scale,plugType,voltage,technology,temp);
            }
    	}

    };
    
	private void HandleBatteryInfoText(int status, int health, int levle,
			int scale, int plugType, int voltage, String technology,
			String temp) {
		switch(status){
		   case BatteryManager.BATTERY_STATUS_CHARGING:
			   mstatus = R.string.Charging;
			   break;
		   case BatteryManager.BATTERY_STATUS_DISCHARGING:
			   mstatus = R.string.DisCharging;
			   break;
		   case BatteryManager.BATTERY_STATUS_FULL:
			   mstatus = R.string.Full;
			   break;
		   case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
			   mstatus = R.string.DisCharging;
			   break;
		   default:
			   mstatus = R.string.Unknow;
			   break;
		}
		mBattery_status.setText(getResources().getString(R.string.Status)+getResources().getString(mstatus));
		
		switch(plugType){
		   case BatteryManager.BATTERY_PLUGGED_AC:
			   mplugType = R.string.ac;
			   break;
		   case BatteryManager.BATTERY_PLUGGED_USB:
			   mplugType = R.string.usb;
			   break;
		
		   case BatteryManager.BATTERY_PLUGGED_WIRELESS:
			   mplugType = R.string.wireless;
			   break;  
		   default:
			   mplugType = R.string.Unknow;
			   break;
		}
		
		mBattery_plug.setText(getResources().getString(R.string.plugType)+getResources().getString(mplugType));
		
		switch(health){
		   case BatteryManager.BATTERY_HEALTH_GOOD:
			   mhealth = R.string.good;
			   break;
		   case BatteryManager.BATTERY_HEALTH_DEAD:
			   mhealth = R.string.dead;
			   break;
		   case BatteryManager.BATTERY_HEALTH_COLD:
			   mhealth = R.string.cold;
			   break;
		   case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
			   mhealth = R.string.over_voltage;
			   break;
		   case BatteryManager.BATTERY_HEALTH_OVERHEAT:
			   mhealth = R.string.overheat;
			   break;
		   default:
			   mhealth = R.string.Unknow;	   
		}
		
		mBattery_health.setText(getResources().getString(R.string.batteryhealth)+getResources().getString(mhealth));
		
		mBattery_level.setText(getResources().getString(R.string.levle)+levle+"%");
		
		mBattery_scale.setText(getResources().getString(R.string.scale)+scale+"%");
		
		mBattery_v.setText(getResources().getString(R.string.voltage)+voltage+"mV");
		
		mBattery_temp.setText(getResources().getString(R.string.temp)+temp+"*C");
		
		mBattery_brand.setText(getResources().getString(R.string.technology)+technology);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.battery_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.battery_fail:
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
	
    private final String tenthsToFixedString(int x) {
        int tens = x / 10;
        Log.e(TAG,"X="+x);
        return Integer.toString(tens) + "." + (x - 10 * tens);
    }
	
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
