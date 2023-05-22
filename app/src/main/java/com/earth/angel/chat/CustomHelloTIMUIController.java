package com.earth.angel.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.earth.angel.R;
import com.earth.angel.base.EarthAngelApp;
import com.earth.angel.chat.adapter.ChatCustomListAdapter;
import com.earth.angel.view.NoScrollRecyclerView;
import com.earth.libbase.base.BaseApplication;
import com.earth.libbase.base.Constance;
import com.earth.libbase.baseentity.ShipAddressEntity;
import com.earth.libbase.util.BaseDateUtils;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

public class CustomHelloTIMUIController {

    private static final String TAG = CustomHelloTIMUIController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final CustomHelloMessage data, final int position, final MessageLayout.OnItemLongClickListener onItemLongClickListener, final MessageInfo info) {
        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(EarthAngelApp.instance).inflate(R.layout.test_custom_message_layout1, null, false);
        parent.addMessageContentView(view);
        // 自定义消息view的实现，这里仅仅展示文本信息，并且实现超链接跳转
        NoScrollRecyclerView mRlv = view.findViewById(R.id.CustomList);
        TextView mCustomTitle = view.findViewById(R.id.CustomTitle);
        mCustomTitle.setText(data.getText());
        TextView mCustomListText = view.findViewById(R.id.CustomListText);
        View mCustomListLine = view.findViewById(R.id.CustomListLine);
        TextView mCustomListCopy = view.findViewById(R.id.CustomListCopy);
        TextView mCustomListAddress = view.findViewById(R.id.CustomListAddress);

        if (data.type == CustomHelloMessage.WANT) {
            ChatCustomListAdapter adapter = new ChatCustomListAdapter(data.list);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EarthAngelApp.instance);
            mRlv.setLayoutManager(linearLayoutManager);
            mRlv.setAdapter(adapter);
            adapter.setOnItemClickListener((adapter1, view1, position1) -> {
                if (BaseDateUtils.INSTANCE.isFastClick()) {
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                            .withString("id", data.list.get(position1).getReleaseRecordId())
                            .withString("userid", data.list.get(position1).getUserId())
                            .navigation();
                }
            });
            mRlv.setVisibility(View.VISIBLE);
            mCustomListAddress.setVisibility(View.GONE);
            mCustomListText.setVisibility(View.GONE);
            mCustomListLine.setVisibility(View.GONE);
            mCustomListCopy.setVisibility(View.GONE);

        } else if (data.type == CustomHelloMessage.ADDRESS) {

            mRlv.setVisibility(View.GONE);
            mCustomListAddress.setVisibility(View.VISIBLE);
            mCustomListText.setVisibility(View.VISIBLE);
            mCustomListLine.setVisibility(View.VISIBLE);
            mCustomListCopy.setVisibility(View.VISIBLE);
            ShipAddressEntity bean = data.shipAddress;
            if(bean!=null){
                String str = bean.getNickName() + "\n" +
                        bean.getStreetAddress() + "\n" +
                        bean.getDistrict() +","+bean.getProvince()+" " +bean.getZipCode() + "\n"
                        + bean.getPhoneNumber();
                mCustomListAddress.setText(str);
                mCustomListCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) BaseApplication.instance
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        if (clipboard == null || str == null) {
                            return;
                        }
                        ClipData clip = ClipData.newPlainText("message", str);
                        clipboard.setPrimaryClip(clip);
                        ToastUtil.toastLongMessage("Copy Success");
                    }
                });
            }

        }
        //   String useridMine = EarthAngelApp.Companion.getMyProfileEntity().getId();
        //     String formUser = data.getFormUser();
       /*     if (formUser.equals(useridMine)) {
                //自己的
            } else {
                //别人
            }*/
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
    }

}
