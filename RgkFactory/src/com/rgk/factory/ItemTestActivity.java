package com.rgk.factory;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.view.Display;

public class ItemTestActivity extends Activity implements OnItemClickListener{
	private static String TAG = "ItemTestActivity";
	WindowManager wm;
	Display display;
	public static int mWidth;
	public static int mHeight;
	GridView gridView;
	GridAdapter gridAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_item_test);
		LinearLayout mLayout = (LinearLayout) findViewById(R.id.item_test_layout);
	    mLayout.setSystemUiVisibility(0x00002000);
	    
		gridView = (GridView)findViewById(R.id.gridview);
		gridAdapter = new GridAdapter(this);
		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(this);
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = (Display) wm.getDefaultDisplay();
		mWidth = display.getWidth();
		mHeight = display.getHeight();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Config.ItemOnClick);

	}
	
	@Override
	protected void onResume() {
		gridView.invalidateViews();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.i(TAG,"onItemClick:pos="+position+" class="+ Util.singleTestClass.get(position));
		startActivity(new Intent(this,Util.singleTestClass.get(position)));
	}
}