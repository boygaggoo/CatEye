package com.rgk.factory.sensor;

import com.rgk.factory.Config;
import com.rgk.factory.NvRAMAgentHelper;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SensorCalibration extends Activity implements
		View.OnClickListener, SensorEventListener {

	public static String TAG = "SensorCalibration";

	float hightPix = 0, widthPix = 0;
	float CirclePix = 0, CirclePiy = 0;
	private LinearLayout mLinearLayoutView, mButtonLayout;
	private TextView mStepInfo;
	private TextView mSensorData;
	private TextView mMaxValue;//PSensor max value
	private TextView mMinValue;//PSensor min value
	private Button mStartButton, mRetestButton, mOkButton;
	private boolean secondStep, thirdStep, lastStep, secondStepFail;
	private Handler mHandler;
	private SensorManager sm;
	private Sensor DistanceSensor;
	WindowManager.LayoutParams mLayoutParams;
	private float mBrightness = 1.0f;
	private boolean oneCircle = true, twoCircle, threeCircle;
	private Panel mPanel;

	private int minValue = 0;//no cover sensor
	private int maxValue = 0;//cover sensor
	private int mThresholdMin = 0;
	private int mThresholdMax = 0;

	private final int MSG_STEP1_SUCCESS = 1;
	private final int MSG_STEP2_SUCCESS = 2;
	private final int MSG_DRAW_CIRCLE0 = 3;
	private final int MSG_DRAW_CIRCLE1 = 4;
	private final int MSG_DRAW_CIRCLE2 = 5;
	private final int MSG_DRAW_CIRCLE3 = 6;
	private final int MSG_OPERATION_FAIL = 7;
	private final int MSG_DATA_FAIL = 8;
	private final int MSG_UPDATE_DATA = 9;

	private static final int UPDATE_INTERVAL = 100;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setTitle(R.string.Calibration);
		setContentView(R.layout.calibration);
		RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.calibration);
	    mLayout.setSystemUiVisibility(0x00002000);
		// get panel size
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		hightPix = mDisplayMetrics.heightPixels;
		widthPix = mDisplayMetrics.widthPixels;

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		DistanceSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		mLayoutParams = getWindow().getAttributes();
		mLayoutParams.screenBrightness = 1;
		getWindow().setAttributes(mLayoutParams);

		mLinearLayoutView = (LinearLayout) findViewById(R.id.calibration_view);
		mButtonLayout = (LinearLayout) findViewById(R.id.calibration_button_layout_t);
		CirclePix = widthPix / 2;
		CirclePiy = hightPix / 2 - 150;
		Log.i(TAG, "CirclePix=" + CirclePix + "   " + "CirclePiy=" + CirclePiy);

		mStepInfo = (TextView) findViewById(R.id.calibration_step_info);
		mSensorData = (TextView) findViewById(R.id.sensor_data);
		mMaxValue = (TextView) findViewById(R.id.sensor_max_value);
		mMinValue = (TextView) findViewById(R.id.sensor_min_value);
		mStepInfo.setText(getString(R.string.distance_modify_step) + "\n"
				+ getString(R.string.distance_modify_step1));

		mStartButton = (Button) findViewById(R.id.calibration_start_next);
		mRetestButton = (Button) findViewById(R.id.distance_modify_more);
		mOkButton = (Button) findViewById(R.id.distance_modify_yes);
		mStartButton.setOnClickListener(this);
		mRetestButton.setOnClickListener(this);
		mOkButton.setOnClickListener(this);
		((Button) findViewById(R.id.calibration_pass)).setOnClickListener(this);
		((Button) findViewById(R.id.calibration_fail)).setOnClickListener(this);

		mPanel = new Panel(this);
		mLinearLayoutView.addView(mPanel);

		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				int action = msg.what;
				switch (action) {
				case MSG_STEP1_SUCCESS:
					minValue = EmSensor.getPsensorData();
					Log.i(TAG, "getPsensorData minValue = " + minValue);
					// read Threshold value
					getPsensorThreshold();
					mStepInfo.setText(R.string.distance_modify_step2);
					mStartButton.setEnabled(true);
					ResultHandle.SaveResultToSystem(Config.FAIL, TAG);
					sendBroadcast(new Intent(Config.ItemOnClick));
					break;
				case MSG_STEP2_SUCCESS:
					// Modify by sunjie begin
					/*
					mStepInfo.setText(R.string.distance_modify_step3);
					mStartButton.setVisibility(View.GONE);
					mButtonLayout.setVisibility(View.VISIBLE);
					lastStep = true;
					mStartButton.setEnabled(true);
					*/
					SendBroadcast(Config.PASS);
					// Modify by sunjie end
					break;
				case MSG_DRAW_CIRCLE0:
					oneCircle = false;
					twoCircle = false;
					threeCircle = false;
					mLinearLayoutView.removeAllViews();
					mLinearLayoutView
							.addView(new Panel(SensorCalibration.this));
					break;
				case MSG_DRAW_CIRCLE1:
					oneCircle = true;
					twoCircle = false;
					threeCircle = false;
					mLinearLayoutView.removeAllViews();
					mLinearLayoutView
							.addView(new Panel(SensorCalibration.this));
					break;
				case MSG_DRAW_CIRCLE2:
					oneCircle = false;
					twoCircle = true;
					threeCircle = false;
					mLinearLayoutView.removeAllViews();
					mLinearLayoutView
							.addView(new Panel(SensorCalibration.this));
					break;
				case MSG_DRAW_CIRCLE3:
					oneCircle = false;
					twoCircle = false;
					threeCircle = true;
					mLinearLayoutView.removeAllViews();
					mLinearLayoutView
							.addView(new Panel(SensorCalibration.this));
					break;
				case MSG_OPERATION_FAIL:
					mStartButton.setEnabled(true);
					mStartButton.setText(R.string.distance_modify_more);
					ResultHandle.SaveResultToSystem(Config.FAIL, TAG);
					sendBroadcast(new Intent(Config.ItemOnClick));
					//SendBroadcast(Config.FAIL);// Add by sunjie
					break;
				case MSG_DATA_FAIL:
					mStartButton.setEnabled(true);
					mStartButton.setText(R.string.distance_modify_more);
					mStepInfo
							.setText(R.string.distance_modify_fail_data_invaild);
					ResultHandle.SaveResultToSystem(Config.FAIL, TAG);
					sendBroadcast(new Intent(Config.ItemOnClick));
					//SendBroadcast(Config.FAIL);// Add by sunjie
					break;
				case MSG_UPDATE_DATA:
					mSensorData.setText("PS:"+EmSensor.getPsensorData());
					mHandler.sendEmptyMessageDelayed(MSG_UPDATE_DATA, UPDATE_INTERVAL);
					break;
				default:
					break;
				}
				// super.handleMessage(msg);
			}

		};

		// read Threshold value
		getPsensorThreshold();
		// read NV value
		NvRAMAgentHelper.readNVData(NvRAMAgentHelper.PSENSOR_INDEX);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.calibration_pass:
			SendBroadcast(Config.PASS);
			break;
		case R.id.calibration_fail:
			SendBroadcast(Config.FAIL);
			break;
		case R.id.calibration_start_next:
			HandlerStartStep();
			break;
		case R.id.distance_modify_more:
			ResetTest();
			break;
		case R.id.distance_modify_yes:
			SendBroadcast(Config.PASS);
			break;
		}
	}

	private void HandlerStartStep() {
		String startButtonText = mStartButton.getText().toString();
		if (startButtonText.equals(getString(R.string.distance_modify_start))) {
			getAlpsInfo();
			secondStep = true;
			mStartButton.setText(R.string.distance_modify_next);
			mStartButton.setEnabled(false);
			EmSensor.clearPsensorCalibration();
			EmSensor.getPsensorMaxValue();
			// Modify by sunjie begin
			/*
			mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE1, 1000);
			mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE2, 2000);
			mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE3, 3000);
			mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE0, 4000);
			mHandler.sendEmptyMessageDelayed(MSG_STEP1_SUCCESS, 4000);
			*/
			mHandler.sendEmptyMessage(MSG_DRAW_CIRCLE2);
			mHandler.sendEmptyMessageDelayed(MSG_STEP1_SUCCESS, UPDATE_INTERVAL);
			// Modify by sunjie end
		} else if (startButtonText
				.equals(getString(R.string.distance_modify_next))) {
			mStartButton.setEnabled(false);
			thirdStep = true;
			mHandler.sendEmptyMessage(MSG_DRAW_CIRCLE3);
			if (secondStepFail) {
				mStepInfo.setText(R.string.distance_modify_fail);
				mHandler.sendEmptyMessage(MSG_OPERATION_FAIL);// Modify by sunjie
			} else {
				mStepInfo.setText(R.string.distance_modify_step2_2);
				maxValue = EmSensor.getPsensorData();
				Log.i(TAG, "getPsensorData maxValue = " + maxValue);
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						checkData();
					}
				}).start();
				// if (checkData()) {
				// mHandler.sendEmptyMessageDelayed(MSG_STEP2_SUCCESS, 4000);
				// mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE0, 4000);
				// mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE3, 1000);
				// mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE2, 2000);
				// mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE1, 3000);
				// } else {
				// mStepInfo
				// .setText(R.string.distance_modify_fail_data_invaild);
				// mHandler.sendEmptyMessageDelayed(MSG_DATA_FAIL, 3000);
				// }
			}
		} else if (startButtonText
				.equals(getString(R.string.distance_modify_more))) {
			ResetTest();
		}
	}

	private void ResetTest() {
		mStartButton.setVisibility(View.VISIBLE);
		mButtonLayout.setVisibility(View.GONE);
		mStartButton.setText(R.string.distance_modify_start);
		mStartButton.setEnabled(true);
		mStepInfo.setText(getString(R.string.distance_modify_step) + "\n"
				+ getString(R.string.distance_modify_step1));
		secondStep = false;
		thirdStep = false;
		lastStep = false;
		oneCircle = true;
		twoCircle = false;
		threeCircle = false;
		mLinearLayoutView.removeAllViews();
		mLinearLayoutView.addView(new Panel(this));
		minValue = 0;
		maxValue = 0;
	}

	private void getAlpsInfo() {
		// getInformation from somewhere such as device
		mStepInfo.setText(R.string.distance_modify_step1_1);
	}

	private void SendBroadcast(String result) {
		ResultHandle.SaveResultToSystem(result, TAG);
		sendBroadcast(new Intent(Config.ItemOnClick));
		sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra(
				"test_item", TAG));
		//if (!Config.FAIL.equalsIgnoreCase(result)) {
			finish();
		//}
	}

	private boolean checkData() {
		boolean result = false;
		Log.i(TAG, "checkData minValue=" + minValue + " maxValue=" + maxValue
				+ " mThresholdMin=" + mThresholdMin + " mThresholdMax="
				+ mThresholdMax);
		if ((mThresholdMax > mThresholdMin) && (maxValue > minValue)
				&& (maxValue > mThresholdMax) && (mThresholdMin > minValue)
				&& (maxValue - minValue > mThresholdMax)) {
			// do calibration
			if (EmSensor.RET_SUCCESS == EmSensor.doPsensorCalibration(minValue,
					maxValue)) {
				result = true;
				// write minValue to NV
				NvRAMAgentHelper.writeNVData(NvRAMAgentHelper.PSENSOR_INDEX,
						minValue);
			} else {
				Log.w(TAG, "checkData doPsensorCalibration failed");
			}
		} else {
			Log.w(TAG, "checkData data invaild");
		}

		if (result) {
			// Modify by sunjie begin
			/*
			mHandler.sendEmptyMessageDelayed(MSG_STEP2_SUCCESS, 4000);
			mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE0, 4000);
			mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE3, 1000);
			mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE2, 2000);
			mHandler.sendEmptyMessageDelayed(MSG_DRAW_CIRCLE1, 3000);
			*/
			mHandler.sendEmptyMessage(MSG_DRAW_CIRCLE0);
			mHandler.sendEmptyMessage(MSG_STEP2_SUCCESS);
			// Modify by sunjie end
		} else {
			mHandler.sendEmptyMessage(MSG_DATA_FAIL);// Modify by sunjie
		}
		return result;
	}

	private void getPsensorThreshold() {
		int[] threshold = new int[2];
		EmSensor.getPsensorThreshold(threshold);
		mThresholdMax = threshold[0];
		mThresholdMin = threshold[1];
		mMaxValue.setText("Max value:"+mThresholdMax);
		mMinValue.setText("Min value:"+mThresholdMin);
		Log.i(TAG, "getPsensorThreshold mThresholdMax=" + mThresholdMax
				+ " mThresholdMin=" + mThresholdMin);
	}

	public class Panel extends View {

		private Paint mPaint = new Paint();

		public Panel(Context context) {
			super(context);
		}

		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			mPaint.setAntiAlias(true);

			mPaint.setStyle(Paint.Style.STROKE);
			{
				mPaint.setColor(Color.BLACK);
				// draw three circle
				/**
				if (secondStep) {
					if (oneCircle) {
						mPaint.setColor(Color.GREEN);
						canvas.drawCircle(CirclePix, CirclePiy, 50, mPaint);
					} else if (twoCircle) {
						mPaint.setColor(Color.GREEN);
						canvas.drawCircle(CirclePix, CirclePiy, 80, mPaint);
					} else if (threeCircle) {
						mPaint.setColor(Color.GREEN);
						canvas.drawCircle(CirclePix, CirclePiy, 110, mPaint);
					} else {
						mPaint.setColor(Color.TRANSPARENT);
						canvas.drawCircle(CirclePix, CirclePiy, 110, mPaint);
						canvas.drawCircle(CirclePix, CirclePiy, 80, mPaint);
						canvas.drawCircle(CirclePix, CirclePiy, 50, mPaint);
					}
				}**/

				mPaint.setColor(Color.BLACK);
				canvas.drawCircle(CirclePix, CirclePiy, 60, mPaint);
				canvas.drawCircle(CirclePix, CirclePiy, 120, mPaint);
				canvas.drawCircle(CirclePix, CirclePiy, 180, mPaint);

				// draw two line
				mPaint.setColor(Color.BLACK);
				canvas.drawLine(CirclePix - 180 - 30, CirclePiy,
						CirclePix + 180 + 30, CirclePiy, mPaint);
				canvas.drawLine(CirclePix, CirclePiy - 180 - 30, CirclePix,
						CirclePiy + 180 + 30, mPaint);
			}

			mPaint.setStyle(Paint.Style.FILL);
			{
				// draw fill green circle
				mPaint.setColor(Color.GREEN);
				canvas.drawCircle(CirclePix, CirclePiy, 40, mPaint);
				// draw black text
				mPaint.setColor(Color.BLACK);
				mPaint.setTextSize(30);
				if (oneCircle) {
					canvas.drawText("1", CirclePix - 7, CirclePiy + 7,
							mPaint);
				} else if (twoCircle) {
					canvas.drawText("2", CirclePix - 7, CirclePiy + 7,
							mPaint);
				} else if (threeCircle) {
					canvas.drawText("3", CirclePix - 7, CirclePiy + 7,
							mPaint);
				}
			}

		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			float X = event.values[0];
			float Y = event.values[1];
			float Z = event.values[2];
			Log.i(TAG, "X=" + X + "   " + "Y=" + Y + "   " + "Z=" + Z);
			Log.i(TAG, "Sensor data = " + EmSensor.getPsensorData());
			if (X == 1.0) {// far away
				if (lastStep)
					mBrightness = 1.0f;
				secondStepFail = true;
			} else {
				if (lastStep)
					mBrightness = 0.01f;
				secondStepFail = false;
			}
			if (lastStep) {
				mLayoutParams = getWindow().getAttributes();
				mLayoutParams.screenBrightness = mBrightness;
				getWindow().setAttributes(mLayoutParams);
			}
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		sm.registerListener(this, DistanceSensor,
				SensorManager.SENSOR_DELAY_UI);
		mHandler.sendEmptyMessage(MSG_UPDATE_DATA);
	}

	@Override
	protected void onStop() {
		sm.unregisterListener(this);
		super.onStop();
	}
}
