/*
 *dushiguang
 */
package com.rgk.factory.touchpanel;

import java.util.ArrayList;

import com.rgk.factory.Config;
import com.rgk.factory.ItemTestActivity;
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
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Paint.Style;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.mediatek.common.featureoption.FeatureOption;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.File;

public class TouchPanel1 extends Activity {

    public static String TAG = "TouchPanel";
    ArrayList<EdgePoint> mArrayList;
    float pointRadius = 20;
    String resultString = "Failed";
    float hightPix = 0, widthPix = 0;
    float w = 0, h = 0;
    Context mContext;
    // If points is too more, it will be hard to touch edge points.
    private final int MAX_POINTS = 15;
    private Paint mPaintLine;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private int mov_x;
    private int mov_y;
    private boolean hadSendBroadcast = false;

	public  String []strAll =null;
	public String ic = null;
	public String file = null;
	private boolean updataTP=false; 
	private static StringBuffer m_TP_buffer = null;
    public class EdgePoint {

        float x;
        float y;
        boolean isChecked = false;

        public EdgePoint(float x, float y, boolean isCheck) {

            this.x = x;
            this.y = y;
            this.isChecked = isCheck;
        }

    }


        public ArrayList<EdgePoint> getTestPoint() {

        ArrayList<EdgePoint> list = new ArrayList<EdgePoint>();

        if(Config.MoreCircle) {
            for (float w = pointRadius - 1; w < widthPix; w += pointRadius * 2) {
            for (float h = /*pointRadius - 1*/(float)15.25; h < hightPix; h += (pointRadius * 2 + 0.5)) {
                if (w == pointRadius - 1) {
                    list.add(new EdgePoint(pointRadius, h, false));
                    continue;
                }


                if (w == widthPix - pointRadius - 1) {
                    list.add(new EdgePoint(widthPix - pointRadius, h, false));
                    continue;
                }

                if (h == /*pointRadius - 1*/(float)15.25) {
                    list.add(new EdgePoint(w, pointRadius, false));
                    continue;
                }

                if (h == /*hightPix - pointRadius - 1*/hightPix  - (float)15.25) {
                    list.add(new EdgePoint(w, hightPix - pointRadius, false));
                    continue;
                }
            }
            }
		} else {
            /*for (float w = pointRadius - 1; w < widthPix; w += pointRadius * 2) {

            for (float h = pointRadius - 1; h < hightPix; h += pointRadius * 2) {


                if (w == pointRadius - 1) {
                    list.add(new EdgePoint(pointRadius, h, false));
					//Log.e("liunianliang","---------h="+h+"list.size=" + list.size());
                    continue;
                }

                if (w == widthPix - pointRadius - 1) {
                    list.add(new EdgePoint(widthPix - pointRadius, h, false));
                    continue;
                }

                if (h == pointRadius - 1) {
                    list.add(new EdgePoint(w, pointRadius, false));
                    continue;
                }

                if (h == hightPix - pointRadius - 1) {
                    list.add(new EdgePoint(w, hightPix - pointRadius, false));
                    continue;
                }
            }
            }*/

              float wDeviation = getDeviation(widthPix,pointRadius*2);
              for (float w = pointRadius - 1; w < widthPix; w += pointRadius * 2+wDeviation) {
                   list.add(new EdgePoint(w, pointRadius - 1, false));
                   list.add(new EdgePoint(w, hightPix - pointRadius, false));
                  
              }
              float hDeviation = getDeviation(hightPix,pointRadius*2);
              for (float h = pointRadius*3 - 1; h < hightPix-pointRadius * 2; h += pointRadius * 2+hDeviation) {
                   list.add(new EdgePoint(pointRadius - 1, h, false));
                   list.add(new EdgePoint(widthPix - pointRadius,h, false));
              }  			
		}       
        float lengthPix = (float) Math.sqrt(widthPix*widthPix +hightPix*hightPix);
        float cos = widthPix/lengthPix;
        float sin = hightPix/lengthPix;
        float rDeviation = getDeviation(lengthPix,pointRadius*2);
        for (float r = pointRadius; r < lengthPix; r += pointRadius * 2+rDeviation) {
        	list.add(new EdgePoint(cos*r - 1, sin*r-1, false));
        	list.add(new EdgePoint(widthPix-(cos*r - 1),sin*r-1, false));
        }
        return list;

    }
//add JWLYW-288 20140928 (start)
        float getDeviation(float total,float each){
         if(each>1&&(total/each)>2){
         float result = (total%each)/(total/each-1);
         Log.d(TAG,"now deviation is:"+result);
         return result;}
         return 0f;
        }
//add JWLYW-288 20140928 (end)


