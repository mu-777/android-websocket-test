package com.mu_777.websocket;

import android.app.Activity;
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


public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";
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
            Log.e(TAG, "Error!", error);
        }
    };

    //    WebSocketClient client = new WebSocketClient(URI.create("ws://0.0.0.0:3000"),

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusTextView = (TextView) findViewById(R.id.textView_status);

        Button connectButton = (Button) findViewById(R.id.btn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ipEditText = (EditText) findViewById(R.id.editText_serverip);
                EditText portEditText = (EditText) findViewById(R.id.editText_serverport);
                String address = "ws://" + ipEditText.getText().toString() + ":" + portEditText.getText().toString();
                Log.e(TAG, String.format("Address: %s", address));
                if (client != null) {
                    client.disconnect();
                    client = null;
                }
                client = new WebSocketClient(URI.create(address), wsListener, extraHeaders);
                client.connect();
            }
        });

        // ëóêMÉ{É^Éì
        Button sendButton = (Button) findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText msgEditText = (EditText) findViewById(R.id.editText_msg);
                client.send(msgEditText.getText().toString());
            }
        });

    }

    @Override
    protected void onDestroy() {
        client.disconnect();
        super.onDestroy();
    }
}

