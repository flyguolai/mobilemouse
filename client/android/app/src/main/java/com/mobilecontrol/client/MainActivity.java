package com.mobilecontrol.client;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilecontrol.client.data.TouchData;
import com.mobilecontrol.client.net.MobileControlClient;
import com.mobilecontrol.client.net.MobileControlClient.OnConnectListener;
import com.mobilecontrol.client.qrscan.QRScannerActivity;
import com.mobilecontrol.client.state.StateManager;

public class MainActivity extends Activity implements View.OnClickListener {

    protected static final String TAG = "MainActivity";
    private StateManager stateManager;

    private static final int REQ_CODE_SCAN = 100;

    private long mLongClickTime = 400;
    private int mSpeed = 1;
    private MobileControlClient mControlClient;
    private TextView mConnectionStatusView;

    private OnConnectListener mOnConnectListener = new OnConnectListener() {

        @Override
        public void onDisconnected(final boolean disconnectedByServer) {
            Log.d(TAG, "onDisconnected" + (disconnectedByServer ? " by server" : ""));
            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mConnectionStatusView.setText(disconnectedByServer
                            ? R.string.disconnected_by_server : R.string.disconnected);
                }
            });
        }

        @Override
        public void onFindServerComplete(final boolean found) {
            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mConnectionStatusView.setText(found ? R.string.connected : R.string.server_not_found);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);

        findViewById(R.id.touch_pad).setOnTouchListener(new OnTouchListener() {

            float lastX, lastY, downX, downY;
            long downTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int fingerCount = event.getPointerCount();
                int i = 0 ;
                int sumX = 0;
                int sumY = 0;
                for(i = 0 ; i < fingerCount; i++){
                    sumX += event.getX(i);
                    sumY += event.getY(i);
                }

                float x =  sumX / fingerCount;
                float y =  sumY / fingerCount;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:

                        downX = lastX = x;
                        downY = lastY = y;
                        downTime = System.currentTimeMillis();

                        stateManager.setState(stateManager.moveState);

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        downX = lastX = x;
                        downY = lastY = y;
                        if(event.getPointerCount() == 2){
                            stateManager.setState(stateManager.scrollState);
                        }else{
                            stateManager.nextState();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:


                        float dx = x - lastX;
                        float dy = y - lastY;
                        stateManager.move(dx,dy);
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_UP:
//                        float x = event.getX();
//                        float y = event.getY();
//                        float dx = x - downX;
//                        float dy = y - downY;
//                        if (dx < 2 && dy < 2) {
//                            // this is a click event
//                            TouchData td_c = new TouchData();
//                            long tx = System.currentTimeMillis() - downTime;
//                            Log.d(TAG, "tx " + tx);
//                            td_c.setType(tx > mLongClickTime ? TouchData.TOUCH_TYPE_LONG_CLICK
//                                    : TouchData.TOUCH_TYPE_CLICK);
////                        td_c.setX((int) x);
////                        td_c.setY((int) y);
//                            send(td_c);
//                        }
                        stateManager.prevState();
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        stateManager.prevState();
                        break;
                }
                return true;
            }
        });

        mConnectionStatusView = (TextView) findViewById(R.id.connection_status);

        MobileControlApp app = (MobileControlApp) getApplication();
        mControlClient = app.getMobileControlClient();
        if (mControlClient == null) {
            mControlClient = new MobileControlClient();
            mControlClient.setOnConnectListener(mOnConnectListener);
//            if (!mControlClient.isConnected()) {
//                mControlClient.findServer();
//            }

            app.setMobileControlClient(mControlClient);

            this.stateManager = new StateManager(mControlClient);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mControlClient.isConnected()) {
            mConnectionStatusView.setText(R.string.connected);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_auto_discovery:
                if (mControlClient.isConnected()) {
                    Toast.makeText(this, R.string.already_connected, Toast.LENGTH_LONG).show();
                } else {
                    autoDiscoverServer();
                }
                return true;
            case R.id.action_qr_scan:
                if (mControlClient.isConnected()) {
                    Toast.makeText(this, R.string.already_connected, Toast.LENGTH_LONG).show();
                } else {
                    startQRScan();
                }
                return true;
            case R.id.fast_connect:
                if (mControlClient.isConnected()) {
                    Toast.makeText(this, R.string.already_connected, Toast.LENGTH_LONG).show();
                } else {
                    mControlClient.fastConnectByQrCode();
                }
                return true;
            case R.id.action_disconnect:
                disconnect();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startQRScan() {
        mControlClient.onQRScanStart();
        startActivityForResult(new Intent(this, QRScannerActivity.class), REQ_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SCAN) {
            if (resultCode == RESULT_OK) {
                String result = null;
                if (data != null) {
                    result = data.getStringExtra(QRScannerActivity.QR_SCAN_RESULT);
                }
                mControlClient.onQRScanEnd(result);
            } else {
                mControlClient.onQRScanEnd(null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mControlClient.disconnect();
    }

    private void autoDiscoverServer() {
        mControlClient.findServer();
    }

    private void disconnect() {
        mControlClient.disconnect();
    }

    private void sendHello() {
        mControlClient.send("Hello");
    }

    private void send(TouchData td) {
        if (td == null || !mControlClient.isConnected()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(td.getHead()).append(",")
                .append(td.getType()).append(",")
                .append(td.getX()).append(",")
                .append(td.getY());
        String jsonStr = sb.toString();
        Log.d(TAG, "send: " + jsonStr);
        mControlClient.send(jsonStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                onLeftClicked();
                break;
            case R.id.btn_right:
                onRightClicked();
                break;
        }
    }

    private void onRightClicked() {
        TouchData td_c = new TouchData();
        td_c.setType(TouchData.TOUCH_TYPE_LONG_CLICK);
        send(td_c);
    }

    private void onLeftClicked() {
        TouchData td_c = new TouchData();
        td_c.setType(TouchData.TOUCH_TYPE_CLICK);
        send(td_c);
    }
}
