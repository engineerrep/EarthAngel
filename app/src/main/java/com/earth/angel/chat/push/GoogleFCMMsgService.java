package com.earth.angel.chat.push;

import com.google.firebase.messaging.FirebaseMessagingService;

public class GoogleFCMMsgService extends FirebaseMessagingService {
    private final String TAG = GoogleFCMMsgService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
       // DemoLog.i(TAG, "google fcm onNewToken : " + token);

        ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
    }
}
