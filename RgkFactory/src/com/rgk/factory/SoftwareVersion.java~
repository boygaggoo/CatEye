package com.rgk.factory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mediatek.common.featureoption.FeatureOption;
import android.os.SystemProperties;
import com.android.internal.telephony.PhoneConstants;
//import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.TelephonyProperties;
//import com.android.internal.telephony.gemini.GeminiPhone;
//import com.android.internal.telephony.Phone;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.os.Handler;
import android.os.Message;
import android.os.AsyncResult;
import com.mediatek.telephony.TelephonyManagerEx;

import android.os.Build;
public class SoftwareVersion extends Activity{
	
	private Context context;
	
	/**************************************************************************
    private static final String KEY_IMEI_SLOT1 = "imei_slot1";
    private static final String KEY_IMEI_SLOT2 = "imei_slot2";
    private static final String KEY_IMEI_SLOT3 = "imei_slot3";
    private static final String KEY_IMEI_SV_SLOT1 = "imei_sv_slot1";
    private static final String KEY_IMEI_SV_SLOT2 = "imei_sv_slot2";
    private static final String KEY_IMEI_SV_SLOT3 = "imei_sv_slot3";
    private static final String KEY_PRL_VERSION_SLOT1 = "prl_version_slot1";
    private static final String KEY_PRL_VERSION_SLOT2 = "prl_version_slot2";
    private static final String KEY_PRL_VERSION_SLOT3 = "prl_version_slot3";
    private static final String KEY_MEID_NUMBER_SLOT1 = "meid_number_slot1";
    private static final String KEY_MEID_NUMBER_SLOT2 = "meid_number_slot2";
    private static final String KEY_MEID_NUMBER_SLOT3 = "meid_number_slot3";
    private static final String KEY_MIN_NUMBER_SLOT1 = "min_number_slot1";
    private static final String KEY_MIN_NUMBER_SLOT2 = "min_number_slot2";
    private static final String KEY_MIN_NUMBER_SLOT3 = "min_number_slot3";
    private static final String CDMA = "CDMA";
    //private GeminiPhone mGeminiPhone = null;
    **************************************************************************/ 
    private static final String KEY_IMEI_SLOT1 = "imei_slot1";
    private static final String KEY_IMEI_SLOT2 = "imei_slot2";
    private static final String KEY_IMEI_SLOT3 = "imei_slot3";
    //private GeminiPhone mGeminiPhone = null;
	private static final String CDMA = "CDMA";
	private static final String TAG = "SoftwareVersion";
	
	//AP Version
	private static TextView m_isoftware_text = null;
	private static TextView m_internal_content = null;
	
	//Modem_version
	private static TextView m_modem_text = null;
	private static TextView m_modem_content = null;
	
	//PSN
	private static TextView	m_PSN_text = null;
	private static TextView	m_PSN_content = null;
	
	//IMEI1
	private static TextView	m_IMEI1_text = null;
	private static TextView	m_IMEI1_content = null;
	
	//IMEI2
	private static TextView	m_IMEI2_text = null;
	private static TextView	m_IMEI2_content = null;
	
	//Blutooth address
	private static TextView	m_Blutooth_text = null;
	private static TextView	m_Blutooth_content = null;
	
	//Wifi address
	private static TextView	m_Wifi_text = null;
	private static TextView	m_Wifi_content = null;

	private static TextView m_esoftware_text = null;
	private static TextView m_external_content = null;
	
    private static TextView m_buildtime_text = null;

	private static TextView m_build_type = null;
	private static TextView m_build_type_content = null;
	
	private static TextView m_lcd_info = null;
	private static TextView m_lcd_content = null;
	private static TextView m_camera_info = null;
	private static TextView m_camera_content = null;

	private static TextView m_memory_info = null;
	private static TextView m_memory_content = null;
	
	private static StringBuffer m_lcd_buffer = null;
	private static StringBuffer m_camera_buffer = null;
	private static StringBuffer m_memory_buffer = null;
	private static StringBuffer temp_buffer = null;
	private static StringBuffer m_version_buffer = null;

	private static final int TP_INFOR_LENTH = 7;
	private static TextView m_tpVersion_info = null;
	private static TextView m_tpVersion_text = null;
	private static TextView m_calibration_text = null;
	//String[] type_array = {"TP IC: ","TP vendor: ","TP only id: ","Current TP fw version: ","TP i2c addr: "};


