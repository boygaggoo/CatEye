
//file create by liunianliang 20130718~20130724

package com.rgk.factory.earphone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.KeyEvent;
import android.os.Handler;
import android.graphics.Color;

public class EarPhone extends Activity implements View.OnClickListener,MediaPlayer.OnCompletionListener {
    
	public static String TAG = "EarPhone";
	
	private Button mRecord;
	private TextView mRecordTime;
	private AudioManager mAudioManager;
	private File mRecAudioFile;
	private File mRecAudioPath;
	private MediaRecorder mMediaRecorder;
	private String	strTempFile	= "record_";
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private boolean playfinish = false;
	private boolean hadSendBroadcast = false;
	private TextView keyHook;
	private boolean isdown = false;
	private Handler handler = new Handler();

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Earphone);
        setContentView(R.layout.earphone);
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.earphone_layout);
        mLayout.setSystemUiVisibility(0x00002000);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        
        mRecord = (Button)findViewById(R.id.earphone_info);
        mRecordTime = (TextView)findViewById(R.id.record_time);
	 keyHook = (TextView)findViewById(R.id.key_hook);
	 
        mRecordTime.setVisibility(View.INVISIBLE);
        
        mRecord.setClickable(mAudioManager.isWiredHeadsetOn());
        mRecord.setEnabled(mAudioManager.isWiredHeadsetOn());
        
        ((Button)findViewById(R.id.earphone_pass)).setOnClickListener(this);
        ((Button)findViewById(R.id.earphone_fail)).setOnClickListener(this);
        mRecord.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mBroadcastReceiver, filter);
    }

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent){
    		Log.e(TAG,"action="+intent.getAction());
            if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)){
            	boolean status = intent.getIntExtra("state", 0)==1;
            	mRecord.setClickable(status);
            	mRecord.setEnabled(status);
            	mRecord.setText(status ? R.string.start_record : R.string.insert_earphone);
		if(status) {
                  keyHook.setVisibility(View.VISIBLE);
		} else {
                 keyHook.setVisibility(View.GONE);
		}
            }
    	}

    };

	private Handler mHandler;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.earphone_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.earphone_fail:
				SendBroadcast(Config.FAIL);
				break;
			case R.id.earphone_info:
				HandlerRecord();
				break;
		}	
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_HEADSETHOOK:
			Log.e("dsg","onKeyUp");
			 if (isdown) {
                          keyHook.setText(R.string.Hook_Key_down);
			    keyHook.setTextColor(Color.GREEN);
			    handler.removeCallbacks(runnable);
			    isdown = false;
		        }
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_HEADSETHOOK:
			Log.e("dsg","onKeyDown");
                          keyHook.setText(R.string.Hook_Key_down);
			    keyHook.setTextColor(Color.RED);
			    handler.postDelayed(runnable, 1);//ms	
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				 isdown = true;
				 Log.e("dsg","runnable"+isdown);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("dsg","runnable"+e);
			}

		}

	};
	public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			finish();
		}
	}

	private void HandlerRecord() {
		String Text = mRecord.getText().toString();
		mRecord.setText(Text.equals(getResources().getString(R.string.start_record)) ? 
		           R.string.stop_record : R.string.start_record);
		if(Text.equals(getResources().getString(R.string.start_record))){
			try
			{
				//start to record
				if(mMediaPlayer != null) {
					mMediaPlayer.release();
					mMediaPlayer = null;
				}
				playfinish = false;
				mRecordTime.setVisibility(View.INVISIBLE);
				mRecAudioFile = File.createTempFile(strTempFile, ".amr");
				Log.e(TAG,"mRecAudioFile="+mRecAudioFile);
				mMediaRecorder = new MediaRecorder();
				mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				mMediaRecorder.setOutputFile(mRecAudioFile.getAbsolutePath());
				mMediaRecorder.prepare();
				mMediaRecorder.start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}else{
			if(mMediaRecorder != null) {
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
			//start to play
			//mRecordTime.setVisibility(View.VISIBLE);
			mMediaPlayer = MediaPlayer.create(getApplicationContext(),
					Uri.parse(mRecAudioFile.getAbsolutePath()));
			mMediaPlayer.setLooping(false);
	        try {
				mMediaPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        mMediaPlayer.start();        
	        mMediaPlayer.setOnCompletionListener(this);
	        DisplayTime();
		}
		
	}

	private void DisplayTime() {
        mHandler = new Handler();
        //mHandler.post(update);	
	}
	
	private Runnable update = new Runnable() {
		
		@Override
		public void run() {
			if(!playfinish){
				if(mMediaPlayer != null) 
				mRecordTime.setText(makeTimeString(mMediaPlayer.getCurrentPosition()));
				mHandler.postDelayed(update, 1);
			}
		}
	};

	@Override
	public void onCompletion(MediaPlayer mp) {
		mMediaPlayer.release();	
		playfinish = true;
	}
	
	private void DeleteRecordFile() throws FileNotFoundException {
		if(mRecAudioFile != null) {
		   File file = new File(mRecAudioFile.getAbsolutePath());
		   if(file.exists()){
			   Log.e(TAG,"delete the record file when exit");
			   file.delete();
		   }
		}
		
	}  
	
	@Override
	public void onDestroy() {
        if(mMediaPlayer != null) mMediaPlayer.release();
        if(mMediaRecorder != null) mMediaRecorder.release();
        unregisterReceiver(mBroadcastReceiver);
        try {
			DeleteRecordFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(mHandler != null && update != null) {
			mHandler.removeCallbacks(update);
		}
		super.onDestroy();
	}
	
	public static String makeTimeString(long milliSecs) {
	       StringBuffer sb = new StringBuffer();
	       long m = milliSecs / (60 * 1000);
	       sb.append(m < 10 ? "0" + m : m);
	       sb.append(":");
	       long s = (milliSecs % (60 * 1000)) / 1000;
	       sb.append(s < 10 ? "0" + s : s);
	       return sb.toString();
	    
	     }

}
