
//file create by liunianliang 20130718~20130724

package com.rgk.factory.wifi;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;
  
import android.app.Activity;  
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;  
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;  
import android.view.WindowManager;
import android.widget.Button;  
import android.widget.RelativeLayout;
import android.widget.TextView;  
  
public class WiFi extends Activity implements View.OnClickListener {  
  
    public static String TAG = "WiFi";
    
    private TextView mWifiInfo;  
    private TextView mWifiStatusText;
    private WifiAdmin mWifiAdmin;
	private boolean hadSendBroadcast = false;
    private Button wifi_pass; 
    private final static int MSG = 1;
    private boolean defWifiOpen;
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState); 
        setTitle(R.string.WiFi);
        setContentView(R.layout.wifi);  
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.wifi_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        mWifiAdmin = new WifiAdmin(WiFi.this);  
        mWifiAdmin.Createwifilock();
  
        mWifiInfo = (TextView) findViewById(R.id.wifi_textview);
        mWifiStatusText = (TextView)findViewById(R.id.wifistatus_textview);
        
        wifi_pass = (Button)findViewById(R.id.wifi_pass);
        wifi_pass.setOnClickListener(this);
        ((Button)findViewById(R.id.wifi_fail)).setOnClickListener(this);
        defWifiOpen = mWifiAdmin.isWifiEnabled();
        if (!defWifiOpen) {  
        	mWifiAdmin.OpenWifi(); 
        }     
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
    }
    private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			mWifiAdmin.StartScan();
			StringBuilder s = mWifiAdmin.LookUpScan();
			mWifiInfo.setText(s.toString());
			if(s.length() != 0) {
				SendBroadcast(Config.PASS);
			} else {
				mHandler.sendEmptyMessageDelayed(MSG,1000);
			}			
			super.handleMessage(msg);
		}    	
    };
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.wifi_pass:
			SendBroadcast(Config.PASS);
			break;
		case R.id.wifi_fail:
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
            if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                int WifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch(WifiState){
	                case WifiManager.WIFI_STATE_DISABLED:
	                	mWifiStatusText.setText(R.string.WifiNotOpen);
				mHandler.removeMessages(MSG);
				mWifiInfo.setText(null);
				wifi_pass.setEnabled(false);
	                	break;
	                case WifiManager.WIFI_STATE_ENABLED:
	                	mWifiStatusText.setText(R.string.WifiAlreadOpen);
				mHandler.sendEmptyMessageDelayed(MSG,3000);
				wifi_pass.setEnabled(true);
	                	break;
	                case WifiManager.WIFI_STATE_DISABLING:
	                	break;
	                case WifiManager.WIFI_STATE_ENABLING:
	                	mWifiStatusText.setText(R.string.AlreadToOpenWifi);
	                	break;
                }
            }
    	}

    };
    
	@Override
	public void onDestroy() {
        if (!defWifiOpen && mWifiAdmin.isWifiEnabled()) {  
        	mWifiAdmin.CloseWifi(); 
        } 
        mHandler.removeMessages(MSG);
        unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}  
}  
