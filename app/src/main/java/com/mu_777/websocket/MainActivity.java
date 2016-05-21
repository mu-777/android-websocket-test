package com.mu_777.websocket;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Handler;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;


public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";

    private Handler mHandler;

    private WebSocketClient mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        if ("sdk".equals(Build.PRODUCT)) {
            // �G�~�����[�^�̏ꍇ��IPv6�𖳌�    ----1
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        try {

            URI uri = new URI("ws://10.4.143.75:3000");

            mClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "onOpen");
                }

                @Override
                public void onMessage(final String message) {
                    Log.d(TAG, "onMessage");
                    Log.d(TAG, "Message:" + message);
                    mHandler.post(new Runnable() {    // ----2
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(TAG, "onError");
                    ex.printStackTrace();
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "onClose");
                }
            };

            mClient.connect();
            Log.d(TAG, "connect");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // ���M�{�^��
        Button button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText) findViewById(R.id.edit);
                try {
                    // ���M
                    mClient.send(edit.getText().toString());
                } catch (NotYetConnectedException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

