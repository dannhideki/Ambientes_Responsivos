package com.example.hideki.ambientesresponsivos;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.net.wifi.ScanResult;
import android.widget.Toast;

public class MainActivity extends Activity {

    Handler handler = new Handler();
    WifiManager wifiManager;
    WifiScanReceiver wifiReciever;
    ArrayAdapter adapter;
    ListView list;
    ArrayList<String> wifis;
    WifiInfo wifiInfo;
    Gps gps;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        atualizaGps();
        list = (ListView) findViewById(R.id.list);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        wifiInfo = wifiManager.getConnectionInfo();
        atualizaGps();
        wifis = new ArrayList<String>(); //initialize wifis
        //wifis.add("loading...");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);

        wifiManager.startScan(); //make sure this is the last call
        String umlImg = "http://www.uml-diagrams.org/examples/profile-example-dicom-2011.png";
        setTextView("Sala 406 - Engenharia de Software: ");
        loadImg(umlImg);

        //Toast.makeText(this, "Bem vindo a Fatec - São José dos Campos", Toast.LENGTH_LONG).show();
    }

    private void atualizaGps(){
        gps = new Gps(this);
        gps.setUpMap();
    }

    private void loadImg(final String urlImg) {
        new Thread() {
            public void run() {
                Bitmap img = null;
                try

                {
                    URL url = new URL(urlImg);
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    InputStream input = conexao.getInputStream();
                    img = BitmapFactory.decodeStream(input);
                } catch (IOException e){}

                final Bitmap aux = img;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageView = new ImageView(getBaseContext());
                        imageView.setImageBitmap(aux);
                        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
                        ll.addView(imageView);
                    }
                });
            }
        }.start();
    }

    private void setTextView(final String text){
        new Thread(){
            public void run() {
                final String textAux = text;
                handler.post(new Runnable() {
                     @Override
                     public void run () {
                         TextView textView = new TextView(getBaseContext());
                         textView.setText(textAux);
                         LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
                         ll.addView(textView);
                     }
                 });
            }
        }.start();

    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
        atualizaGps();
    }

    private void setVibrator(){
        // vibration for 300 milliseconds
        ((Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            String umlImg = "http://www.uml-diagrams.org/examples/profile-example-dicom-2011.png";
            String calculoImg = "http://wwmat.mat.fc.ul.pt/aninf/2005_1/aninf1/t/24/2003_1/aninf1/material/ace/9.7a.jpg";

            // O numero da latitude e longitude tem que ter 8 digitos para ter uma melhor precisao
            String latitude_406 = "-23.1618";
            String longitude_406 ="-45.7955";
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            wifis.clear(); //add this
            for (int i = 0; i < wifiScanList.size(); i++) {
                String ssid = wifiScanList.get(i).SSID; //Get the SSID
                String bssid =  wifiScanList.get(i).BSSID; //Get the BSSID
                int level =  wifiScanList.get(i).level; //Get the level

                //use add here for get information about latitude, longitude and other informations about wifi:
//                if(ssid.equals("FatecSJC")&& bssid.equals("6c:19:8f:4d:b8:06")){
//                    wifis.add(ssid + " " + bssid + " " + level + " | " +String.valueOf(gps.getLocation().getLatitude()) + " | " + String.valueOf(gps.getLocation().getLongitude())); //append to the other data
//                }


                if((ssid.equals("FatecSJC") && bssid.equals("6c:19:8f:4d:b8:06") ) &&
                        (String.valueOf(gps.getLocation().getLatitude()).startsWith(latitude_406) && String.valueOf(gps.getLocation().getLongitude()).startsWith(longitude_406))
                        ) {
                    setTextView("Sala 407 - Calculo: ");
                    loadImg(calculoImg);
                    setVibrator();
                }
            }



            adapter.notifyDataSetChanged(); //add this
            wifiManager.startScan(); //start a new scan to update values faster

        }
    }
}




//    WifiManipulator wifi = new WifiManipulator();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//    }
//
//    public void onClick(View view){
//
//        TextView result = (TextView) findViewById(R.id.value);
//
//        List<ScanResult> results = wifi.getConections(this);
//
//        if(results!=null){
//
//            StringBuilder info = new StringBuilder();
//
//            for(ScanResult connection: results){
//                if(connection.SSID.equals("FatecSJC")) {
//                    result.setText(info.append("Wifi Network")
//                            .append("\n")
//                            .append("ID: ")
//                            .append(connection.SSID)
//                            .append("\n")
//                            .append(String.valueOf(connection.level)).append("\n\n"));
//                    if (connection.SSID.equals("FatecSJC") && (connection.level == -73)) {
//                        Toast.makeText(this, "Sala 406", Toast.LENGTH_LONG);
//                    }
//                }
//            }
//
//        } else {
//            result.setText("Sorry, no connections");
//        }
//
//    }
//
//}
