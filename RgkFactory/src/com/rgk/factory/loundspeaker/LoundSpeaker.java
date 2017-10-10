
//file create by liunianliang 20130718~20130724

package com.rgk.factory.loundspeaker;

import java.io.IOException;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public class LoundSpeaker extends Activity implements View.OnClickListener,MediaPlayer.OnCompletionListener{
    
    public static String TAG = "LoundSpeaker";
    
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private AudioManager mAudioManager;
    private boolean hadSendBroadcast = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTitle(R.string.LoundSpeaker);
        setContentView(R.layout.loundspeaker);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.loudspeaker_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
        mAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);  //set to ring mode

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.test);
        mMediaPlayer.setLooping(true);
        try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(this);
        
        ((Button)findViewById(R.id.loundspeaker_pass)).setOnClickListener(this);
        ((Button)findViewById(R.id.loundspeaker_fail)).setOnClickListener(this);
	
    }
    
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.loundspeaker_pass:
				SendBroadcast(Config.PASS);
				break;
			case R.id.loundspeaker_fail:
				SendBroadcast(Config.FAIL);
				break;
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		mMediaPlayer.release();
	}
	
	@Override
	public void onDestroy() {
        if(mMediaPlayer != null) mMediaPlayer.release();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);  //set to normal mode
		super.onDestroy();
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
