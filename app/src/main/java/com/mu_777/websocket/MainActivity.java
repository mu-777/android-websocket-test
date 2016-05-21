package com.mu_777.websocket;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {


    private static final String TAG = "MainActivity";
    private static final double RAD2DEG = 180 / Math.PI;
    private SensorManager mSensorManager;
    float[] rotationMatrix = new float[9];
    float[] gravity = new float[3];
    float[] geomagnetic = new float[3];
    float[] attitude = new float[3];
    double azimuth = 0.0;
    double pitch = 0.0;
    double roll = 0.0;

    TextView statusTextView;
    List<BasicNameValuePair> extraHeaders = Arrays.asList(
            new BasicNameValuePair("Cookie", "session=abcd")
    );
    WebSocketClient client;
    WebSocketClient.Listener wsListener = new WebSocketClient.Listener() {
        @Override
        public void onConnect() {
//            statusTextView.setText("Connected!");
            Log.d(TAG, "Connected!");
        }

        @Override
        public void onMessage(String message) {
            Log.d(TAG, String.format("Got string message! %s", message));
        }

        @Override
        public void onMessage(byte[] data) {
            Log.d(TAG, String.format("Got binary message! %s", new String(data)));
        }

        @Override
        public void onDisconnect(int code, String reason) {
//            statusTextView.setText("Disconnected!");
            Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
        }

        @Override
        public void onError(Exception error) {
//            statusTextView.setText("Error!");
            Log.d(TAG, "Error!", error);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusTextView = (TextView) findViewById(R.id.textView_status);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
        }
        if (geomagnetic != null && gravity != null) {
            SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
            SensorManager.getOrientation(rotationMatrix, attitude);
            azimuth = attitude[0] * RAD2DEG;
            pitch = attitude[1] * RAD2DEG;
            roll = attitude[2] * RAD2DEG;
            if (client != null) {
                client.send(Integer.toString((int) azimuth));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void connectBtnClicked(View view) {
        EditText ipEditText = (EditText) findViewById(R.id.editText_serverip);
        EditText portEditText = (EditText) findViewById(R.id.editText_serverport);
        String address = "ws://" + ipEditText.getText().toString() + ":" + portEditText.getText().toString();
        Log.d(TAG, String.format("Address: %s", address));
        if (client != null) {
            client.disconnect();
            client = null;
        }
        client = new WebSocketClient(URI.create(address), wsListener, extraHeaders);
        client.connect();
        try {
            Thread.sleep(1000); //3000ƒ~ƒŠ•bSleep‚·‚é
        } catch (InterruptedException e) {
        }
    }

    public void sendBtnClicked(View view) {
        EditText msgEditText = (EditText) findViewById(R.id.editText_msg);
        client.send(msgEditText.getText().toString());
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        client.disconnect();
        super.onDestroy();
    }

}

