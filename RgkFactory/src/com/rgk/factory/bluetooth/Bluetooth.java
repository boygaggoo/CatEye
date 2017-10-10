
//file create by liunianliang 20130718~20130724

package com.rgk.factory.bluetooth;

import java.util.ArrayList;
import java.util.List;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Bluetooth extends Activity implements View.OnClickListener {
    
    public static String TAG = "Bluetooth";
    
    private TextView mBluetoothStatus;
    private TextView mBluetoothInfo;
	private Handler _handler = new Handler();
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();
	private volatile boolean _discoveryFinished;

	private Handler mHandler;
	private boolean hadSendBroadcast = false;
    private boolean defBlueOpen;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Bluetooth);
        setContentView(R.layout.bluetooth);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.bluetooth_layout);
        mLayout.setSystemUiVisibility(0x00002000);
        mBluetoothStatus = (TextView)findViewById(R.id.bluetoothstatus_textview);
        mBluetoothInfo = (TextView)findViewById(R.id.bluetooth_textview);
        
        findViewById(R.id.bluetooth_pass).setOnClickListener(this);
        findViewById(R.id.bluetooth_fail).setOnClickListener(this);
        defBlueOpen = _bluetooth.isEnabled();
		if (!defBlueOpen)
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
		
        //mHandler = new Handler();
        //mHandler.post(_discoveryWorkder);
        
        _bluetooth.startDiscovery();
    }

    //no use
	private Runnable update = new Runnable() {
		
		@Override
		public void run() {

			mHandler.postDelayed(update, 2);
		}
	};
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.bluetooth_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.bluetooth_fail:
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
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent){
    		Log.e(TAG,"action="+intent.getAction());
            if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int BluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch(BluetoothState){
	                case BluetoothAdapter.STATE_OFF:
	                	mBluetoothStatus.setText(R.string.BluetoothIsClosed);
	                	break;
	                case BluetoothAdapter.STATE_ON:
	                	_bluetooth.startDiscovery();
	                	mBluetoothStatus.setText(R.string.BluetoothIsScaning);
	                	break;
	                case BluetoothAdapter.STATE_TURNING_ON:
	                	mBluetoothStatus.setText(R.string.BluetoothIsTurningOn);
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
			mBluetoothStatus.setText(R.string.BluetoothIsScaning);
			showDevices();
		}
	};
	private BroadcastReceiver _discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			_discoveryFinished = true;
			mBluetoothStatus.setText(R.string.BluetoothScanFinish);	
		}
	};
    
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
	
	private void showDevices()
	{
		StringBuilder b = new StringBuilder();
		for (int i = 0, size = _devices.size(); i < size; ++i)
		{
			BluetoothDevice d = _devices.get(i);
			b.append(new Integer(i + 1).toString() + ":");   
			b.append(d.getAddress());
			b.append("  ");
			b.append(d.getName()+'\n');
			
		}
		mBluetoothInfo.setText(b.toString());
		if(_devices != null && _devices.size() > 0) {
			SendBroadcast(Config.PASS);
		}
	}

	@Override
	public void onDestroy() {
		if (!defBlueOpen && _bluetooth.isEnabled())
		{
			_bluetooth.disable();
		}
        if(mBroadcastReceiver != null) unregisterReceiver(mBroadcastReceiver);
		if(_foundReceiver != null) unregisterReceiver(_foundReceiver);
		if(_discoveryReceiver != null) unregisterReceiver(_discoveryReceiver);
		super.onDestroy();
	} 
}
