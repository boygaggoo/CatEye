
//file create by liunianliang 20130718~20130724

package com.rgk.factory.microphone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class MicrPhone extends Activity implements View.OnClickListener,MediaPlayer.OnCompletionListener {
    
	public static String TAG = "MicrPhone";
	
	private Button mRecord,mPass;
	private TextView mRecordTime;
	private AudioManager mAudioManager;
	private File mRecAudioFile;
	private File mRecAudioPath;
	private MediaRecorder mMediaRecorder;
	private String	strTempFile	= "record_";
	private MediaPlayer mMediaPlayer = null;
	private boolean playfinish = false;
	private boolean hadSendBroadcast = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.Microphone);
        setContentView(R.layout.microphone);
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.microphone_layout);
        mLayout.setSystemUiVisibility(0x00002000);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // D:JWLYB-427 huangkunming 20141015 {
        // mAudioManager.setMode(AudioManager.MODE_RINGTONE);
        // D:}
        
        
        mRecord = (Button)findViewById(R.id.microphone_info);
        mRecordTime = (TextView)findViewById(R.id.microphone_time);
        mRecordTime.setVisibility(View.INVISIBLE);
        mPass = (Button)findViewById(R.id.microphone_pass);      
        ((Button)findViewById(R.id.microphone_pass)).setOnClickListener(this);
        ((Button)findViewById(R.id.microphone_fail)).setOnClickListener(this);
        mRecord.setOnClickListener(this);

    }


	private Handler mHandler;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.microphone_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.microphone_fail:
				SendBroadcast(Config.FAIL);
				break;
			case R.id.microphone_info:
				HandlerRecord();
				break;
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
			mPass.setEnabled(true);
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
			mMediaPlayer = new MediaPlayer();
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
        try {
			DeleteRecordFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
