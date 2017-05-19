package com.example.niaba.bluetoothlocalisator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by niaba on 20/05/17.
 */

public class BluetoothLocalizer{

    private Context my_context;

    public BluetoothLocalizer(Context context) {
        this.my_context = context;
    }

    public boolean is_BLE_enabled(){
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!this.my_context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d("test", "bluetooth low energy not supported....");
            Toast.makeText(this.my_context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.d("test","bluetooth low energy supported....");
        return true;
    }







}
