package com.example.niaba.bluetoothlocalisator;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


       // verification de l'activation du bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            Toast.makeText(this, "Pas de Bluetooth",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Avec Bluetooth",
                    Toast.LENGTH_SHORT).show();


   if (!bluetoothAdapter.isEnabled()) {
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // position
    private void getPos(){

    }

    // obtenir les beacons
    private  void getBeacon(){

    }



}

