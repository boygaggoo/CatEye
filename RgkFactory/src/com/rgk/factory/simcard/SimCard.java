/**
 * @author shiguang.du
 * @date 2014.06.17
 */
package com.rgk.factory.simcard;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.internal.telephony.ITelephony;
import com.mediatek.common.telephony.ITelephonyEx;
import com.android.internal.telephony.PhoneConstants;
import android.os.ServiceManager;
import android.content.Context;
import android.os.RemoteException;
import com.mediatek.common.featureoption.FeatureOption;
import com.mediatek.telephony.TelephonyManagerEx;
import android.content.Context;

public class SimCard extends Activity implements View.OnClickListener {
	
    public static String TAG = "SimCard";
	private ITelephonyEx mTelephonyEx;
	private ITelephony mTelephony;
	private boolean hadSendBroadcast = false;
    private Context context;
    boolean bRet = false;
    private BroadcastReceiver mReceiver = null;
    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    TextView sim1,sim2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.SIM_Card);
        setContentView(R.layout.simcard);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.simcard_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
		mTelephony = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
        mTelephonyEx = ITelephonyEx.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICEEX));

        sim1 = (TextView)findViewById(R.id.sim1);
        sim2 = (TextView)findViewById(R.id.sim2);
        
        ((Button)findViewById(R.id.sim_pass)).setOnClickListener(this);
        ((Button)findViewById(R.id.sim_fail)).setOnClickListener(this);
        if(!FeatureOption.MTK_GEMINI_SUPPORT)
        {
        	((TextView)findViewById(R.id.sim2)).setVisibility(View.GONE);
        }
        IntentFilter filter = new IntentFilter(ACTION_SIM_STATE_CHANGED);
        mReceiver = new SimStateReceive();
        registerReceiver(mReceiver, filter);
    }
    
    @Override
	protected void onResume() {        
        sim1.setText(hasIccCard(0) ? R.string.sim1_find : R.string.sim1_notfind);
        sim2.setText(hasIccCard(1) ? R.string.sim2_find : R.string.sim2_notfind);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public boolean hasIccCard(int slot) {
		int i;
            try {
                final TelephonyManagerEx iTelephony1 = new TelephonyManagerEx(context);
                //TelephonyManagerEx iTelephony1 = new TelephonyManagerEx(context);
                if (null != iTelephony1) {
                    //bRet = iTelephony1.isSimInsert(slot);
                    i = iTelephony1.getSimState(slot);
					Log.d("dsg","i=="+i);
					if(i == 5) {
						bRet = true;
		            } else {
		            	bRet = false;
					}
                }
            } catch (Exception ex) {
                Log.d(TAG, "isSimInsert: fail");
                ex.printStackTrace();
            }	  
        return bRet;
    }
    
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		   case R.id.sim_pass:
			   SendBroadcast(Config.PASS);
			   break;
		   case R.id.sim_fail:
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

	    public int getSimInof(int slotId) {
            return FeatureOption.MTK_GEMINI_SUPPORT ? 
                getSimIndicatorGemini(getContentResolver(), mTelephonyEx,slotId) : 
                getSimIndicator(getContentResolver(), mTelephony);
    }

	    public int getSimIndicator(ContentResolver resolver, ITelephony iTelephony) {
				Log.d(TAG,"getSimIndicator for single");
				int indicator = PhoneConstants.SIM_INDICATOR_UNKNOWN;
				if (iTelephony != null) {
					try {
						indicator = iTelephony.getSimIndicatorState();
					} catch (RemoteException e) {
						Log.d(TAG, "RemoteException");
					} catch (NullPointerException ex) {
						Log.d(TAG, "NullPointerException");
					}
				}
				return indicator;
    }
	public int getSimIndicatorGemini(ContentResolver resolver, ITelephonyEx iTelephonyEx, int slotId) {
			int indicator = PhoneConstants.SIM_INDICATOR_UNKNOWN;
			if (iTelephonyEx != null) {
				try {
					indicator = iTelephonyEx.getSimIndicatorState(slotId);
				} catch (RemoteException e) {
					Log.d(TAG, "RemoteException");
				} catch (NullPointerException ex) {
					Log.d(TAG, "NullPointerException");
				}
			}
			return indicator;
		}
	class SimStateReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
				sim1.setText(hasIccCard(0) ? R.string.sim1_find : R.string.sim1_notfind);
		        sim2.setText(hasIccCard(1) ? R.string.sim2_find : R.string.sim2_notfind);
			}
		}
	}
}
