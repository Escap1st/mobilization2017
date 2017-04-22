package com.mobilization2017.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mobilization2017.R;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Pelevin Igor on 29.03.2017.
 */

public class NetworkCheck {

    private Context context;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;
    private ReentrantLock reentrantLock;

    public NetworkCheck(Context context) {
        this.context = context.getApplicationContext();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        reentrantLock = new ReentrantLock();
    }

    public boolean check(RequestHandler handler) {
        if (reentrantLock.isLocked()) {
            try {
                reentrantLock.wait();
            } catch (Exception e) {

            }
        }

        return isConnected(handler);
    }

    private boolean isConnected(RequestHandler handler) {
        reentrantLock.lock();
        boolean result = false;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnectedOrConnecting()) {
                result = true;
            } else {
                if (handler != null) {
                    TranslateApi.dispatchErrorMessage(handler, context.getString(R.string.error_network_connection));
                }
            }
        }
        reentrantLock.unlock();
        return result;
    }
}
