/**
 *dushiguang2014.07.25
 */


package com.rgk.factory.telephony;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.os.Handler;
import android.provider.CallLog;

public class Telephony extends Activity implements View.OnClickListener {
    
	public static String TAG = "Telephony";
    public Button buttonPass,buttonFail;
    private boolean hadSendBroadcast = false;
    private Handler mHandler = new Handler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Telephony);
		setContentView(R.layout.telephony);   
		LinearLayout mLayout = (LinearLayout) findViewById(R.id.telephony_layout);
        mLayout.setSystemUiVisibility(0x00002000);
        buttonPass = ((Button)findViewById(R.id.telephony_pass));
        buttonPass.setOnClickListener(this);
        buttonFail= ((Button)findViewById(R.id.telephony_fail));
        buttonFail.setOnClickListener(this);
		//callPhony();  
		mHandler.postDelayed(callPhonyrunnable, 0);//����
    }

	private Runnable callPhonyrunnable = new Runnable() {
		@Override
		public void run() {
			try {
			    callPhony(); 
			    Thread.currentThread().sleep(2000);
			    buttonIsVisible();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};
    public void callPhony(){
    	Intent callintent = new Intent(Intent.ACTION_CALL_PRIVILEGED);
        callintent.setData(Uri.fromParts("tel", "112", null));
        callintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callintent); 
		//buttonIsVisible();
    }
    public void clearCallLog() {
    	ContentResolver resolver = getContentResolver();
    	Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, "number=?", 
    			new String[]{"112"}, null);
    	if(cursor.moveToFirst()){
    		String id = cursor.getString(cursor.getColumnIndex("_id"));
    		resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id});
    	}
    }
    public void buttonIsVisible(){
    	buttonPass.setVisibility(View.VISIBLE);
    	buttonFail.setVisibility(View.VISIBLE);
    }
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.telephony_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.telephony_fail:
				SendBroadcast(Config.FAIL);
				break;
		}	
	}

	@Override
	protected void onPause() {
		clearCallLog();
		super.onPause();
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
	
}
