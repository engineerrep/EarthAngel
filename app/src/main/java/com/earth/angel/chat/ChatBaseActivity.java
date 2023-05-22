package com.earth.angel.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.earth.angel.R;
import com.earth.angel.base.EarthAngelApp;
import com.earth.angel.util.ClickUtils;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IMEventListener;

/**
 * 登录状态的Activity都要集成该类，来完成被踢下线等监听处理。
 */
public class ChatBaseActivity extends AppCompatActivity {

    private static final String TAG = ChatBaseActivity.class.getSimpleName();

    // 监听做成静态可以让每个子类重写时都注册相同的一份。
    private static IMEventListener mIMEventListener = new IMEventListener() {
        @Override
        public void onForceOffline() {
            //    ToastUtil.toastLongMessage(EarthAngelApp.instance.getString(R.string.repeat_login_tip));
            logout(EarthAngelApp.instance);
        }

        @Override
        public void onUserSigExpired() {
            //   ToastUtil.toastLongMessage(EarthAngelApp.instance.getString(R.string.expired_login_tip));
            logout(EarthAngelApp.instance);
        }

    };

    public static void logout(Context context) {
   /*     DemoLog.i(TAG, "logout");
        UserInfo.getInstance().setToken("");
        UserInfo.getInstance().setAutoLogin(false);
        Intent intent = new Intent(context, LoginForDevActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.LOGOUT, true);
        context.startActivity(intent);*/
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //  DemoLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigation_bar_color));
            int vis = getWindow().getDecorView().getSystemUiVisibility();
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(vis);
        }
        TUIKit.addIMEventListener(mIMEventListener);
    }

    @Override
    protected void onStart() {
        //  DemoLog.i(TAG, "onStart");
        super.onStart();
    /*    boolean login = UserInfo.getInstance().isAutoLogin();
        if (!login) {
            ChatBaseActivity.logout(EarthAngelApp.instance);
        }*/
    }

    @Override
    protected void onResume() {
        //   DemoLog.i(TAG, "onResume");
        super.onResume();
        ClickUtils.clear();
    }

    @Override
    protected void onPause() {
        //   DemoLog.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        //    DemoLog.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //      DemoLog.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //   DemoLog.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
    }
}
