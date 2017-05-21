package com.example.niaba.bluetoothlocalisator;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import static com.example.niaba.bluetoothlocalisator.RecognitionListenerActivity.PERMISSIONS;

public class MainActivity extends AppCompatActivity {

    /* BLE PART !!! */

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    TextView textView;

    BluetoothLeScanner btScanner;
    Button startScanningButton;
    Button stopScanningButton;
    TextView peripheralTextView;

    HashMap<String, Integer> beacons;

    private HashMap<String, Point> beacons_pos;


    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    SimpleDateFormat sdf;
    String currentDateandTime;


    /* CHATBOT PART !!! */
    public static final int REQUEST_CODE_RECOGNITION_RESULT = 42;
    public static final int REQUEST_CODE_PARSING_RESULT = 51;
    private static final int MY_REQUEST_CODE = 51;

    private static final String UTTERANCE_ID_CHATBOT = "utterance_id_chatbot";
    public static final String MAIN_ACTIVITY = "MainActivity";

    private TextToSpeech TTS;

    Button localize_button;
    Button talk_me_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        localize_button = (Button) findViewById(R.id.button);
        talk_me_button = (Button) findViewById(R.id.button2);

        if (localize_button == null || talk_me_button == null) {
            Log.e(MAIN_ACTIVITY, "bouton pas init");
        }


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }


        sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());

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
////////////////////////////////////////////////////
        /*ACTIVITY FOR BLE TEST*/
        localize_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BluetoothLocalizationActivity.class);
                startActivity(i);
            }
        });
////////////////////////////////////////////////
        // button for text to speech activity
        talk_me_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(MainActivity.this, RecognitionListenerActivity.class);
                startActivityForResult(resultIntent, REQUEST_CODE_RECOGNITION_RESULT);
            }
        });

        TTS = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.e(MAIN_ACTIVITY, "TTS onInit");
            }
        });


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_REQUEST_CODE);
        }

        beacons = new HashMap<>();
        beacons_pos = new HashMap<>();
        beacons_pos.put("Carrefour_1", new Point(0, 0));
        beacons_pos.put("Carrefour_2", new Point(100, 0));
        beacons_pos.put("Carrefour_3", new Point(0, 100));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_RECOGNITION_RESULT:

                if (resultCode == RecognitionListenerActivity.RECOGNIZER_ACTIVITY_RESULT_NOT_OK) {
                    return;
                }

                String recognizerResult = data.getStringExtra(RecognitionListenerActivity.RECOGNIZER_RESULT);

                if (recognizerResult == null) {
                    return;
                }

                Intent parseIntent = new Intent(MainActivity.this, ChatBotActivity.class);
                parseIntent.putExtra(ChatBotActivity.SENTENCE_TO_PARSE, recognizerResult);
                parseIntent.putExtra(ChatBotActivity.X_COORD, 10);
                parseIntent.putExtra(ChatBotActivity.Y_COORD, 10);

                startActivityForResult(parseIntent, REQUEST_CODE_PARSING_RESULT);
                break;

            case REQUEST_CODE_PARSING_RESULT:
                String resultChatBot = data.getStringExtra(ChatBotActivity.CHATBOT_RESULT);
                Log.e(MAIN_ACTIVITY, "chat bot speaking");
                TTS.speak(resultChatBot, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID_CHATBOT);

                break;

            default:
                Log.e(MAIN_ACTIVITY, "unknown request code");
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        startScanning();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScanning();
    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            String device_name = result.getDevice().getName();
            int device_rssi = result.getRssi();
            if (device_name == null) {
                return;
            }
            String[] res = device_name.split("_");
            if (res == null) {
                return;
            }
            if (res[0].equals("Carrefour")) {
                if (!beacons.containsKey(device_name)) {
                    beacons.put(device_name, device_rssi);
                } else {
                    beacons.put(device_name, device_rssi);
                }
                currentDateandTime = sdf.format(new Date());
                Log.d("leScanCallback", currentDateandTime + " Device Name: " + device_name + " rssi: " + device_rssi + "\n");
                Log.d(device_name, "" + device_rssi);
            }

            /* Fetch information on 3 devices that we shall use for trilateration */
            ArrayList<Point> points = new ArrayList<>(3);
            ArrayList<Integer> powers = new ArrayList<>(3);

            if (beacons.keySet().size() >= beacons_pos.keySet().size()) {
                for (String s : beacons.keySet()) {
                    points.add(beacons_pos.get(s));
                    powers.add(beacons.get(s));
                }
            }

            final int min_trilat_devices = 3;

            if (points.size() >= min_trilat_devices && powers.size() >= min_trilat_devices) {
                Point current_pos = getCoordinateWithBeacon(points.get(0), powers.get(0),
                        points.get(1), powers.get(1),
                        points.get(2), powers.get(2));
            }
        }
    };

    public void startScanning() {
        System.out.println("start scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        System.out.println("stopping scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    public Point getCoordinateWithBeacon(Point beacon_a, float dB_a, Point beacon_b, float dB_b, Point beacon_c, float dB_c) {
        float W, Z, x, y, y2;

        W = dB_a * dB_a - dB_b * dB_b - beacon_a.x * beacon_a.x - beacon_a.y * beacon_a.y +
                beacon_b.x * beacon_b.x + beacon_b.y * beacon_b.y;
        Z = dB_b * dB_b - dB_c * dB_c - beacon_b.x * beacon_b.x - beacon_b.y * beacon_b.y +
                beacon_c.x * beacon_c.x + beacon_c.y * beacon_c.y;

        x = (W * (beacon_c.y - beacon_b.y) - Z * (beacon_b.y - beacon_a.y)) /
                (2 * ((beacon_b.x - beacon_a.x) * (beacon_c.y - beacon_b.y) - (beacon_c.x - beacon_b.x) * (beacon_b.y - beacon_a.y)));

        y = (W - 2 * x * (beacon_b.x - beacon_a.x)) / (2 * (beacon_b.y - beacon_a.y));

        y2 = (Z - 2 * x * (beacon_c.x - beacon_b.x)) / (2 * (beacon_c.y - beacon_b.y));

        y = (y + y2) / 2;

        return new Point((int) x, (int) y);
    }


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

}

