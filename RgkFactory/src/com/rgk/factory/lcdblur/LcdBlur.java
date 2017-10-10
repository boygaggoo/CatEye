package com.rgk.factory.lcdblur;
import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class LcdBlur extends Activity implements OnClickListener{
	public static String TAG = "LcdBlur";
	final int MAX_POINTS_X = 6;
	float widthPix,hightPix;
	float rectangle_x, rectangle_y;
	private boolean hadSendBroadcast;
	Panel panel;
	LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle(R.string.lcd_blur);  
        
        Display display = getWindowManager().getDefaultDisplay(); 
        widthPix = display.getWidth();
        hightPix = display.getHeight();
        
        rectangle_x = widthPix/MAX_POINTS_X;
        rectangle_y = rectangle_x+getDeviation(hightPix,rectangle_x);
        panel = new Panel(this);
        panel.setSystemUiVisibility(0x00002000);
        panel.setOnClickListener(this);
        setContentView(panel);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onClick(View v) {
		if(v.equals(panel)) {
			setContentView(R.layout.lcd_blur);
	        layout = (LinearLayout)findViewById(R.id.lcd_blur_layout);
	        layout.setOnClickListener(this);
		} else if(v.equals(layout)) {
			showDialog();
		}			
	}
	
	float getDeviation(float total,float each) {
		float deviation = (total%each)/(int)(total/each);
		Log.i(TAG, "getDeviation()->deviation="+deviation);
		return deviation;
    }
		
	private void showDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.choose)
        .setCancelable(false)
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
    }
	
	private void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			finish();
		}
	}
	
	class Panel extends View {
		Paint mPaint = new Paint();
		public Panel(Context context) {
			super(context);
		}
		@Override
		protected void onDraw(Canvas canvas) {
			mPaint.setStyle(Style.FILL);
			mPaint.setAntiAlias(true);
			int i=0,j=0;float x=0,y=0;
			for (; x < widthPix; x += rectangle_x, i++) {
				for (; y < hightPix; y += rectangle_y, j++) {
					if((i%2 != 0 && j%2 == 0) || (i%2 == 0 && j%2 != 0)) {
						mPaint.setColor(Color.WHITE);
					} else {
						mPaint.setColor(Color.BLACK);
					}
					canvas.drawRect(x,y,x+rectangle_x,y+rectangle_y, mPaint);
				}
				j=0;y=0;
	        }
			super.onDraw(canvas);
		}
	}
}
