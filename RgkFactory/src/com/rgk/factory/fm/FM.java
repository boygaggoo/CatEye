//file create by liunianliang 20130718~20130724

package com.rgk.factory.fm;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.media.MediaPlayer;

import java.io.IOException;

import com.rgk.factory.fm.FMNative; //should do something in mediatek\external\fmradio\fmr\libfm_jni.cpp

public class FM extends Activity implements View.OnClickListener {

	public static String TAG = "FM";

	private Button mFMinfo;
	private AudioManager mAudioManager;
	private MediaPlayer mFMPlayer = null;
	private boolean hadSendBroadcast = false;
	private boolean hasHeadSet = false;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setTitle(R.string.FM);
		setContentView(R.layout.fm);
		RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.fm_layout);
        mLayout.setSystemUiVisibility(0x00002000);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		mFMinfo = (Button) findViewById(R.id.fm_info);

		mFMinfo.setClickable(mAudioManager.isWiredHeadsetOn());
		mFMinfo.setEnabled(mAudioManager.isWiredHeadsetOn());

		((Button) findViewById(R.id.fm_pass)).setOnClickListener(this);
		((Button) findViewById(R.id.fm_fail)).setOnClickListener(this);
		mFMinfo.setOnClickListener(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_HEADSET_PLUG);
		filter.addAction("return_result_form_fm");
		filter.addAction("exit_fm_cit_test");
		registerReceiver(mBroadcastReceiver, filter);

		mFMPlayer = new MediaPlayer();
		mFMPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
		mFMPlayer.setAudioStreamType(AudioManager.STREAM_FM);

		startActivityForResult(new Intent().setClassName(
				"com.mediatek.FMRadio",
				"com.mediatek.FMRadio.FMRadioCITActivity"), 1000);
		// then the test should let fmradio to do
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.e(TAG, "action=" + intent.getAction());
			if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
				boolean status = intent.getIntExtra("state", 0) == 1;
				hasHeadSet = status;
				mFMinfo.setClickable(status);
				mFMinfo.setEnabled(status);
				mFMinfo.setText(status ? R.string.start_scan
						: R.string.insert_earphone_to_openfm);
			} else if (intent.getAction().equals("return_result_form_fm")) {
				String result = intent.getStringExtra("result");
				if (result.equalsIgnoreCase("ok") && hasHeadSet) {
					SendBroadcast(Config.PASS);
				} else {
					SendBroadcast(Config.FAIL);
				}
			} else if (intent.getAction().equals("exit_fm_cit_test")) {
				String exit = intent.getStringExtra("exit");
				if (exit.equals("true")) {
					finish();
				}
			}
		}

	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.fm_pass:
			SendBroadcast(Config.PASS);
			break;
		case R.id.fm_fail:
			SendBroadcast(Config.FAIL);
			break;
		case R.id.fm_info:
			HandlerSerach();
			break;
		}

	}

	private void HandlerSerach() {
		String Text = mFMinfo.getText().toString();
		short[] shortChannels = null;
		int[] iChannels = null;
		mFMinfo.setText(Text.equals(getResources().getString(
				R.string.start_scan)) ? R.string.stop_scan
				: R.string.start_scan);
		if (Text.equals(getResources().getString(R.string.start_scan))) {
			if (FMNative.opendev()) {
				Log.e(TAG, "------open sucessful--------");
				// if(FMNative.tune((float)87.5)){};
				int channels[] = startScan();
			}
		} else {
			boolean stop = stopScan();
		}
	}

	private int[] startScan() {

		// setRDS(false);
		// enableFMAudio(false);

		int[] iChannels = null;
		short[] shortChannels = null;
		shortChannels = FMNative.autoscan();
		if (null != shortChannels) {
			int size = shortChannels.length;
			iChannels = new int[size];
			for (int i = 0; i < size; i++) {
				iChannels[i] = shortChannels[i];
				Log.e(TAG, "iChannels=" + shortChannels[i]);
			}
		}
		// setRDS(true);

		return iChannels;
	}

	private boolean stopScan() {
		return FMNative.stopscan();
	}

	private int setRDS(boolean on) {

		int ret = -1;
		if (isRDSSupported()) {
			ret = FMNative.rdsset(on);
		}
		return ret;
	}

	public boolean isRDSSupported() {
		boolean isRDSSupported = (FMNative.isRDSsupport() == 1);
		return isRDSSupported;
	}

	private void enableFMAudio(boolean enable) {
		Log.d(TAG, ">>> " + enable);
		if ((mFMPlayer == null)) {
			Log.w(TAG, "mFMPlayer is null in Service.enableFMAudio");
			return;
		}

		if (!enable) {
			if (!mFMPlayer.isPlaying()) {
				Log.d(TAG, "warning: FM audio is already disabled.");
				return;
			}
			Log.d(TAG, "stop FM audio.");
			mFMPlayer.stop();
			return;
		}

		if (mFMPlayer.isPlaying()) {
			Log.d(TAG, "warning: FM audio is already enabled.");
			return;
		}

		try {
			mFMPlayer.prepare();
			mFMPlayer.start();
		} catch (IOException e) {
			Log.e(TAG, "Exception: Cannot call MediaPlayer prepare.");
		} catch (IllegalStateException e) {
			Log.e(TAG, "Exception: Cannot call MediaPlayer prepare.");
		}

	}

	public void SendBroadcast(String result) {
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra(
					"test_item", TAG));
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onActivityResult");
		if (1000 == requestCode) {
			if (resultCode == RESULT_OK) {
				Log.i(TAG, "RESULT_OK");
				//SendBroadcast(Config.PASS);
			} else {
				Log.i(TAG, "RESULT_FAIL");
				//SendBroadcast(Config.FAIL);
			}
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

}
