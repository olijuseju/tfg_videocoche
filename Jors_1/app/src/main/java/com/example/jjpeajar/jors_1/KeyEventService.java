package com.example.jjpeajar.jors_1;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class KeyEventService extends Service {
    public BLReceiver receiver;
    /*boolean connection;
    boolean stop;
    boolean isStopLento;
    BluetoothSocket btSocket;
    BluetoothAdapter btAdapter;*/
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("keys", "hola");

         /*IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        receiver = new BLReceiver(getApplicationContext());
        registerReceiver(receiver, filter);
       connection = intent.getBooleanExtra("EXTRA_BOOLEAN_VALUE", false);
        stop = intent.getBooleanExtra("EXTRA_BOOLEAN_VALUE", false);
        isStopLento = intent.getBooleanExtra("EXTRA_BOOLEAN_VALUE", false);*/


/*
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(btAdapter.getBondedDevices());

        BluetoothDevice hc05 = btAdapter.getRemoteDevice("40:91:51:1D:E0:7E");
        try {
            if (hc05.getName().equals("Videocoche")) {
                Log.d("keys", "videocoche encontrado");
                btSocket = hc05.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);//
                System.out.println(btSocket);
                btSocket.connect();
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "CONECTADO CON VIDEOCOCHE", Toast.LENGTH_SHORT);

                toast1.show();
                connection=true;
                Intent i = new Intent("ConnUpdates");
                // You can also include some extra data.
                i.putExtra("Connection", connection);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
            }
            System.out.println(hc05.getName());
        } catch (Exception e) {
            Log.d("keys", "mierda");
            e.printStackTrace();
            Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                            "NO SE PUDO CONECTAR CON VIDEOCOCHE", Toast.LENGTH_SHORT);

            toast1.show();
        }*/

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast toast1 =
                Toast.makeText(getApplicationContext(),
                        "SERVICIO DESTRUIDO", Toast.LENGTH_SHORT);

        toast1.show();
        unregisterReceiver(receiver);
        super.onDestroy();
    }


/*
    private class KeyEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event != null && event.getAction() == KeyEvent.ACTION_UP) {
                    switch (event.getKeyCode()) {
                        case 90:
                            Log.d("keys", "alante");
                            MandarMensajeBt("Ve", 1);
                            break;
                        case 87:
                            Log.d("keys", "derecha");
                            MandarMensajeBt("Ve", 2);
                            break;
                        case 88:
                            Log.d("keys", "izquierda");
                            MandarMensajeBt("Ve", 3);
                            break;
                        case 89:
                            Log.d("keys", "atrás");
                            MandarMensajeBt("Ve", -1);
                            break;
                    }
                }
            }

            if (intent.getAction().equals("St0")) {
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "START", Toast.LENGTH_SHORT);

                toast1.show();
            }
        }


    }
*/

    /*Intent i = new Intent("ConnUpdates");
        // You can also include some extra data.
        i.putExtra("Stop", stop);
        i.putExtra("Connection", connection);
        i.putExtra("IsStopLento", isStopLento);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);*/
}
