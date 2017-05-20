package com.example.niaba.bluetoothlocalisator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    Button localize_button;
    Button talk_me_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //
        localize_button = (Button) findViewById(R.id.button);
        talk_me_button = (Button) findViewById(R.id.button2);

        if (localize_button == null|| talk_me_button == null){
            Log.e("MainActivity:OnCreate", "boutton pas init");
        }

        localize_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BluetoothLocalizationActivity.class);
                startActivity(i);
            }
        });


    }


}

