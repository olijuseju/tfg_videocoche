package com.example.jjpeajar.jors_1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BLReceiver extends BroadcastReceiver {
    private ConnectionLostCallback listener;

    public BLReceiver(ConnectionLostCallback listener ){

        this.listener = listener;

    }
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_OFF){
            Log.d("receiver", String.valueOf(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)));
            Intent i = new Intent("ConnUpdates");
            i.putExtra("STATE", intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1));
            LocalBroadcastManager.getInstance(context).sendBroadcast(i);
        }else{
            Log.d("receiver", String.valueOf(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)));
            Intent i = new Intent("ConnUpdates");
            i.putExtra("STATE", intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1));
            LocalBroadcastManager.getInstance(context).sendBroadcast(i);
        }

    }
}
