package com.mu_777.websocket;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ryosuke on 2016/05/21.
 */
public class MainView extends View {

    private static final String TAG = "MainView";

    WebSocketClient client;
    WebSocketClient.Listener wsListener;
    List<BasicNameValuePair> extraHeaders = Arrays.asList(
            new BasicNameValuePair("Cookie", "session=abcd")
    );

    TextView statusTextView;

    public MainView(Context context) {
        super(context, null);
        statusTextView = (TextView) findViewById(R.id.textView_status);

        initWsListenner();
    }

    public void wsConnect(String address) {
        if (client != null) {
            client.disconnect();
            client = null;
        }
        client = new WebSocketClient(URI.create(address), wsListener, extraHeaders);
        client.connect();
    }

    public void wsSendMsg(String msg) {
        client.send(msg);
    }

    public void wsDisconnect() {
        client.disconnect();
    }

    private void initWsListenner() {
        wsListener = new WebSocketClient.Listener() {
            @Override
            public void onConnect() {
                statusTextView.setText("Connected!");
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
                statusTextView.setText("Disconnected!");
                Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }

            @Override
            public void onError(Exception error) {
                statusTextView.setText("Error!");
                Log.d(TAG, "Error!", error);
            }
        };
    }

}
