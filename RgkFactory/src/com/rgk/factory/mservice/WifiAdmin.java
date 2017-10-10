package com.rgk.factory.mservice;

import java.util.List;  
    
import android.content.Context;  
import android.net.wifi.ScanResult;  
import android.net.wifi.WifiConfiguration;  
import android.net.wifi.WifiInfo;  
import android.net.wifi.WifiManager;  
import android.net.wifi.WifiManager.WifiLock;  
import android.util.Log;    
     
public class WifiAdmin {      
     
    private WifiManager mWifiManager = null;      
    private WifiInfo mWifiInfo = null;      
    private List<ScanResult> mWifiList = null;
    private List<WifiConfiguration> mWifiConfiguration = null;     
    private WifiLock mWifiLock = null;      
     
    public WifiAdmin(Context mContext) {      
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);      
        mWifiInfo = mWifiManager.getConnectionInfo();      
    }      
     
    public boolean isWifiEnabled(){  
        return mWifiManager.isWifiEnabled();  
    }  
      
    public void OpenWifi() {      
        if (!mWifiManager.isWifiEnabled()) {      
            mWifiManager.setWifiEnabled(true);     
        }      
    }      
     
    public void CloseWifi() {      
        if (mWifiManager.isWifiEnabled()) {      
            mWifiManager.setWifiEnabled(false);     
        }      
    }      
     
    public void lockWifi() {      
        mWifiLock.acquire();     
    }      
     
    public void rlockWifi() {      
        if (mWifiLock.isHeld()) {      
            mWifiLock.acquire();    
        }      
    }      
     
    public void Createwifilock() {      
        mWifiLock = mWifiManager.createWifiLock("Testss");     
    }      
     
    public List<WifiConfiguration> GetConfinguration() {      
        return mWifiConfiguration;   
    }      
     
    public void ConnectConfiguration(int index) {      
        if (index > mWifiConfiguration.size()) {      
            return;      
        }      
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);   
    }      
    public void StartScan()      
    {      		
        mWifiManager.startScan();      
          
        mWifiList = mWifiManager.getScanResults();      
       
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();      
    }      
       
    public List<ScanResult> GetWifiList()      
    {      
        return mWifiList;      
    }      
      
    public boolean LookUpScan()      
    {      
        if(mWifiList.size() > 0){
        	return true;
        }  
        return false;      
    }      
   
    public String GetMacAddress()      
    {      
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();      
    }      
         
    public String GetBSSID()      
    {      
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();      
    }      
          
    public int GetIPAddress()      
    {      
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();      
    }      
         
    public int GetNetworkId()      
    {      
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();      
    }      
      
    public String GetWifiInfo()      
    {      
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();      
    }      
        
    public void AddNetwork(WifiConfiguration wcg)      
    {      
        int wcgID = mWifiManager.addNetwork(wcg);       
        mWifiManager.enableNetwork(wcgID, true);       
    }      
        
    public void DisconnectWifi(int netId)      
    {      
        mWifiManager.disableNetwork(netId);      
        mWifiManager.disconnect();      
    }      
}    
