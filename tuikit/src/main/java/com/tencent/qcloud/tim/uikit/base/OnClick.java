package com.tencent.qcloud.tim.uikit.base;

import android.view.View;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class OnClick {
    public static final long LIMIT_DURATION = 1500L;	//时间间隔：1500ms

    public OnClick() {
    }

    public static View.OnClickListener setOnClickListener(final View.OnClickListener raw) {
        return (View.OnClickListener) Proxy.newProxyInstance(View.OnClickListener.class.getClassLoader(), new Class[]{View.OnClickListener.class}, new InvocationHandler() {
            long lastTime;

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (!"onClick".equals(method.getName())) {
                    return method.invoke(raw, args);
                } else {
                    long now = System.currentTimeMillis();
                    if (now - this.lastTime >= OnClick.LIMIT_DURATION) {
                        this.lastTime = now;
                        return method.invoke(raw, args);
                    } else {
                        return null;
                    }
                }
            }
        });
    }

}