	private static final String UNKNOWN = "unknown";
	private static final String MODEM = getString(TelephonyProperties.PROPERTY_BASEBAND_VERSION);

	private TelephonyManagerEx tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setTitle(R.string.soft_version);
		setContentView(R.layout.software_version);
		LinearLayout mLayout = (LinearLayout) findViewById(R.id.software_version_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
		context = SoftwareVersion.this;
		tm = new TelephonyManagerEx(context);;     
		initView();
		showContest();
		//bluetoothAddress();
		wifiAddress();
		//showIMEI();
	}
	private static String getString(String property) {
            	return SystemProperties.get(property, UNKNOWN);
     }
	private void initView() {

        m_buildtime_text = (TextView) findViewById(R.id.build_time);
        
        //AP version
		m_isoftware_text = (TextView) findViewById(R.id.internal_vertion_text);		
		m_internal_content = (TextView) findViewById(R.id.vertion_content);
		
		//Modem version
		m_modem_text = (TextView) findViewById(R.id.modem_vertion_text);
		m_modem_content = (TextView) findViewById(R.id.modem_content);
		
		//PSN
		m_PSN_text = (TextView) findViewById(R.id.PSN_text);
		m_PSN_content = (TextView) findViewById(R.id.PSN_content);
		
		//IMEI1
		m_IMEI1_text = (TextView) findViewById(R.id.IMEI1_text);
		m_IMEI1_content = (TextView) findViewById(R.id.IMEI1_content);
		
		//IMEI2
		m_IMEI2_text = (TextView) findViewById(R.id.IMEI2_text);
		m_IMEI2_content = (TextView) findViewById(R.id.IMEI2_content);
		
		//Blutooth Address
		//m_Blutooth_text = (TextView) findViewById(R.id.blutooth_text);
		//m_Blutooth_content = (TextView) findViewById(R.id.blutooth_content);
		
		//Wifi address
		m_Wifi_text = (TextView) findViewById(R.id.wifi_text);
		m_Wifi_content = (TextView) findViewById(R.id.wifi_content);
		
		m_esoftware_text = (TextView) findViewById(R.id.external_vertion_text);		
		m_external_content = (TextView) findViewById(R.id.external_content);
				
		m_build_type =  (TextView) findViewById(R.id.build_type);
		m_build_type_content = (TextView) findViewById(R.id.build_type_content);
		
		m_lcd_info = (TextView) findViewById(R.id.lcd_info);
		m_lcd_content = (TextView) findViewById(R.id.lcd_content);
		
		m_camera_info = (TextView) findViewById(R.id.camera_info);
		m_camera_content = (TextView) findViewById(R.id.camera_content);
		
		m_memory_info =  (TextView) findViewById(R.id.memory_info);
		m_memory_content =  (TextView) findViewById(R.id.memory_content);
		

		m_tpVersion_info = (TextView) findViewById(R.id.tp_infor_text);
		m_tpVersion_text = (TextView) findViewById(R.id.tp_infor_content);
		
		m_calibration_text = (TextView) findViewById(R.id.calibration);
				
		m_camera_buffer = new StringBuffer();
		m_memory_buffer = new StringBuffer();
		temp_buffer = new StringBuffer();
		m_lcd_buffer = new StringBuffer();
		
		m_version_buffer = new StringBuffer();
		//temp_buffer.append("TP Version Info:\n\n");
		
		/***************************************************************
		 * LCD start
		**************************************************************/
		try {
			FileInputStream mFileInputStream = new FileInputStream("/proc/rgk_lcdInfo");
			InputStreamReader mInputStreamReader = new InputStreamReader(mFileInputStream);
			BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
			String ch = null;
			while ((ch = mBufferedReader.readLine()) != null) {
				m_lcd_buffer.append(ch);
			}
			mFileInputStream.close();
		} catch (Exception e) {
			m_lcd_buffer.append("No lcdinfo file found");
			Log.i("dsg", "No lcdinfo file found");
			m_lcd_buffer.append(e);
            Log.w("dsg", "Error reading lcdinfo file", e);
		}		
		/***************************************************************
		 * LCD end
		**************************************************************/
		
		/***************************************************************
		 * Camera start
		**************************************************************/
		try {
			FileInputStream mFileInputStream = new FileInputStream("/proc/rgk_cameraInfo");
			InputStreamReader mInputStreamReader = new InputStreamReader(mFileInputStream);
			BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
			
			String cameraString = null;
			if((cameraString = mBufferedReader.readLine()) != null) {
				String[] cameraCh = cameraString.split(",");
				m_camera_buffer.append(cameraCh[0]).append("\n");
				m_camera_buffer.append(cameraCh[1]);
		    } else {
		    	//SystemProperties.get("ro.build.date","unknow");
		    	m_camera_buffer.append("unknow");
		    }
			mFileInputStream.close();
		} catch (Exception e) {
			m_camera_buffer.append("No cameraInfo file found");
			Log.w("dsg", "Error reading cameraInfo file", e);
		}
		/***************************************************************
		 * Camera end
		**************************************************************/	
		
		/***************************************************************
		 *  AP Version start
		**************************************************************/
		try {
			FileInputStream mFileInputStream = new FileInputStream("/system/build.prop");
			InputStreamReader mInputStreamReader = new InputStreamReader(mFileInputStream);
			BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
			
			String versionString = null;
			//m_version_buffer.append("unknow");
			String[] versionCh = null;
			while((versionString = mBufferedReader.readLine()) != null) {
				versionCh = versionString.split("=");
				if (versionCh[0].equals("ro.internal.version")) {
            		m_version_buffer.append(versionCh[1]);
            		Log.d("dsg","******"+m_version_buffer);
            	}
		    }		
			mFileInputStream.close();
		} catch (Exception e) {
			m_version_buffer.append("unknow");
			Log.w("dsg", "Error reading cameraInfo file", e);
		}
		/***************************************************************
		 * AP Version end
		**************************************************************/
	}
	
