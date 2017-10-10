package com.rgk.factory.tdcoder;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.rgk.factory.R;
import com.rgk.factory.tdcoder.camera.CameraManager;
import com.rgk.factory.tdcoder.decoding.CaptureActivityHandler;
import com.rgk.factory.tdcoder.decoding.InactivityTimer;
import com.rgk.factory.tdcoder.view.ViewfinderView;
import com.rgk.factory.Config;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.os.Handler;

public class CaptureActivity extends Activity implements Callback
{

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	public InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Handler mHandler = new Handler();
       public static String TAG = "Tdcode";
       private boolean hadSendBroadcast = false;
	private boolean testPass = false;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tdcode);
		FrameLayout mLayout = (FrameLayout) findViewById(R.id.tdcode_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
		// CameraManager
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
    
	public void closeDriver() {
		inactivityTimer.shutdown();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface)
		{
			initCamera(surfaceHolder);
		}
		else
		{
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		{
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy()
	{
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder)
	{
		try
		{
			CameraManager.get().openDriver(surfaceHolder);
		}
		catch (IOException ioe)
		{
			return;
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (handler == null)
		{
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (!hasSurface)
		{
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView()
	{
		return viewfinderView;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void drawViewfinder()
	{
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(final Result obj, Bitmap barcode)
	{
	    String ButtonText;
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		if (barcode == null)
		{
			dialog.setIcon(null);
		}
		else
		{
			Drawable drawable = new BitmapDrawable(barcode);
			dialog.setIcon(drawable);
		}
		dialog.setTitle(R.string.TD_results);
		dialog.setCancelable(false);
		if(obj.getText() == null){
			dialog.setMessage(getResources().getString(R.string.fail));
			testPass = false;
			mHandler.postDelayed(runnable, 1000);//ms	fail
	       } else if((obj.getText()).equals(getResources().getString(R.string.Camera_test))) {        	       
		       dialog.setMessage(getResources().getString(R.string.pass));
			testPass = true;
		       mHandler.postDelayed(runnable, 1000);//ms	

		} else {
                      dialog.setMessage(getResources().getString(R.string.fail));
			 testPass = false;
			 mHandler.postDelayed(runnable, 1000);//ms	
		}
		/*
		dialog.setNegativeButton(R.string.fail, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{   
				//
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse("http://"+obj.getText());
				intent.setData(content_url);
				startActivity(intent);
				finish();
				SendBroadcast(Config.FAIL);
			}
		});

		dialog.setPositiveButton(R.string.pass, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SendBroadcast(Config.PASS);
			}
		});
		*/
		dialog.create().show();
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				 if(testPass) {
                                  SendBroadcast(Config.PASS);
				 } else {
                                  SendBroadcast(Config.FAIL);
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};
	public void SendBroadcast(String result){
		mHandler.removeCallbacks(runnable);
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);		
			sendBroadcast(new Intent(Config.ItemOnClick));		
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));		
			finish();	
		}
	}
	private void initBeepSound()
	{
		if (playBeep && mediaPlayer == null)
		{
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try
			{
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			}
			catch (IOException e)
			{
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate()
	{
		if (playBeep && mediaPlayer != null)
		{
			mediaPlayer.start();
		}
		if (vibrate)
		{
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	public void showDialog() {

        new AlertDialog.Builder(this)

        .setTitle(R.string.TD_result_fail)

        .setCancelable(false).show();
	 testPass = false;
	 mHandler.postDelayed(runnable, 1000);//ms	

		
        /*
        .setPositiveButton(R.string.pass, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int i) {
                SendBroadcast(Config.PASS);
            }
        })

        .setNegativeButton(R.string.fail, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int i) {

                SendBroadcast(Config.FAIL);
            }
        }).show();

        */
    }

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener()
	{
		public void onCompletion(MediaPlayer mediaPlayer)
		{
			mediaPlayer.seekTo(0);
		}
	};

}
