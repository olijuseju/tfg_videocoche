package com.example.jjpeajar.jors_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.microedition.khronos.egl.EGLConfig;

public class HomeActivity extends AppCompatActivity implements ConnectionLostCallback {


    private static final String TAG = "MainActivity";


    private TextView txNVel;
    private TextView txNAc;
    private ImageView btForward;
    private ImageView btBackward;
    private ImageView btLeft;
    private ImageView btRight;
    private ImageView btPlayPause;


    private Button btSt;
    private Button btStL;
    private Button btConn;
    private SeekBar seekBarVel;
    private SeekBar seekBarAc;


    private float vel;
    private float auxvel;
    private boolean isStopLento;
    private float ac;
    private float velTx;
    private float acTx;
    private boolean stop;
    private boolean esperarFin;
    private boolean onkeydown;
    private int dir;
    BLReceiver receiver;
    Context context;

    private BluetoothSocket btSocket;
    private static final String APP_NAME = "Jors_1";
    private boolean connection;

    public WebView webview;
    String videoUrl = "https://www.youtube.com/embed/QdeaC4YA0iA";
    BluetoothAdapter btAdapter;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final static int REQUEST_ENABLE_BT = 1;
    private boolean onKeyup;


    @Override
    protected void onDestroy() {
        if (connection){
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        btBackward=findViewById(R.id.btBackward);
        btForward=findViewById(R.id.btForward);
        btLeft=findViewById(R.id.btLeft);
        btRight=findViewById(R.id.btRight);
        btPlayPause=findViewById(R.id.btPlayPause);
        btSt=findViewById(R.id.btSt);
        btStL=findViewById(R.id.bt_st);
        btConn=findViewById(R.id.btConn);
        txNVel=findViewById(R.id.txNVel);
        txNAc=findViewById(R.id.txNAc);
        context=this;


        seekBarVel=findViewById(R.id.seekBarVel);
        seekBarAc=findViewById(R.id.seekBarAc);

        dir=1;

        ac=0.03f;
        vel=1f;
        auxvel=1f;

        acTx=ac*100;
        velTx=vel*10;
        stop=true;
        esperarFin=true;
        onkeydown=false;
        onKeyup=false;


        seekBarVel.setProgress((int)velTx);
        seekBarAc.setProgress((int)acTx);

        txNVel.setText(String.valueOf(vel));
        txNAc.setText(String.valueOf(ac));

        btPlayPause.setOnClickListener(this::onClick);

        webview = (WebView) findViewById(R.id.videoView);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(videoUrl);


        //btConn.setVisibility(View.INVISIBLE);


        dir=0;



        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("ConnUpdates"));

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        System.out.println(btAdapter.getBondedDevices());

        //BluetoothDevice hc05 = btAdapter.getRemoteDevice("40:91:51:1D:E0:7E");
        BluetoothDevice hc05 = btAdapter.getRemoteDevice("24:D7:EB:7D:DF:F2");
        try {
            if (hc05.getName().equals("Videocoche")) {
                btConn.setVisibility(View.VISIBLE);
            }
            System.out.println(hc05.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        isStopLento=true;


        if (savedInstanceState != null) {
            // Recupera los datos guardados en onSaveInstanceState
            connection = savedInstanceState.getBoolean("connection");
            stop = savedInstanceState.getBoolean("stop");
            isStopLento = savedInstanceState.getBoolean("isStopLento");
            dir = savedInstanceState.getInt("dir");
            if(connection){
                try {
                    btSocket = hc05.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                    System.out.println(btSocket);
                    btSocket.connect();
                    btConn.setText("Disconnect");


                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (isStopLento){
                    btStL.setText("START");

                }else{
                    btStL.setText("STOP");
                }

                if (stop){
                    btSt.setText("ON");
                }else{
                    btSt.setText("OFF");
                }


            }
        }



        btForward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isStopLento){
                    isStopLento=false;
                    vel=auxvel;
                    btStL.setText("STOP");
                }
                dir=1;
                MandarMensajeBt("Ve" + vel + ":", dir);
            }
        });
        btBackward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isStopLento){
                    isStopLento=false;
                    vel=auxvel;
                    btStL.setText("STOP");
                }
                dir=-1;
                MandarMensajeBt("Ve" + vel + ":", dir);
            }
        });

        btLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isStopLento){
                    isStopLento=false;
                    vel=auxvel;
                    btStL.setText("STOP");
                }
                dir=3;
                MandarMensajeBt("Ve" + vel + ":", dir);

            }
        });

        btRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isStopLento){
                    isStopLento=false;
                    vel=auxvel;
                    btStL.setText("STOP");
                }
                dir=2;
                MandarMensajeBt("Ve" + vel + ":", dir);
            }
        });


        btConn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Trying to pair with videocoche");
                if (!btAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }else{

                    IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                    receiver = new BLReceiver((ConnectionLostCallback) context);
                    registerReceiver(receiver, filter);
                    if(!connection){
                        try {
                            btSocket = hc05.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                            System.out.println(btSocket);
                            btSocket.connect();

                            System.out.println(btSocket.isConnected());
                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "CONECTADO CON VIDEOCOCHE", Toast.LENGTH_SHORT);

                            toast1.show();
                            btConn.setText("Disconnect");


                        } catch (IOException e) {
                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "NO SE PUDO CONECTAR CON VIDEOCOCHE", Toast.LENGTH_SHORT);

                            toast1.show();
                            e.printStackTrace();
                        }
                        connection=btSocket.isConnected();
                    }else{
                        try {
                            btSocket.close();
                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "VIDEOCOCHE DESCONECTADO", Toast.LENGTH_SHORT);

                            toast1.show();
                            stop = true;
                            isStopLento = false;
                            dir = 0;
                            btStL.setText("STOP");
                            btSt.setText("ON");
                            connection=false;
                            btConn.setText("Connect");


                        } catch (IOException e) {
                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "NO SE PUDO DESCONECTAR CON VIDEOCOCHE", Toast.LENGTH_SHORT);

                            toast1.show();
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        if (stop){
            btSt.setText("ON");
        }else{
            btSt.setText("OFF");
        }

        btSt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (connection){
                    if(stop){
                        btSt.setText("OFF");
                        stop = false;
                        MandarMensajeBt("St", 0);
                        Toast toast1 =
                                Toast.makeText(getApplicationContext(),
                                        "START", Toast.LENGTH_SHORT);

                        toast1.show();
                    }else{
                        btSt.setText("ON");
                        stop = true;
                        MandarMensajeBt("St", 1);
                        Toast toast1 =
                                Toast.makeText(getApplicationContext(),
                                        "STOP", Toast.LENGTH_SHORT);

                        toast1.show();
                    }
                }else{
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "No hay conexión con Videocoche", Toast.LENGTH_SHORT);

                    toast1.show();
                }

            }
        });

        btStL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connection){
                    if (isStopLento){
                        btStL.setText("STOP");
                        vel=auxvel;
                        Log.d("keys", "Stop lento start");
                        MandarMensajeBt("Ve" + String.valueOf(vel) + ":", dir);
                        isStopLento=false;
                    }else{
                        btStL.setText("START");
                        vel=0;
                        Log.d("keys", "Stop lento");
                        MandarMensajeBt("Ve" + String.valueOf(vel) + ":", dir);
                        isStopLento=true;
                    }
                }else{
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "No hay conexión con Videocoche", Toast.LENGTH_SHORT);

                    toast1.show();
                }
            }
        });


        seekBarAc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!isStopLento) {
                    progressChangedValue = (float) progress / 100;
                    ac = progressChangedValue;

                    txNAc.setText(String.valueOf(ac));
                }

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                MandarMensajeBt("Ac", progressChangedValue);
            }
        });
        seekBarVel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = (float)progress/10;
                auxvel=progressChangedValue;
                vel=progressChangedValue;
                txNVel.setText(String.valueOf(vel));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                vel=progressChangedValue;
                if (connection){
                    if(!stop){

                        if(dir!=0){
                            MandarMensajeBt("Ve" + vel + ":", dir);
                        }


                    }
                }else{
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "No estás conectado con Videocoche", Toast.LENGTH_SHORT);

                    toast1.show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
            Log.d("Working:", String.valueOf(resultCode));
            Toast.makeText(this, "Bluetooth Activado", Toast.LENGTH_SHORT).show();
        }else{
            Log.d("Working:", String.valueOf(resultCode));
            onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Guarda los datos importantes en el Bundle
        savedInstanceState.putBoolean("connection", connection);
        savedInstanceState.putBoolean("stop", stop);
        savedInstanceState.putBoolean("isStopLento", isStopLento);
        savedInstanceState.putBoolean("isCambioPantalla", true);
        savedInstanceState.putInt("dir", dir);

        // Llama al método padre para que guarde el estado de la interfaz
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onClick(View v){
        /*dialog=new ProgressDialog(HomeActivity.this);
        dialog.setMessage("Please wait... ");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        try {
            if (videoView.isPlaying()){
                Uri uri = Uri.parse(videoUrl);
                videoView.setVideoURI(uri);
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        btPlayPause.setImageResource(R.drawable.play60);

                    }
                });
            }else{
                videoView.pause();
                btPlayPause.setImageResource(R.drawable.pausa64);
            }

        }catch (Exception ex){

        }
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                dialog.dismiss();
                videoView.start();
            }
        });*/
    }

    private void MandarMensajeBt(String prefijo, float valor){
        String msg = prefijo + String.valueOf(valor);
        if(connection){

            try {
                Log.d("keys", msg);
                OutputStream outputStream = btSocket.getOutputStream();
                outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                if (valor!=0f){
                    if (onKeyup){
                        MandarMensajeBt("Ve0.0:", 0);
                    }
                }

            } catch (IOException e) {
                connection = false;
                stop = true;
                isStopLento = false;
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "No hay conexión con Videocoche", Toast.LENGTH_SHORT);

                toast1.show();

                e.printStackTrace();
            }

        }else{

            Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                            "No estás conectado con Videocoche", Toast.LENGTH_SHORT);

            toast1.show();
        }
    }

    private void MandarMensajeBt(String prefijo, int valor){

        String msg = prefijo + String.valueOf(valor);
        if(connection){
            try {
                Log.d("keys", msg);
                OutputStream outputStream = btSocket.getOutputStream();
                outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                if (valor!=0){
                    if (onKeyup){
                        MandarMensajeBt("Ve0.0:", 0);
                    }
                }
            } catch (IOException e) {
                connection = false;
                stop = true;
                isStopLento = false;
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "No hay conexión con Videocoche", Toast.LENGTH_SHORT);

                toast1.show();
                e.printStackTrace();
            }

        }else{

            Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                            "No estás conectado con Videocoche", Toast.LENGTH_SHORT);

            toast1.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (esperarFin) {
                esperarFin=false;
                esperarYMensaje(1500);
                switch (keyCode) {
                    case 90:
                        dir = 1;
                        Log.d("keys", "alante");
                        MandarMensajeBt("Ve" + String.valueOf(vel) + ":", dir);
                        return true;

                    case 87:
                       dir = 2;
                       Log.d("keys", "derecha");
                       MandarMensajeBt("Ve" + String.valueOf(vel) + ":", dir);
                       return true;

                    case 88:
                       dir = 3;
                       Log.d("keys", "izquierda");
                       MandarMensajeBt("Ve" + String.valueOf(vel) + ":", dir);
                       return true;

                    case 89:
                       dir = -1;
                       Log.d("keys", "atrás");
                       MandarMensajeBt("Ve" + String.valueOf(vel) + ":", dir);
                       return true;

                    case 85:
                        Log.d("keys", "Stop");

                        if (stop) {
                            btSt.setText("OFF");
                            stop = false;
                            MandarMensajeBt("St", 0);
                        } else {
                            btSt.setText("ON");
                            stop = true;
                            MandarMensajeBt("St", 1);
                        }
                        return true;
                    case 24:
                        if (isStopLento) {
                            btStL.setText("STOP");
                            vel = auxvel;
                            Log.d("keys", "Stop lento start");
                            MandarMensajeBt("Ve" + String.valueOf(vel) + ":", dir);
                            isStopLento = false;
                            return true;
                        } else {
                            btStL.setText("START");
                            vel = 0;
                            Log.d("keys", "Stop lento");
                            MandarMensajeBt("Ve" + String.valueOf(vel) + ":", dir);
                            isStopLento = true;
                            return true;
                        }

                    default:
                        return super.onKeyUp(keyCode, event);

                }
            }else{
                return false;
            }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        dir=0;
        if(esperarFin){
            Log.d("keys", "StopjOY");
            esperarYMensaje(1500);
            MandarMensajeBt("Ve" + String.valueOf(0.0) + ":", dir);
        }else{
            onkeydown=true;
        }
        return true;


    }

    public void esperarYMensaje(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
                esperarFin=true;
                if (onkeydown){
                    MandarMensajeBt("Ve" + String.valueOf(0.0) + ":", dir);
                    onkeydown=false;
                }
            }
        }, milisegundos);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (connection) {
                int state = intent.getIntExtra("STATE", -1);
                Log.d("receivor", String.valueOf(state));
                connection = false;
                stop = true;
                isStopLento = false;
                dir = 0;
                btStL.setText("STOP");
                btSt.setText("ON");
                btConn.setText("Connect");
                switch (state) {
                    case 13:
                        Toast toast1 =
                                Toast.makeText(getApplicationContext(),
                                        "BLUETOOTH DESCONECTADO", Toast.LENGTH_SHORT);

                        toast1.show();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    case -1:
                        Toast toast2 =
                                Toast.makeText(getApplicationContext(),
                                        "CONEXION PERDIDA", Toast.LENGTH_SHORT);

                        toast2.show();


                }
            }
        }
    };

    @Override
    public void connectionLost() {

    }
}




