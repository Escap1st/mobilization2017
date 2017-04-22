package com.mobilization2017.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.mobilization2017.R;

/**
 * Created by Pelevin Igor on 21.03.2017.
 */

public class RequestHandler extends Handler {

    public final static int STATUS_OK = 0,
            STATUS_NOT_OK = 1;

    private Context mContext;
    private boolean showPositiveMessage = false,
            showNegativeMessage = false;
    private Bundle mMessage;
    private int mStatus;

    public RequestHandler() {

    }

    public RequestHandler(Context context) {
        mContext = context;
    }

    public RequestHandler(Context context, boolean showPositiveMessage, boolean showNegativeMessage) {
        this.showNegativeMessage = showNegativeMessage;
        this.showPositiveMessage = showPositiveMessage;
        mContext = context;
    }

    public RequestHandler start() {
        onStart();
        return this;
    }

    public void onStart() {
    }


    public void onSuccess() {
    }


    public void onError() {
    }


    public void onFinish() {
    }

    @Override
    public void handleMessage(Message msg) {
        mMessage = msg.getData();
        mStatus = msg.what;

        if (mStatus == STATUS_OK) {
            onSuccess();
            if (showPositiveMessage && mMessage.containsKey("responseStatus")) {
                Toast.makeText(mContext, mMessage.getString("responseStatus"), Toast.LENGTH_LONG).show();
            }
        } else {
            onError();
            if (showNegativeMessage) {
                Toast.makeText(mContext, mMessage.containsKey("responseStatus") ?
                        mMessage.getString("responseStatus") :
                        mContext.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show();
            }
        }

        onFinish();
    }

    public Bundle getMessage() {
        return mMessage;
    }
}
