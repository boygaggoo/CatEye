package com.rgk.factory.ControlCenter;

import com.rgk.factory.CitTestResult;
import com.rgk.factory.MainActivity;
import com.rgk.factory.Util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.rgk.factory.R;

public class AutoTestHandle extends BroadcastReceiver {

	private static String TAG = "AutoTestHandle";	
	@Override
	public void onReceive(Context context, Intent intent) {		
		Log.e(TAG, "--------start to auto test----------");
		if (MainActivity.mAutoTest) {
			int count = Util.TestItems.length;
			int i = 0;
			String mTestItem = intent.getStringExtra("test_item");
			/*
			 * if(FeatureOption.SM618_W_APP){ count = count -1; }
			 */
			//judge the TestItem's position in 
			for (i = 0; i < count; i++) {
				if (mTestItem.equals(Util.TestItems[i])) {
					i++;
					break;
				}
			}
			Log.e(TAG, "count=" + count + " mTestItem=" + mTestItem+" i="+i);
			if(mTestItem.equals(MainActivity.TAG)) {
				i = 0;
			}
			if (i == count) {
				i = 0;
				MainActivity.mAutoTest = false;
				context.startActivity((new Intent(context,CitTestResult.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
			} else {
				context.startActivity((new Intent()).setClass(context,
						(Class<?>) Util.TestItemClass[i]).setFlags(
						Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		}
	}

}