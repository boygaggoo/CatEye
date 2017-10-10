
//dushiguang 2014.08.19

package com.rgk.factory.mservice;

import com.rgk.factory.Config;
import com.rgk.factory.ControlCenter.ResultHandle;
    
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;    
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
  
public class WiFi extends Service{  
  
    public static String TAG = "WiFi";
    
    private WifiAdmin mWifiAdmin;

	private Handler mHandler;  
	private final int OUT_TIME = 20 * 1000;
	private boolean testFail = false;
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate() {   
 
        Log.d("dsg", "=====WifiService==>>>onCreate");
        mWifiAdmin = new WifiAdmin(WiFi.this);  
        mWifiAdmin.Createwifilock();
         
        if (!mWifiAdmin.isWifiEnabled()) {  
        	mWifiAdmin.OpenWifi(); 
        } 
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
        
        mHandler = new Handler();
        mHandler.post(update);
       
    }
    
    CountDownTimer mCountDownTimer = new CountDownTimer(OUT_TIME, 3000) {

        @Override
        public void onTick(long arg0) {
      
        }

        @Override
        public void onFinish() {
            if(testFail) {
            	SendBroadcast(Config.PASS);
            } else {
            	SendBroadcast(Config.FAIL);
            }
            Log.d("dsg", "Time_out");
        }
    };
	private Runnable update = new Runnable() {
		
		@Override
		public void run() {
            mWifiAdmin.StartScan();  
            testFail = mWifiAdmin.LookUpScan();  
			mHandler.postDelayed(update, 2);
		}
	};
	
	public void SendBroadcast(String result){
		ResultHandle.SaveResultToSystem(result, TAG);
		stopSelf();
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent){
    		Log.e(TAG,"action="+intent.getAction());
            if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                int WifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch(WifiState){
	                case WifiManager.WIFI_STATE_DISABLED:
	                	Log.d("dsg", "WifiNotOpen");
	                	break;
	                case WifiManager.WIFI_STATE_ENABLED:
						mCountDownTimer.start();
	                	Log.d("dsg", "WifiAlreadOpen");
	                	break;
	                case WifiManager.WIFI_STATE_DISABLING:
	                	break;
	                case WifiManager.WIFI_STATE_ENABLING:
	                	Log.d("dsg", "AlreadToOpenWifi");
	                	break;
                }
            }
    	}

    };
    
	@Override
	public void onDestroy() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        
        if (mWifiAdmin.isWifiEnabled()) {  
        	mWifiAdmin.CloseWifi(); 
        } 
        mHandler.removeCallbacks(update);
        unregisterReceiver(mBroadcastReceiver);
		Intent serviceIntent = new Intent(WiFi.this, Bluetooth.class);
		startService(serviceIntent);
		Log.d("dsg", "=====GPSService==>>>startBluetoothService()");
        super.onDestroy();
        Log.d("dsg", "=====GPSService==>>>onDestroy()");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}  
}  
