package com.example.niaba.bluetoothlocalisator;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

@TargetApi(21)

public class BluetoothLocalizationActivity extends Activity{


    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    TextView textView;

    BluetoothLeScanner btScanner;
    Button startScanningButton;
    Button stopScanningButton;
    TextView peripheralTextView;

    HashMap<String,Integer> beacons;

    private HashMap<String,Point> beacons_pos;


    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    SimpleDateFormat sdf ;
    String currentDateandTime ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_localization);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());


        peripheralTextView = (TextView) findViewById(R.id.PeripheralTextView);
        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());


        startScanningButton = (Button) findViewById(R.id.buttonStart);
        startScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startScanning();
            }
        });

        stopScanningButton = (Button) findViewById(R.id.buttonStop);
        stopScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopScanning();
            }
        });
        stopScanningButton.setVisibility(View.INVISIBLE);

        btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
        beacons = new HashMap<>();
        beacons_pos = new HashMap<>();
        beacons_pos.put("Carrefour_1",new Point(0,0));
        beacons_pos.put("Carrefour_2",new Point(100,0));
        beacons_pos.put("Carrefour_3",new Point(0,100));

    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {


            String device_name = result.getDevice().getName();
            int device_rssi = result.getRssi();
            if (device_name == null){
                return;
            }
            String[] res = device_name.split("_");
                if(res == null) {
                    return;
                }
                if(res[0].equals("Carrefour")){
                    if(!beacons.containsKey(device_name)){
                        beacons.put(device_name, device_rssi);
                    }else{
                        beacons.put(device_name, device_rssi);
                    }
                    currentDateandTime = sdf.format(new Date());
                    peripheralTextView.append(currentDateandTime +" Device Name: " + device_name+ " rssi: " + device_rssi + "\n");
                Log.d(device_name, ""+device_rssi);
            }

            ArrayList<Point> points = new ArrayList<>(3);
            ArrayList<Integer> powers = new ArrayList<>(3);


            if(beacons.keySet().size() >= beacons_pos.keySet().size()){
                for(String s: beacons.keySet()){
                    if(beacons_pos.containsKey(s)){
                        points.add(beacons_pos.get(s));
                        powers.add(beacons.get(s));
                    }
                }
            }
            if (points.size()>= 3 && powers.size() >= 3) {
                Point current_pos = getCoordinateWithBeacon(points.get(0), powers.get(0),
                        points.get(1), powers.get(1), points.get(2), powers.get(2));
                peripheralTextView.append(currentDateandTime + " Position: (" + current_pos.x + "," + current_pos.y + ") \n");
            }


            // auto scroll for text view
            final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
            // if there is no need to scroll, scrollAmount will be <=0
            if (scrollAmount > 0)
                peripheralTextView.scrollTo(0, scrollAmount);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void startScanning() {
        System.out.println("start scanning");
        peripheralTextView.setText("");
        startScanningButton.setVisibility(View.INVISIBLE);
        stopScanningButton.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        System.out.println("stopping scanning");
        peripheralTextView.append("Stopped Scanning");
        startScanningButton.setVisibility(View.VISIBLE);
        stopScanningButton.setVisibility(View.INVISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    public Point getCoordinateWithBeacon(Point beacon_a,float dB_a,Point beacon_b, float dB_b,Point beacon_c,float dB_c){
        float W, Z, x, y, y2;

        W = (dB_a*dB_a - dB_b*dB_b) - (beacon_a.x*beacon_a.x - beacon_a.y*beacon_a.y )+
                (beacon_b.x*beacon_b.x + beacon_b.y*beacon_b.y);

        Z = (dB_b*dB_b - dB_c*dB_c) - (beacon_b.x*beacon_b.x - beacon_b.y*beacon_b.y )+
                (beacon_c.x*beacon_c.x + beacon_c.y*beacon_c.y);

        x = (W * (beacon_c.y - beacon_b.y) - Z * (beacon_b.y - beacon_a.y)) /
                (2 * ((beacon_b.x-beacon_a.x)*(beacon_c.y-beacon_b.y) - (beacon_c.x-beacon_b.x)));

        y =  (W - 2*x*(beacon_b.x - beacon_a.x)) / (2 *(beacon_b.y - beacon_a.y));

        y2 = (Z - 2*x*(beacon_c.x - beacon_b.x)) / (2 *(beacon_c.y - beacon_b.y));

        y = (y + y2) / 2;

        return new Point((int)x,(int)y);
    }


}
