package com.rgk.factory.maincamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rgk.factory.Config;
import com.rgk.factory.ControlCenter.ResultHandle;
import com.rgk.factory.R;

import java.io.IOException;
import java.util.List;

public class MainCamera extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    public static final String TAG = "MainCamera";

    private boolean mHadSendBroadcast = false;

    private boolean mHadTackPic = false;

    private WindowManager mWindowManager;

    private Button mFailButton, mPassButton, mTakePictureButton;
    private Camera mCamera = null;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    @Override
    public void onCreate(Bundle icicle) {
        // TODO Auto-generated method stub
        super.onCreate(icicle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LogUtils.logD(TAG, "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        /*
         * To reduce startup time, we start the camera open and preview threads.
		 * We make sure the preview is started at the end of onCreate.
		 */
        setContentView(R.layout.maincamera);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.camera_root);
        mLayout.setSystemUiVisibility(0x00002000);

		/* SurfaceHolder set */
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(MainCamera.this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mFailButton = (Button) findViewById(R.id.bt_fail);
        mFailButton.setOnClickListener(this);
        mPassButton = (Button) findViewById(R.id.bt_pass);
        mPassButton.setOnClickListener(this);
        mTakePictureButton = (Button) findViewById(R.id.bt_take_picture);
        mTakePictureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_take_picture:
                takePicture();
                mTakePictureButton.setEnabled(false);
                LogUtils.logD(TAG, "onClick take picture");
                break;
            case R.id.bt_pass:
                SendBroadcast(Config.PASS);
                break;
            case R.id.bt_fail:
                SendBroadcast(Config.FAIL);
                break;
        }
    }

    // add huruilong end
    public void SendBroadcast(String result) {
        LogUtils.logD(TAG, "SendBroadcast()  mHadTackPic " + mHadTackPic);
        mHadTackPic = true;
        if (!mHadSendBroadcast) {
            mHadSendBroadcast = true;
            ResultHandle.SaveResultToSystem(result, TAG);
            sendBroadcast(new Intent(Config.ItemOnClick));
            stopCamera();
            sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra(
                    "test_item", TAG));
            finish();
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {
        initCamera();
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w, int h) {
        startCamera();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        stopCamera();
    }

    private void takePicture() {
        if (mCamera != null) {
            try {
                /* mCamra for the Camera object is photographed following method */
                mCamera.takePicture(mShutterCallback, rawPictureCallback, jpegCallback);
                /* When taking pictures, the flash */
                /*Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                mCamera.setParameters(parameters);*/
            } catch (Exception e) {
                showToast(getString(R.string.camera_fail_open));
                finish();
            }
        } else {
            showToast(getString(R.string.camera_fail_open));
            finish();
        }
    }

    private ShutterCallback mShutterCallback = new ShutterCallback() {

        public void onShutter() {
            Log.d(TAG, "onShutter");
        }
    };

    private PictureCallback rawPictureCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {
            try {
                _camera.stopPreview();
//                Thread.sleep(1000);
//                _camera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                showToast(getString(R.string.start_preview_failed));
            }
        }
    };

    private void initCamera() {
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception exception) {
            showToast(getString(R.string.camera_fail_open));
            mCamera = null;
        }

        if (mCamera == null) {
            finish();
        } else {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
                finish();
            }
        }
    }

    private void startCamera() {
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> allSize = parameters
                        .getSupportedPictureSizes();
                int max = 0, maxHeight = 0, maxWidth = 0;
                for (int count = 0; count < allSize.size(); count++) {
                    System.out.println("maxHeight" + maxHeight + "maxWidth"
                            + maxWidth);
                    if (max < (allSize.get(count).height * allSize.get(count).width)) {
                        maxHeight = allSize.get(count).height;
                        maxWidth = allSize.get(count).width;
                        max = maxHeight * maxWidth;
                        System.out.println("maxHeight" + maxHeight + "maxWidth"
                                + maxWidth);
                    }
                }
                parameters.setPictureSize(maxWidth, maxHeight);
                Camera.Size size = parameters.getPictureSize();
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                parameters.setRotation(CameraInfo.CAMERA_FACING_BACK);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(0);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        }
    }

    private void stopCamera() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
