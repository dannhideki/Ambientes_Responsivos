package com.example.hideki.ambientesresponsivos;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;
/**
 * Created by Giuliano on 10/04/2015.
 */
public class WifiManipulator {

    WifiManager wifiManager;

    public List<ScanResult> getConections(Context context){
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<android.net.wifi.ScanResult> connections = wifiManager.getScanResults();
        return connections;

    }

}
