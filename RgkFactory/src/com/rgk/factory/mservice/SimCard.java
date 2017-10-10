/**
 * @author shiguang.du
 * @date 2014.06.17
 */
package com.rgk.factory.mservice;
import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.util.Log;
import android.content.ContentResolver;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class SimCard extends Service {
	
    public static String TAG = "SimCard";
	private ITelephonyEx mTelephonyEx;
	private ITelephony mTelephony;
	private boolean hadSendBroadcast = false;
    private Context context;
    private boolean hasSim1 = false;
    private boolean hasSim2 = false;
    @Override
    public void onCreate() {
    	
    	Log.d("dsg", "=====SimCard==>>onCreate()");
		mTelephony = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
        mTelephonyEx = ITelephonyEx.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICEEX));
       
        hasSim1 = hasIccCard(0);
        hasSim2 = hasIccCard(1);
        
        if(hasSim1 && hasSim2) {
        	SendBroadcast(Config.PASS);
        } else {
        	SendBroadcast(Config.FAIL);
        }
        
    }
    
    public boolean hasIccCard(int slot) {
        boolean bRet = false;
		int i;
            try {
                final TelephonyManagerEx iTelephony1 = new TelephonyManagerEx(context);
                //TelephonyManagerEx iTelephony1 = new TelephonyManagerEx(context);
                if (null != iTelephony1) {
                    //bRet = iTelephony1.isSimInsert(slot);
                    i = iTelephony1.getSimState(slot);
					Log.d("dsg","i=="+i);
					if(i == 1) {
                       bRet = false;
		            } else {
                       bRet = true;
					}
                }
            } catch (Exception ex) {
                Log.d(TAG, "isSimInsert: fail");
                ex.printStackTrace();
            }	  
        return bRet;
    }
    	
		public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			stopSelf();
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

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d("dsg", "=====SimCard==>>onDestroy()");			
		Intent serviceIntent = new Intent(SimCard.this, Memory.class);
		startService(serviceIntent);
		Log.d("dsg", "=====Memory==>>>startMemoryService()");
        super.onDestroy();
        Log.d("dsg", "=====Memory==>>>onDestroy()");
		super.onDestroy();
	}


}
