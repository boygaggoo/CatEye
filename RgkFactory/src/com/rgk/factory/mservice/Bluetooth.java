//add dushiguang 08.19
package com.rgk.factory.mservice;

import java.util.ArrayList;
import java.util.List;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class Bluetooth extends Service {
    
    public static String TAG = "Bluetooth";
    

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();
	private volatile boolean _discoveryFinished;
    private boolean testResult = false;
    private final int OUT_TIME = 20 * 1000;
    
    @Override
    public void onCreate() {
       
    	Log.d("dsg", "=====Bluetooth==>>onCreate");
		if (!_bluetooth.isEnabled())
		{
			_bluetooth.enable();
		}

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
        
		IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(_discoveryReceiver, discoveryFilter);
		
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(_foundReceiver, foundFilter);
		       
        _bluetooth.startDiscovery();
    }
    
    CountDownTimer mCountDownTimer = new CountDownTimer(OUT_TIME, 3000) {

        @Override
        public void onTick(long arg0) {
      
        }

        @Override
        public void onFinish() {
            if(testResult) {
            	SendBroadcast(Config.PASS);
            } else {
            	SendBroadcast(Config.FAIL);
            }
            Log.d("dsg", "Time_out");
        }
    };
    /*
    //no use
	private Runnable update = new Runnable() {
		
		@Override
		public void run() {

			mHandler.postDelayed(update, 2);
		}
	};
	*/	
	public void SendBroadcast(String result){
		ResultHandle.SaveResultToSystem(result, TAG);
		stopSelf();

	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent){
    		Log.e(TAG,"action="+intent.getAction());
            if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int BluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch(BluetoothState){
	                case BluetoothAdapter.STATE_OFF:
	                	Log.d("dsg", "BluetoothIsClosed");
	                	break;
	                case BluetoothAdapter.STATE_ON:
	                	_bluetooth.startDiscovery();
	                	mCountDownTimer.start();
	                	Log.d("dsg", "BluetoothIsScaning");
	                	break;
	                case BluetoothAdapter.STATE_TURNING_ON:
	                	Log.d("dsg", "BluetoothIsTurningOn");
	                	break;
	                case BluetoothAdapter.STATE_TURNING_OFF:
	                	break;
                }
            }
    	}
    };
    
	private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			_devices.add(device);
			showDevices();
		}
	};
	private BroadcastReceiver _discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			_discoveryFinished = true;
		}
	};
    
	/*
	//no use
	private Runnable _discoveryWorkder = new Runnable() {
		public void run() 
		{
			_bluetooth.startDiscovery();
			for (;;) 
			{
				if (_discoveryFinished) 
				{
					break;
				}
				try 
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e){}
			}
		}
	};
	*/
	private void showDevices()
	{
		StringBuilder b = new StringBuilder();
		if(_devices.size() >0) {
			testResult = true;			
		} else {
			testResult = false;
		}
		
		for (int i = 0, size = _devices.size(); i < size; ++i)
		{
			BluetoothDevice d = _devices.get(i);
			b.append(new Integer(i + 1).toString() + ":");   
			b.append(d.getAddress());
			b.append("  ");
			b.append(d.getName()+'\n');
			
		}
	}

	@Override
	public void onDestroy() {
		Log.d("dsg", "=====Bluetooth==>>onDestroy()");
		if (_bluetooth.isEnabled())
		{
			_bluetooth.disable();
		}
        if(mBroadcastReceiver != null) unregisterReceiver(mBroadcastReceiver);
		if(_foundReceiver != null) unregisterReceiver(_foundReceiver);
		if(_discoveryReceiver != null) unregisterReceiver(_discoveryReceiver);
		
		Intent serviceIntent = new Intent(Bluetooth.this, SimCard.class);
		startService(serviceIntent);
		Log.d("dsg", "=====GPSService==>>>startWifiService()");
        super.onDestroy();
        Log.d("dsg", "=====GPSService==>>>onDestroy()");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	} 
}
