package com.rgk.factory.hall;
import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Hall extends Activity implements View.OnClickListener{
	public static String TAG = "Hall";
    public Button buttonPass,buttonFail;
    private boolean hadSendBroadcast = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.hall);
        setContentView(R.layout.hall);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.hall_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
	    buttonPass = ((Button)findViewById(R.id.hall_pass));
        buttonPass.setOnClickListener(this);
        buttonFail= ((Button)findViewById(R.id.hall_fail));
        buttonFail.setOnClickListener(this);
    }
    
    @Override
	protected void onResume() {
    	Settings.System.putInt(getContentResolver(), "com_rgk_factory_hall", 1);
		super.onResume();
	}

	@Override
	protected void onPause() {
		Settings.System.putInt(getContentResolver(), "com_rgk_factory_hall", 0);
		super.onPause();
	}

	@Override
    protected void onDestroy() {
		Settings.System.putInt(getContentResolver(), "com_rgk_factory_hall", 0);
        super.onStop();
    }
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.hall_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.hall_fail:
				SendBroadcast(Config.FAIL);
				break;
		}	
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.i(TAG, "dispatchKeyEvent:"+event.getKeyCode());
		if(event.getKeyCode() == event.KEYCODE_F2) {
			SendBroadcast(Config.PASS);
		}
		return true;
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
