package com.rgk.factory.touchpanel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import android.widget.Toast;
public class TouchPanel extends Activity {
    public static String TAG = "TouchPanel";
    boolean tpTestResult,hadSendBroadcast;
    private final static float PADDING = 3;
	String[] strAll =null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        TouchPanelSurfaceView panel = new TouchPanelSurfaceView(this);
        panel.setSystemUiVisibility(0x00002000);
        setContentView(panel);
        tpTestResult = tpVersion();
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.equals(KeyEvent.KEYCODE_BACK)) {
			SendBroadcast(Config.FAIL);
		}
		return super.onKeyDown(keyCode, event);
	}
    
	public boolean tpVersion() {
		boolean result = false;
		String ic=null ,file=null;
		FileInputStream mFileInputStream = null;
    	try {
			File tpfile = new File("/proc/rgk_tpInfo");
			if (tpfile.exists()){
                mFileInputStream = new FileInputStream("/proc/rgk_tpInfo");
			}
			InputStreamReader mInputStreamReader = new InputStreamReader(mFileInputStream);
			BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
			String ch = null;
			String[] icstring= null;
			String[] filestring= null;
			while ((ch = mBufferedReader.readLine()) != null) {
				strAll = ch.split(",");
				for (int i = 0; i<strAll.length; i++) {
					strAll[i]=strAll[i].trim();
					if(strAll[i].contains("SW FirmWare") || strAll[i].contains("SW firmware")){
						icstring = strAll[i].split(":");
						ic = icstring[1];
					} else if(strAll[i].contains("Sample FirmWare") || strAll[i].contains("Sample firmware")){
					    filestring = strAll[i].split(":");
						file = filestring[1];
				    }					
				}
			}
			Log.i(TAG, "tpVersion()->ic="+ic+",file="+file);
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
	
	public void SendBroadcast(String result){
		if (!hadSendBroadcast) {
			hadSendBroadcast = true;
			ResultHandle.SaveResultToSystem(result, TAG);
			sendBroadcast(new Intent(Config.ItemOnClick));
			sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
			finish();
		}
	}
    class TouchPanelSurfaceView extends SurfaceView implements Callback, OnTouchListener {
    	private final static int MSG_UPDATE_R = 1;
    	private final static int MSG_UPDATE_P = 2;
    	final int MAX_POINTS_X = 16;
    	float widthPix,hightPix;
    	float rectangle_x, rectangle_y;
    	float parallelogram_h, parallelogram_w, parallelogram_x_add;
    	float currentX,currentY,lastX,lastY;
    	private ArrayList<EdgePoint> mRectangleList,mParallelogramLeftList,mParallelogramRightList;
    	boolean isRectangleTestEnd,isPathPaintReStart,isParallelogramTestStart,
    		isParallelogramLeftTestEnd,isParallelogramRightTestEnd;    	
    	Paint mPaint,pathPaint,textPaint;
    	Path path,pathParallelogram;
    	private SurfaceHolder sfh;
    	private Canvas canvas;
    	private Handler myHandler = new Handler() {
    		@Override
    		public void handleMessage(Message msg) {
    			switch(msg.what) {
    			case MSG_UPDATE_R:
    				drawRectangle();
    				break;
    			case MSG_UPDATE_P:
    				drawParallelogram();
    				break;
    			}			
    			super.handleMessage(msg);
    		}		
    	};
    	
    	public TouchPanelSurfaceView(Context context) {
    		super(context);
    		sfh = this.getHolder();
    		sfh.addCallback(this);		
            setFocusable(true);
            this.setOnTouchListener(this);
    	}
    	
    	@Override
    	public boolean onTouch(View v, MotionEvent event) {
    		if(isParallelogramLeftTestEnd && isParallelogramRightTestEnd) {
/*   wangjun delete
    		if(!tpTestResult) {
    	    		Toast.makeText(TouchPanel.this,getResources().getString(R.string.To_info),Toast.LENGTH_SHORT).show();
    				SendBroadcast(Config.FAIL);
    	    	} else {
    	    		SendBroadcast(Config.PASS);
    	    	}     */
                SendBroadcast(Config.PASS);       	
    		}    		
    		currentX = event.getRawX();
    		currentY = event.getRawY();
    		final int eventAction = event.getAction();			
    		if(eventAction== MotionEvent.ACTION_DOWN){
    			path.moveTo(currentX, currentY);
    			if(isRectangleTestEnd) {
    				isParallelogramTestStart = true;
    			}
    		} else if(eventAction== MotionEvent.ACTION_MOVE){		
    			path.quadTo(lastX, lastY, currentX, currentY);
    		} else if(eventAction== MotionEvent.ACTION_UP){
    			if(isParallelogramTestStart && !isPathPaintReStart) {
    				path.reset();
    				isPathPaintReStart = true;
    			}
    		}		
    		lastX = currentX;
    		lastY = currentY;
    		if(!isRectangleTestEnd) {
    			checkPointInRectangle(currentX, currentY);
    			myHandler.sendEmptyMessage(MSG_UPDATE_R);
    		} else if(isParallelogramTestStart) {
    			checkPointInLeftParallelogram(currentX,currentY);
    			checkPointInRightParallelogram(currentX,currentY);
    			myHandler.sendEmptyMessage(MSG_UPDATE_P);
    		}		
    		return true;
    	}
    	
    	@Override
    	public void surfaceCreated(SurfaceHolder holder) {
    		Log.i(TAG, "surfaceCreated()");
    		widthPix = this.getWidth();
    	    hightPix = this.getHeight();
            rectangle_x = widthPix/MAX_POINTS_X;
            rectangle_y = rectangle_x+getDeviation(hightPix,rectangle_x);
            mRectangleList = getRectanglePoint();
            
            parallelogram_h = rectangle_y;
            parallelogram_x_add = (parallelogram_h*widthPix)/hightPix;
            parallelogram_w = rectangle_x+parallelogram_x_add;
            mParallelogramLeftList = getParallelogramLeftPoint();
            mParallelogramRightList = getParallelogramRightPoint();
            Log.i(TAG, "rectangle_x="+rectangle_x+" rectangle_y="+rectangle_y
            		+" parallelogram_h="+parallelogram_h+" parallelogram_x_add="
            		+parallelogram_x_add+" parallelogram_w="+parallelogram_w);
            
            mPaint = new Paint();
    	    mPaint.setStyle(Style.STROKE);
    		mPaint.setAntiAlias(true);
    		
    		pathPaint = new Paint();
    		pathPaint.setColor(Color.WHITE);
        	pathPaint.setStyle(Style.STROKE);
        	pathPaint.setStrokeWidth(1.5f);
        	pathPaint.setAntiAlias(true);
        	
        	textPaint = new Paint();
        	textPaint.setColor(Color.WHITE);
        	textPaint.setTextSize(30);
        	
        	path = new Path();
    	}
    	
    	@Override
    	public void surfaceChanged(SurfaceHolder holder, int format, int width,
    			int height) {
    		Log.i(TAG, "surfaceChanged()");
    		myHandler.sendEmptyMessage(MSG_UPDATE_R);		
    	}
    	
    	@Override
    	public void surfaceDestroyed(SurfaceHolder holder) {
    		Log.i(TAG, "surfaceDestroyed()");
    	}
    	
    	float getDeviation(float total,float each) {
    		float deviation = (total%each)/(int)(total/each);
    		Log.i(TAG, "getDeviation()->deviation="+deviation);
    		return deviation;
        }
    	
    	private ArrayList<EdgePoint> getRectanglePoint() {
    		ArrayList<EdgePoint> list = new ArrayList<EdgePoint>();
    		//Top line point
    		for (float x=0; x < widthPix; x += rectangle_x) {
    			list.add(new EdgePoint(x,PADDING,x+rectangle_x,rectangle_y+PADDING, false));
            }
    		//Bottom line point
    		for (float x=0; x < widthPix; x += rectangle_x) {
    			list.add(new EdgePoint(x,hightPix-rectangle_y-PADDING,x+rectangle_x,hightPix-PADDING, false));
            }
    		//Left line point
    		for (float y=rectangle_y+PADDING; y < hightPix-rectangle_y-PADDING; y += rectangle_y) {
    			list.add(new EdgePoint(0,y,rectangle_x,y+rectangle_y, false));
    		}
    		//Right line point
    		for (float y=rectangle_y+PADDING; y < hightPix-rectangle_y-PADDING; y += rectangle_y) {
    			list.add(new EdgePoint(widthPix-rectangle_x,y,widthPix,y+rectangle_y, false));
    		}
    		return list;
    	}
    	
    	private ArrayList<EdgePoint> getParallelogramLeftPoint() {
    		ArrayList<EdgePoint> list = new ArrayList<EdgePoint>();
    		for (float x=widthPix+parallelogram_x_add-parallelogram_w, y=0;
    				x+parallelogram_w > 0 && y < hightPix; x -= parallelogram_x_add, y += parallelogram_h) {
    			list.add(new EdgePoint(x,y,x+parallelogram_w,y,
    					x-parallelogram_x_add,y+parallelogram_h,
    					parallelogram_w+x-parallelogram_x_add,y+parallelogram_h,false));
    		}
    		return list;
    	}
    	
    	private ArrayList<EdgePoint> getParallelogramRightPoint() {
    		ArrayList<EdgePoint> list = new ArrayList<EdgePoint>();
    		for (float x=-parallelogram_x_add, y=0; x < widthPix && y < hightPix; x += parallelogram_x_add, y += parallelogram_h) {
    			list.add(new EdgePoint(x,y,x+parallelogram_w,y,
    					x+parallelogram_x_add,y+parallelogram_h,
    					parallelogram_w+x+parallelogram_x_add,y+parallelogram_h,false));
    		}
    		return list;
    	}
    	
		private void checkPointInLeftParallelogram(float x, float y) {
			int i = 0, j = 0, k = 0, size = mParallelogramLeftList.size();
			for (; i <size; i++) {
	            EdgePoint point = mParallelogramLeftList.get(i);
	            if (!point.isChecked
	            		&& (y >= point.y1)
	                    && (y <= point.y3)
	                    && (x >= point.x1-(y-point.y1)*widthPix/hightPix)
	                    && (x <= point.x1-(y-point.y1)*widthPix/hightPix+parallelogram_w)) {
	            	point.isChecked = true;
	            	mParallelogramLeftList.add(point);
	            	mParallelogramLeftList.remove(i);
	                break;
	            }
	        }
			for (; j < size; j++) {
	            EdgePoint point = mParallelogramLeftList.get(j);            
	            if (point.isChecked) {
	            	k++;
	            }
	        }
			if(k == size) {
				isParallelogramLeftTestEnd = true;
			}
		}
		
		private void checkPointInRightParallelogram(float x, float y) {
			int i = 0, j = 0, k = 0, size = mParallelogramRightList.size();
			for (; i <mParallelogramRightList.size(); i++) {
	            EdgePoint point = mParallelogramRightList.get(i);
	            if (!point.isChecked
	            		&& (y >= point.y1)
	                    && (y <= point.y3)
	                    && (x >= (y-point.y1)*widthPix/hightPix+point.x1)
	                    && (x <= (y-point.y1)*widthPix/hightPix+point.x1+parallelogram_w)) {
	            	point.isChecked = true;
	            	mParallelogramRightList.add(point);
	            	mParallelogramRightList.remove(i);
	            	break;
	            }
	        }
			for (; j < size; j++) {
	            EdgePoint point = mParallelogramRightList.get(j);            
	            if (point.isChecked) {
	            	k++;
	            }
	        }
			if(k == size) {
				isParallelogramRightTestEnd = true;
			}
		}
    		
    	private void checkPointInRectangle(float x, float y) {
    		int i = 0, j = 0, k = 0, size;
    		size = mRectangleList.size();
    		for (; i < size ; i++) {
                EdgePoint point = mRectangleList.get(i);
                if (!point.isChecked
                        && (x >= point.x1 && x <= point.x2)
                        && (y >= point.y1 && y <= point.y2)) {
                	point.isChecked = true;
                	mRectangleList.add(point);
                	mRectangleList.remove(i);
                    break;
                }
            }
    		for (; j < size ; j++) {
                EdgePoint point = mRectangleList.get(j);
                if (point.isChecked) {
                	k++;
                }
            }
    		Log.i(TAG, "checkPointInRectangle()->k="+k+" size="+size);
    		if(k == size) {
    			isRectangleTestEnd = true;
    		}
    	}
    	
        private void drawRectangle() {
        	try {
    			canvas = sfh.lockCanvas();
    			if (canvas != null) {
    				mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
    				canvas.drawPaint(mPaint);
    				mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
    				//draw tp info    	        	
    				canvas.drawText("W: "+widthPix, rectangle_x+10, hightPix/2-2*rectangle_y, textPaint);
    				canvas.drawText("H: "+hightPix, rectangle_x+10, hightPix/2-rectangle_y, textPaint);
    				if(strAll != null && strAll.length > 0) {
        				for(int i =0 ; i<strAll.length; i++) {
        					canvas.drawText(strAll[i], rectangle_x+10, hightPix/2+i*rectangle_y, textPaint);
        				}
    				}
    				//draw rectangle
    				mPaint.setColor(Color.BLUE);
    	            for (int i = 0; i < mRectangleList.size(); i++) {
    	                EdgePoint point = mRectangleList.get(i);
    	                if(point.isChecked) {
    	                	mPaint.setColor(Color.GREEN);
    	                }
    	                canvas.drawRect(point.x1, point.y1, point.x2, point.y2, mPaint);
    	            }
    	            //draw line
    	        	canvas.drawPath(path, pathPaint);
    	        	//draw tips
    	        	if(isRectangleTestEnd) {
    	    			canvas.drawText(getResources().getString(R.string.tp_test_pass), rectangle_x+10, 2*rectangle_y, textPaint);
    	        	}
    			}			
    		} catch (Exception e) {
    			// TODO: handle exception
    		} finally {
    			if (canvas != null) {
    				sfh.unlockCanvasAndPost(canvas);
    			}
    		}
        }
        
        private void drawParallelogram() {
        	try {
    			canvas = sfh.lockCanvas();
    			if (canvas != null) {
    				mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
    				canvas.drawPaint(mPaint);
    				mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));								
					//draw left parallelogram
			     	for (int i = 0; i < mParallelogramLeftList.size(); i++) {
		                EdgePoint point = mParallelogramLeftList.get(i);
		                mPaint.setColor(Color.BLUE);
		                if(point.isChecked) {
		                	mPaint.setColor(Color.GREEN);
		                }
		                pathParallelogram = new Path();
		                pathParallelogram.moveTo(point.x1,point.y1);
		                pathParallelogram.lineTo(point.x2,point.y2);
		                pathParallelogram.lineTo(point.x4,point.y4);
		                pathParallelogram.lineTo(point.x3,point.y3);
		                pathParallelogram.close();
		                canvas.drawPath(pathParallelogram, mPaint);
		            }
		            //draw right parallelogram
		            for (int i = 0; i < mParallelogramRightList.size(); i++) {
		                EdgePoint point = mParallelogramRightList.get(i);
		                mPaint.setColor(Color.BLUE);
		                if(point.isChecked) {
		                	mPaint.setColor(Color.GREEN);
		                }
		                pathParallelogram = new Path();
		                pathParallelogram.moveTo(point.x1,point.y1);
		                pathParallelogram.lineTo(point.x2,point.y2);
		                pathParallelogram.lineTo(point.x4,point.y4);
		                pathParallelogram.lineTo(point.x3,point.y3);
		                pathParallelogram.close();
		                canvas.drawPath(pathParallelogram, mPaint);
		            }
    				//draw line
    	        	canvas.drawPath(path, pathPaint);
    			}
    		} catch (Exception e) {
    			// TODO: handle exception
    		} finally {
    			if (canvas != null)
    				sfh.unlockCanvasAndPost(canvas);
    		}
        }
    }
    class EdgePoint {	
        float x1,y1,x2,y2,x3,y3,x4,y4;
        boolean isChecked = false;
        public EdgePoint(float x1, float y1, float x2, float y2,  boolean isCheck) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.isChecked = isCheck;
        }
        public EdgePoint(float x1, float y1, float x2, float y2, 
        		float x3, float y3, float x4, float y4,   boolean isCheck) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.x3 = x3;
            this.y3 = y3;
            this.x4 = x4;
            this.y4 = y4;
            this.isChecked = isCheck;
        }
    }
}