    @Override
    public void finish() {
		//if (mArrayList != null)  mArrayList.removeAll();
        super.finish();
    }

    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
        super.onCreate(savedInstanceState);
        init(this);
        m_TP_buffer = new StringBuffer();
        // full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get panel size
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        hightPix = mDisplayMetrics.heightPixels;
        widthPix = mDisplayMetrics.widthPixels;

		//add by liunianliang for SMLYE-36 20130905 (start)
		if(hightPix == 854 && widthPix == 480) 
			Config.MoreCircle = true;
		//add by liunianliang for SMLYE-36 20130905 (end)
 
        if(Config.MoreCircle) {
            pointRadius = 15;
	    } else {
			pointRadius = getRadius(hightPix, widthPix);
	    }	
		Log.e("dsg", "===tpVersion===1");
        updataTP = tpVersion();
        Log.e(TAG,hightPix + " " + widthPix); 
        Panel panel = new Panel(this);
        try {
        	//panel.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED);
        	panel.setSystemUiVisibility(0x00002000);
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        setContentView(panel);
    }

    private void init(Context context) {

        mContext = context;
        resultString = "Failed";
    }

    float getRadius(float hightPix, float widthPix) {

        float h = hightPix / 2;
        float w = widthPix / 2;
        if (w > h)// 
        {
            float t;
            t = w;
            w = h;
            h = t;
        }
        float radius = 15;
        float minRadius = w / MAX_POINTS;
//lxb add
        System.out.println("lxb,now min is:"+minRadius+",and w is:"+w);
//lxb add

        for (float i = minRadius; i < w; i++) {
            if (h % i == 0 && w % i == 0) {

                return i;
            }
        }
//lxb add
        System.out.println("lxb,return is:"+radius);
//lxb add
        return radius;
    }

    class Panel extends View {

        public static final int TOUCH_TRACE_NUM = 30;
        public static final int PRESSURE = 500;
        private TouchData[] mTouchData = new TouchData[TOUCH_TRACE_NUM];
        private int traceCounter = 0;
        private Paint mPaint = new Paint();

        public class TouchData {

            public float x;
            public float y;
            public float r;
        }

        public Panel(Context context) {

            super(context);
			/*
	if(FeatureOption.SM618_W_APP){
			int w = (int)widthPix;
			int h = (int)hightPix;
			float sw = (float)5.0;
			mPaintLine = new Paint();
			mBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas();
			mCanvas.setBitmap(mBitmap);

			mPaintLine.setStyle(Style.STROKE);
			mPaintLine.setStrokeWidth(sw);
			mPaintLine.setARGB(255, 0, 0, 255);
			mPaintLine.setAntiAlias(true);
		}
		*/
            mArrayList = getTestPoint();
            mPaint.setARGB(100, 100, 100, 100);
            for (int i = 0; i < TOUCH_TRACE_NUM; i++) {
                mTouchData[i] = new TouchData();
            }

        }

        private int getNext(int c) {

            int temp = c + 1;
            return temp < TOUCH_TRACE_NUM ? temp : 0;
        }

