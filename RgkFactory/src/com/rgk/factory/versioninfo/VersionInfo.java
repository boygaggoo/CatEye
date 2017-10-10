package com.rgk.factory.versioninfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import com.android.internal.telephony.TelephonyProperties;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;
public class VersionInfo extends Activity implements OnClickListener{
	public static final String TAG = "VersionInfo";
	TextView soft_version,customer_ver,modem_ver,android_ver,kernel_ver,tp_ver;
	Button bt_pass,bt_fail;
	private boolean hadSendBroadcast = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.version_info);
		setTitle(R.string.soft_version);
		RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.versioninfo_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
		initTextView();
		setText();
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onClick(View v) {
		if(v.equals(bt_pass)) {
			SendBroadcast(Config.PASS);
		} else if(v.equals(bt_fail)){
			SendBroadcast(Config.FAIL);
		}
		
	}
	private void initTextView(){
		soft_version = (TextView)findViewById(R.id.soft_version);
		customer_ver = (TextView)findViewById(R.id.customer_ver);
		modem_ver = (TextView)findViewById(R.id.modem_ver);
		android_ver = (TextView)findViewById(R.id.android_ver);
		kernel_ver = (TextView)findViewById(R.id.kernel_ver);
		tp_ver = (TextView)findViewById(R.id.tp_ver);
		bt_pass = (Button)findViewById(R.id.version_pass);
		bt_pass.setOnClickListener(this);
		bt_fail = (Button)findViewById(R.id.version_fail);
		bt_fail.setOnClickListener(this);
	}
	private void setText(){
		soft_version.setText(getInternalVerno());
		customer_ver.setText(getCustomerVerno());
		modem_ver.setText(getModemVerno());
		android_ver.setText(getAndroidVerno());
		kernel_ver.setText(getKernelVerno());
		tp_ver.setText(getTpVerno());
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
	private String getCustomerVerno() {
		return SystemProperties.get("ro.build.display.id","unknow");
	}	
	private String getInternalVerno() {
		return SystemProperties.get("ro.internal.version", "unknow");
	}	
	private String getAndroidVerno() {
		return Build.VERSION.RELEASE;
	}	
	private String getModemVerno() {
		return SystemProperties.get(TelephonyProperties.PROPERTY_BASEBAND_VERSION, "unknow");
	}
	private String getKernelVerno() {
		String result = "";
		try {
    		BufferedReader reader  = new BufferedReader(new FileReader("/proc/version"), 256);
    		String str = "";
			str = reader.readLine();
        	final String PROC_VERSION_REGEX =
                    "Linux version (\\S+) " +
                    "\\((\\S+?)\\) " +
                    "(?:\\(gcc.+? \\)) " +
                    "(#\\d+) " +
                    "(?:.*?)?" +
                    "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)";
                Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(str);
                if (!m.matches()) {
                	result = "Unavailable";
                } else if (m.groupCount() < 4) {
                	result = "Unavailable";
                }
                result = m.group(1);
                reader.close();
        } catch (IOException e) {
        	return "Unavailable";
        }
        return result;
	}
	private StringBuilder getTpVerno() {
		StringBuilder sb = new StringBuilder();
		try {
			FileInputStream fis = new FileInputStream("/proc/rgk_tpInfo");
			InputStreamReader isr = new InputStreamReader(fis);
	        BufferedReader br = new BufferedReader(isr, 4096);
	        String ch = br.readLine();
	        if(ch != null){
	        	String[] tp = ch.split(",");
	        	for(int i=0; i<tp.length; i++){
	        		sb.append(tp[i]).append("\n");
	        	}
	        } else {
	        	sb.append("TPInfo is null").append("\n");
	        }
	        fis.close();
	    } catch (Exception e) {
	    	Log.i(TAG, "No TPInfo file found");
	    }
		return sb;
	}
}
