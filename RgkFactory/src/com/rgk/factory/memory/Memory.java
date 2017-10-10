
//dushiguang 2014.08.14

package com.rgk.factory.memory;


import java.io.IOException;
import java.io.InputStreamReader;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Intent;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import android.content.Context;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import java.io.FileReader;
import java.io.BufferedReader;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;

public class Memory extends Activity implements View.OnClickListener {
	
    public static String TAG = "Memory";
    private boolean hadSendBroadcast = false;
    private TextView mTextView;
	private int toTmemory = 0;
    private int Avamemory = 0;
	private long memory=0;
	private Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Memory);
        setContentView(R.layout.memory);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.memory_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        //execSystemCmd();
		mTextView = (TextView)findViewById(R.id.memory_info);
		
        ((Button)findViewById(R.id.memory_pass)).setOnClickListener(this);
        ((Button)findViewById(R.id.memory_fail)).setOnClickListener(this);
		
        mTextView.setText(getResources().getString(R.string.Totmemory_info) + this.getTotalMemory() + "\n" + 
			             getResources().getString(R.string.Avamemory_info) + this.getAvailMemory());			

		startMenoryTest();
    }

    public void startMenoryTest() {
		mHandler.removeCallbacks(runnable);
		mHandler.postDelayed(runnable, 2000);//ms
	}
    private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				 if (memory > 0) {
				 	SendBroadcast(Config.PASS);
				 } else {
                    SendBroadcast(Config.FAIL);
				 }
			    //Thread.currentThread().sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};
	
	private String getAvailMemory() {
    	
    	ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mMemoryInfo = new MemoryInfo();
        mActivityManager.getMemoryInfo(mMemoryInfo);
    	return Formatter.formatFileSize(getBaseContext(), mMemoryInfo.availMem);
    }
    
    private String getTotalMemory() {
    	String mfileStr = "/proc/meminfo";
    	String str2;
    	String[] arrayOfString = null;
		long initial_memory = 0;
    	FileReader mFileReader;
    	BufferedReader mfileBuff;
    	try {
			mFileReader = new FileReader(mfileStr);
			mfileBuff = new BufferedReader(mFileReader);
			str2 = mfileBuff.readLine();
			
			arrayOfString = str2.split("\\s+");
			for(String num:arrayOfString) {
				Log.i(str2,num+"\t");
			}
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue()*1024;
			memory = initial_memory;
			mfileBuff.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
    	return Formatter.formatFileSize(getBaseContext(), initial_memory);
    }

	private void execSystemCmd() {
		   String cmd = "df ";
		   Process p = null;
		   try {
			p = Runtime.getRuntime().exec(cmd);
		   } catch (IOException e) {
			e.printStackTrace();
		   }
		   InputStreamReader reader = new InputStreamReader(p.getInputStream());
		   BufferedReader br = new BufferedReader(reader);
		   String result = null;
		   try {
			while((result = br.readLine()) != null){
				((TextView)findViewById(R.id.memory_info)).append(result+"\n");
			   }
		} catch (IOException e) {
			e.printStackTrace();
		}
		   try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		   p.destroy();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		   case R.id.memory_pass:
			   SendBroadcast(Config.PASS);
			   break;
		   case R.id.memory_fail:
			   SendBroadcast(Config.FAIL);
			   break;
		}
	}
	
	public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
		    mHandler.removeCallbacks(runnable);
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			mHandler.removeCallbacks(runnable);
			finish();
		}
	}
}
