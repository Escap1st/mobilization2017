package com.mobilization2017.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mobilization2017.R;
import com.mobilization2017.models.request.DictionaryRoot;
import com.mobilization2017.models.request.PossibleLanguage;
import com.mobilization2017.models.request.RequestError;
import com.mobilization2017.models.request.SupportedLanguages;
import com.mobilization2017.models.request.Translation;

import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

/**
 * Created by Pelevin Igor on 21.03.2017.
 */

public class TranslateApi {

    private static volatile TranslateApi sTranslateApi;

    private final static String TAG = "TranslateApi";

    private Context mContext;
    private AsyncHttpClient mHttpClient;
    private NetworkCheck mNetworkCheck;
    private String mTranslatePath, mDictionaryPath, mTranslateKey, mDictionaryKey;

    private TranslateApi(Context context) {
        mContext = context;
        mHttpClient = new AsyncHttpClient();
        mNetworkCheck = new NetworkCheck(context);
        mTranslatePath = mContext.getString(R.string.api_request_path_translate);
        mDictionaryPath = mContext.getString(R.string.api_request_path_dictionary);
        mTranslateKey = mContext.getString(R.string.api_credentials_translate_key);
        mDictionaryKey = mContext.getString(R.string.api_credentials_dictionary_key);
    }

    public static TranslateApi getInstance(Context context) {
        if (sTranslateApi == null) {
            synchronized (TranslateApi.class) {
                sTranslateApi = new TranslateApi(context.getApplicationContext());
            }
        }
        return sTranslateApi;
    }

    public void translateText(RequestHandler handler, String originalText, String languages) {
        if (mNetworkCheck.check(handler)) {
            mHttpClient.get(mContext.getString(R.string.api_request_translate, mTranslatePath, mTranslateKey,
                    originalText, languages), createResponseHandler(handler, Translation.class));
        }
    }

    public void lookupDictionary(RequestHandler handler, String originalText, String languages) {
        if (mNetworkCheck.check(handler)) {
            mHttpClient.get(mContext.getString(R.string.api_request_lookup_dictionary, mDictionaryPath, mDictionaryKey,
                    originalText, languages), createResponseHandler(handler, DictionaryRoot.class));
        }
    }

    public void getSupportedLanguages(RequestHandler handler, String locale) {
        if (mNetworkCheck.check(handler)) {
            mHttpClient.get(mContext.getString(R.string.api_request_get_languages_translate, mTranslatePath,
                    mTranslateKey, locale), createResponseHandler(handler, SupportedLanguages.class));
        }
    }

    public void detectLanguage(RequestHandler handler, String text) {
        if (mNetworkCheck.check(handler)) {
            mHttpClient.get(mContext.getString(R.string.api_request_detect_language, mTranslatePath, mTranslateKey,
                    text), createResponseHandler(handler, PossibleLanguage.class));
        }
    }

    private TextHttpResponseHandler createResponseHandler(final RequestHandler handler, final Class dataModel) {
        return new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dispatchResult(handler, responseString, dataModel);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorString, Throwable throwable) {
                dispatchError(handler, errorString, throwable);
            }
        };
    }

    private void dispatchResult(RequestHandler handler, String responseString, Class dataModel) {
        Log.d(TAG, mContext.getString(R.string.log_response_success, responseString));

        try {
            Parcelable result = (Parcelable) LoganSquare.parse(responseString, dataModel);
            Message resultMessage = handler.obtainMessage(RequestHandler.STATUS_OK);
            Bundle resultBundle = new Bundle();
            resultBundle.putParcelable("responseResult", result);
            resultMessage.setData(resultBundle);
            handler.dispatchMessage(resultMessage);
        } catch (Exception e) {
            e.printStackTrace();
            dispatchError(handler, null, null);
        }
    }

    private void dispatchError(RequestHandler handler, String errorString, Throwable throwable) {
        Log.d(TAG, mContext.getString(R.string.log_response_error, errorString));

        String errorMessage = "";

        if (throwable != null && (throwable instanceof SocketTimeoutException
                || throwable instanceof ConnectTimeoutException)) {
            errorMessage = mContext.getString(R.string.error_connection_timeout);
        } else {
            try {
                RequestError requestError = LoganSquare.parse(errorString, RequestError.class);
                errorMessage = requestError.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = mContext.getString(R.string.error_server_connection);
            }
        }

        dispatchErrorMessage(handler, errorMessage);
    }

    public static void dispatchErrorMessage(RequestHandler handler, String errorMessage) {
        Message resultMessage = handler.obtainMessage(RequestHandler.STATUS_NOT_OK);
        Bundle resultBundle = new Bundle();
        resultBundle.putString("responseStatus", errorMessage);
        resultMessage.setData(resultBundle);
        handler.dispatchMessage(resultMessage);
    }
}
