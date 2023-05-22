package com.earth.angel.util;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.earth.angel.base.EarthAngelApp;
import com.google.firebase.analytics.FirebaseAnalytics;

public class DataReportUtils {
    private static volatile DataReportUtils instance;
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static DataReportUtils getInstance() {
        if (instance == null) {
            instance = new DataReportUtils();
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    private DataReportUtils() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(EarthAngelApp.instance);
    }

    /**
     * 埋点
     *
     * @param key
     * @param value
     */
    public void report(String key, Bundle value) {
        if (value == null) {
            value = new Bundle();
        }
        mFirebaseAnalytics.logEvent(key, value);
    }

    /**
     * 埋点
     *
     * @param key
     */
    public void report(String key) {
        report(key, null);
    }

}
