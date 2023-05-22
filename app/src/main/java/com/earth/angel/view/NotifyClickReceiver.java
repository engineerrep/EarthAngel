package com.earth.angel.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotifyClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String content = intent.getStringExtra("content");
        int type = intent.getIntExtra("type", -1);
        Toast.makeText(context, content + "" + type, Toast.LENGTH_LONG).show();




    }
}
