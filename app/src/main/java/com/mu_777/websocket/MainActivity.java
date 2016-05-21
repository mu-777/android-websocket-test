package com.mu_777.websocket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";
    MainView mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = (MainView) findViewById(R.id.mainView);

        Button connectButton = (Button) findViewById(R.id.btn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ipEditText = (EditText) findViewById(R.id.editText_serverip);
                EditText portEditText = (EditText) findViewById(R.id.editText_serverport);
                String address = "ws://" + ipEditText.getText().toString() + ":" + portEditText.getText().toString();
                Log.d(TAG, String.format("Address: %s", address));
                mainView.wsConnect(address);
            }
        });

        Button sendButton = (Button) findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText msgEditText = (EditText) findViewById(R.id.editText_msg);
                String msg = msgEditText.getText().toString();
                mainView.wsSendMsg(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mainView.wsDisconnect();
        super.onDestroy();
    }
}