	/***************************************************************
	 * Blutooth
	**************************************************************/
    public void bluetoothAddress() {
    	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	String addressStr = mBluetoothAdapter.getAddress();
    	m_Blutooth_content.setText(addressStr);
    }
	/***************************************************************
	 * Wifi
	**************************************************************/
    public void wifiAddress() {
        WifiManager mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()) {
        	mWifiManager.setWifiEnabled(true);
        }
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        //String wifiAddress = int2Ip(mWifiInfo.getIpAddress());
        String wifiAddress = mWifiInfo.getMacAddress();
        m_Wifi_content.setText(wifiAddress);
    }
    private String int2Ip(int i) {
    	return (i & 0xFF)+"."+((i>>8)&0xFF) + "."+((i>>16) & 0xFF)+"."+((i>>24) & 0xFF);
    }
    
	private void showContest() {
		m_isoftware_text.setText("AP Version:");
		m_internal_content.setText(m_version_buffer);
		
		m_lcd_info.setText("Lcd:");
		m_lcd_content.setText(m_lcd_buffer);
		
		m_camera_info.setText("Camera");
		m_camera_content.setText(m_camera_buffer);

		m_modem_content.setText(MODEM);

		Log.d("dsg", "tm.getDeviceId(0)"+tm.getDeviceId(0));
		Log.d("dsg", "tm.getDeviceId(0)"+tm.getDeviceId(1));
		m_IMEI1_content.setText(tm.getDeviceId(0));
		m_IMEI2_content.setText(tm.getDeviceId(1));

        
		String serial = Build.SERIAL;
		if(serial.equals("")) {
            m_PSN_content.setText("UNKNOWN");
 		} else {
            m_PSN_content.setText(serial);
		}
		showTPInfo();
	}
	private void showTPInfo() {
		try {
			FileInputStream fis = new FileInputStream("/proc/rgk_tpInfo");
			InputStreamReader isr = new InputStreamReader(fis);
	        BufferedReader br = new BufferedReader(isr, 4096);
	        String ch = br.readLine();
	        if(ch != null){
	        	String[] tp = ch.split(",");
	        	for(int i=0; i<tp.length; i++){
	        		temp_buffer.append(tp[i]).append("\n");
	        	}
	        } else {
	        	temp_buffer.append("TPInfo is null").append("\n");
	        }
	        fis.close();
	    } catch (FileNotFoundException e) {
	    	temp_buffer.append("No TPInfo file found");
	    	Log.i(TAG, "No TPInfo file found");
	    } catch (IOException e) {
	    	temp_buffer.append("Error reading TPInfo file");
	    	temp_buffer.append(e);
	    	Log.w(TAG, "Error reading TPInfo file", e);
        }
		m_tpVersion_info.setText("TP Version:");
		m_tpVersion_text.setText(temp_buffer);
	}	
}


