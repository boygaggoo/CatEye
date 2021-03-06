package com.ds05.launcher;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.ds05.launcher.common.Constants;
import com.ds05.launcher.common.config.MyAvsHelper;
import com.ds05.launcher.common.utils.AppUtil;
import com.ds05.launcher.ui.home.BaseFragment;
import com.ds05.launcher.ui.home.DesktopFragment;
import com.ds05.launcher.ui.settings.SettingsActivity;
import com.ichano.MediaManagerService;
import com.tencent.bugly.crashreport.CrashReport;

import org.jsoup.helper.StringUtil;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivityWifi";
    private MyAvsHelper mMyAvsHelper;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CrashReport.initCrashReport(getApplicationContext(), "57db92d6d5", false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mMyAvsHelper = MyAvsHelper.getInstance(getApplicationContext());
        if(!StringUtil.isBlank(AppUtil.getZYLicense())){
            mMyAvsHelper.login();
        }

        Intent service = new Intent(MainActivity.this, MediaManagerService.class);
        bindService(service, conn, BIND_AUTO_CREATE);

//        Intent serviceconfig = new Intent(MainActivityWifi.this, RespondReceiveConfigFromServer.class);
//        bindService(serviceconfig,conn,BIND_AUTO_CREATE);
//        startService(new Intent(this, UpdateVersionInfoService.class));

        getContentResolver().registerContentObserver(Uri.parse(BaseFragment.FRAG_SWITCH_AUTHORITIES), true, mObserver);

        FragmentTransaction ft = getFragmentManager().beginTransaction();


        Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
        if(fragment != null){

        }else{
            ft.add(R.id.container, new DesktopFragment()).commit();
        }
    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(this, UpdateVersionInfoService.class));
        getContentResolver().unregisterContentObserver(mObserver);
        unbindService(conn);
        mMyAvsHelper.logout();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d("MainActivityWifi", "按返回键也没什么用的，不要按了。");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("ZXH","######### keyCode = " + keyCode);
        if(keyCode == Constants.HOME_KEY){
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            startActivity(intent);
//            return true;
        }else if(keyCode ==  Constants.CAPTURE_KEY){
            Intent intent = new Intent(this, CameraActivity_ZY.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            isBound = true;
        }

        //client 和service连接意外丢失时，会调用该方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            isBound = false;
        }
    };

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, Uri uri) {
            String uriString = uri.toString();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (uriString.equals(BaseFragment.getFragmentUri(DesktopFragment.class))) {
                ft.replace(R.id.container, new DesktopFragment());
                ft.commit();
            } else if (uriString.equals(BaseFragment.getFragmentUri(SettingsActivity.class))) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        }
    };

}
