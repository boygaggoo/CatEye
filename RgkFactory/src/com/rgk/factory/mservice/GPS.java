//add dushiguang
package com.rgk.factory.mservice;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rgk.factory.Config;
import com.rgk.factory.R;
import com.rgk.factory.ControlCenter.ResultHandle;

import android.provider.Settings;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.util.Log;

public class GPS extends Service {

    public static String TAG = "GPS";
    private Context mContext;
    private Location location;
    LayoutInflater mInflater = null;
    LocationManager mLocationManager = null;
    final int OUT_TIME = 30 * 1000;
    final int MIN_SAT_NUM = 1;

    @Override
    public void onCreate() {

        mContext = this;
        getService();
        if (!mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
        	Settings.Secure.setLocationProviderEnabled(this.getContentResolver(),LocationManager.GPS_PROVIDER, true);
        }
        Log.d("dsg","=====GPSservice onCreate=========");
        startGPS();
    }

    CountDownTimer mCountDownTimer = new CountDownTimer(OUT_TIME, 3000) {

        @Override
        public void onTick(long arg0) {
      
        }

        @Override
        public void onFinish() {

            fail(getString(R.string.time_out));
        }
    };

    void startGPS() {

        if (!mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Settings.Secure.setLocationProviderEnabled(this.getContentResolver(),LocationManager.GPS_PROVIDER, true);
        }
        Criteria criteria;
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String provider = mLocationManager.getBestProvider(criteria, true);
        if (provider == null) {
            fail("Fail to get GPS Provider!");
        }
        loge("here");
        mLocationManager.requestLocationUpdates(provider, 500, 0, mLocationListener);
        mLocationManager.addGpsStatusListener(gpsStatusListener);

        location = mLocationManager.getLastKnownLocation(provider);
        setLocationView(location);
        //stopGPS();
    }

    private void setLocationView(Location location) {

        if (location != null) {
            SendBroadcast(Config.PASS);
        } else {

        }
    }

    LocationListener mLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            setLocationView(location);
        }

        public void onProviderDisabled(String provider) {

            setLocationView(null);
        }

        public void onProviderEnabled(String provider) {

            toast("Provider enabled");

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private GpsStatus mGpsStatus;
    private Iterable<GpsSatellite> mSatellites;
    List<String> satelliteList = new ArrayList<String>();
    GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {

        public void onGpsStatusChanged(int arg0) {

            switch (arg0)
            {
            case GpsStatus.GPS_EVENT_STARTED:
                toast("GPS Start");
                mCountDownTimer.start();
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                 toast("GPS Stop");
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                toast("Locate sucess");
                pass();
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                logd("GPS_EVENT_SATELLITE_STATUS");
                mGpsStatus = mLocationManager.getGpsStatus(null);
                mSatellites = mGpsStatus.getSatellites();
                Iterator<GpsSatellite> it = mSatellites.iterator();
                int count = 0;
                satelliteList.clear();
                while (it.hasNext()) {
                    GpsSatellite gpsS = (GpsSatellite) it.next();
                    satelliteList.add(count++, gpsS.toString());
                }
                if (count >= MIN_SAT_NUM)
                    pass();
                break;
            default:
                break;
            }

        }

    };

    void stopGPS() {

        try {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager.removeGpsStatusListener(gpsStatusListener);
        } catch (Exception e) {
            loge(e);
        }
    }

	public void SendBroadcast(String result){
		ResultHandle.SaveResultToSystem(result, TAG);
		stopGPS();
		stopSelf();
	}
	
	
    void getService() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager == null) {
            fail("Fail to get LOCATION_SERVICE!");

        }
    }

    @Override
	public void onDestroy() {
        Log.d("dsg","=====GPSservice onDestroy=========");
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
		if (mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
        	Settings.Secure.setLocationProviderEnabled(this.getContentResolver(),LocationManager.GPS_PROVIDER, false);
        }
        Intent serviceIntent = new Intent(GPS.this, WiFi.class);
		startService(serviceIntent);
		Log.d("dsg", "=====GPSService==>>>startWifiService()");
        super.onDestroy();
        Log.d("dsg", "=====GPSService==>>>onDestroy()");
    }

    
    void fail(Object msg) {
    	SendBroadcast(Config.FAIL);
        loge(msg);
        toast(msg);
    }

    void pass() {

	SendBroadcast(Config.PASS);
    }

    public void toast(Object s) {
        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }

    private void loge(Object e) {

        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
