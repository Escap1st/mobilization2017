package com.mobilization2017.application;

import android.app.Application;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

import io.realm.Realm;

/**
 * Created by Pelevin Igor on 05.04.2017.
 */

public class MobilizationApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        setLocale();
    }

    private void setLocale() {
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        String languageCode = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("language", "ru");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(languageCode));
        } else {
            conf.locale = new Locale(languageCode);
        }

        res.updateConfiguration(conf, dm);
    }
}
