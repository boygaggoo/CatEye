package com.rgk.factory.otg;
import java.io.FileInputStream;
import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Otg extends Activity implements View.OnClickListener{
	public static String TAG = "Otg";
    public Button buttonPass,buttonFail;
    private TextView tv;
    private boolean hadSendBroadcast = false;
    private static final String OTG_FILE = "/sys/class/switch/otg_state/state";
    private final static int DEVICE = 0;
    private final static int HOST = 1;    
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case DEVICE:
				tv.setText(R.string.otg_state_device);
				break;
			case HOST:
				tv.setText(R.string.otg_state_host);
				SendBroadcast(Config.PASS);
				break;
			}
			if(getOtgState()) {
				handler.sendEmptyMessageDelayed(HOST, 300);
			} else {
				handler.sendEmptyMessageDelayed(DEVICE, 300);
			}
			super.handleMessage(msg);
		}    	
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.otg);
        setContentView(R.layout.otg);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.otg_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
	    buttonPass = ((Button)findViewById(R.id.otg_pass));
        buttonPass.setOnClickListener(this);
        buttonFail= ((Button)findViewById(R.id.otg_fail));
        buttonFail.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.otg_tv);
        if(getOtgState()) {
			handler.sendEmptyMessageDelayed(HOST, 300);
		} else {
			handler.sendEmptyMessageDelayed(DEVICE, 300);
		}
    }
    
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.otg_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.otg_fail:
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
	
	public boolean getOtgState() {
        try {
            FileInputStream fis = new FileInputStream(OTG_FILE);
            int result = fis.read();
            Log.i(TAG, "otg state="+result);
            fis.close();
            return (result != '0');
        } catch (Exception e) {
            return false;
        }
    }
}