        public void onDraw(Canvas canvas) {

            super.onDraw(canvas);
			int mh = 30;
            mPaint.setColor(Color.LTGRAY);
            mPaint.setTextSize(20);
            canvas.drawText("W: " + w, widthPix / 2 - 200, hightPix / 2 - 10, mPaint);
            canvas.drawText("H: " + h, widthPix / 2 - 200, hightPix / 2 + 10, mPaint);
			for(int a =0 ; a<strAll.length; a++) {
			    canvas.drawText(strAll[a], widthPix / 2 - 200, hightPix / 2 + mh, mPaint);
				mh +=20; 
			}
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(pointRadius);
            for (int i = 0; i < mArrayList.size(); i++) {
                EdgePoint point = mArrayList.get(i);
                mPaint.setColor(Color.RED);
                canvas.drawCircle(point.x, point.y, mPaint.getStrokeWidth(), mPaint);

            }
	//if(!FeatureOption.SM618_W_APP){

		            for (int i = 0; i < TOUCH_TRACE_NUM; i++) {
		                TouchData td = mTouchData[i];
		                mPaint.setColor(Color.BLUE);
		                if (td.r > 0) {
		                    canvas.drawCircle(td.x, td.y, 2, mPaint);
		                }

		            }
			//}
            invalidate();
			/*
		if(FeatureOption.SM618_W_APP){
	            canvas.drawBitmap(mBitmap,0,0,null);
	            }
	            */
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            final int eventAction = event.getAction();

            w = event.getRawX();
            h = event.getRawY();
            if ((eventAction == MotionEvent.ACTION_MOVE) || (eventAction == MotionEvent.ACTION_UP)) {
                for (int i = 0; i < mArrayList.size(); i++) {
                    EdgePoint point = mArrayList.get(i);
                    if (!point.isChecked
                            && ((w >= (point.x - pointRadius)) && (w <= (point.x + pointRadius)))
                            && ((h >= (point.y - pointRadius)) && (h <= (point.y + pointRadius)))) {
                        mArrayList.remove(i);
                        break;
                    }

                }

                if (mArrayList.isEmpty()) {
                    
                	//showDialog();
                	if (updataTP) {
						//Toast.makeText(TouchPanel.this,getResources().getString(R.string.To_info),Toast.LENGTH_SHORT).show();
						//for (int i =0; i<500; i++){}
                	    SendBroadcast(Config.PASS);
                	} else {
                        Toast.makeText(TouchPanel1.this,getResources().getString(R.string.To_info),Toast.LENGTH_SHORT).show();
						for (int i =0; i<500; i++){}
						SendBroadcast(Config.FAIL);
					}
                }

                TouchData tData = mTouchData[traceCounter];
                tData.x = event.getX();
                tData.y = event.getY();
                tData.r = event.getPressure() * PRESSURE;
                traceCounter = getNext(traceCounter);
                invalidate();

            }
			/*
	if(FeatureOption.SM618_W_APP){

			 if (eventAction == MotionEvent.ACTION_MOVE) 
			 	{
			 		mCanvas.drawLine(mov_x,mov_y,(int)event.getX(),(int)event.getY(),mPaintLine);
					invalidate();
			 	}
			 else if(eventAction == MotionEvent.ACTION_DOWN)
			 	{
			 		mov_x = (int)event.getX();
					mov_y = (int)event.getY();
					mCanvas.drawPoint(mov_x,mov_y,mPaintLine);
					invalidate();
			 	}
			 mov_x = (int)event.getX();
			 mov_y = (int)event.getY();
	 	}*/
            return true;
        }

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
    
	public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			finish();
		}
	}
	
	@Override
    public void onBackPressed() {
		finish();
    }

	public boolean tpVersion() {
		boolean result = false;
		FileInputStream mFileInputStream = null;
    	try {
			File tpfile = new File("/proc/rgk_tpInfo");
			if (tpfile.exists()){
                mFileInputStream = new FileInputStream("/proc/rgk_tpInfo");
			}
			InputStreamReader mInputStreamReader = new InputStreamReader(mFileInputStream);
			BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
			String ch = null;
			String []icstring= null;
			String []filestring= null;
			while ((ch = mBufferedReader.readLine()) != null) {
				strAll = ch.split(",");
				for (int i = 0; i<strAll.length; i++) {
					if(strAll[i].contains("SW FirmWare")){
						icstring = strAll[i].split(":");
						ic = icstring[1];
					} else if(strAll[i].contains("Sample FirmWare")){
					    filestring = strAll[i].split(":");
						file = icstring[1];
				    }					
				}
			}
			if(ic !=null && file !=null) {
                if(ic.equals(file)) {
                	result = true;
				}
			}
			mFileInputStream.close();
		} catch (Exception e) {
            Log.w(TAG, "Error reading rgk_tpInfo."+e);
		}	
		return result;
    }
}
