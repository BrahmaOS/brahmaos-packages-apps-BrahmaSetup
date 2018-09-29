package io.brahmaos.setupwizard.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class NetworkMonitor {

    public static final String TAG = NetworkMonitor.class.getSimpleName();

    private static NetworkMonitor sInstance;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            BLog.v(TAG, intent.toString());
            NetworkMonitor.this.updateNetworkStatus(context);
        }
    };
    private Context mContext = null;
    private boolean mNetworkConnected = false;
    private NetworkInfo mNetworkInfo = null;

    public static void initInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkMonitor(context.getApplicationContext());
        }
    }

    public static NetworkMonitor getInstance() {
        return sInstance;
    }

    public NetworkMonitor(Context context) {
        mContext = context;
        BLog.v(TAG, "Starting NetworkMonitor");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(mBroadcastReceiver, filter);
        new Handler().post(new Runnable() {
            public void run() {
                updateNetworkStatus(mContext);
            }
        });
    }

    public boolean isNetworkConnected() {
        BLog.v(TAG, "isNetworkConnected() returns " + mNetworkConnected);
        return mNetworkConnected;
    }

    public boolean isWifiConnected() {
        boolean wifiConnected = (mNetworkConnected && mNetworkInfo != null &&
                mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI);
        BLog.v(TAG, "isWifiConnected() returns " + wifiConnected);
        return wifiConnected;
    }

    private void onNetworkConnected(NetworkInfo ni) {
        BLog.v(TAG, "onNetworkConnected()");
        mNetworkConnected = true;
        mNetworkInfo = ni;
    }

    private void onNetworkDisconnected() {
        BLog.v(TAG, "onNetworkDisconnected()");
        mNetworkConnected = false;
        mNetworkInfo = null;
    }

    private boolean updateNetworkStatus(Context context) {
        ConnectivityManager cm = context.getSystemService(ConnectivityManager.class);
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            boolean isConnected = ni != null ? ni.isConnected() : false;
            if (isConnected && !mNetworkConnected) {
                onNetworkConnected(ni);
            } else if (!isConnected && mNetworkConnected) {
                onNetworkDisconnected();
            }
        }
        return mNetworkConnected;
    }
}
