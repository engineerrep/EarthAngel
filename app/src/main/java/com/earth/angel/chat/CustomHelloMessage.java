package com.earth.angel.chat;


import com.earth.libbase.baseentity.BaseGiftEntity;
import com.earth.libbase.baseentity.ShipAddressEntity;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;

import java.util.ArrayList;

/**
 * 自定义消息的bean实体，用来与json的相互转化
 */
public class CustomHelloMessage {
    public static final int WANT = 0;
    public static final int ADDRESS = 1;
    String text = "";
    int type = WANT;
    String userName = "";
    String formUser = "";// 发送方是哪位
    ArrayList<BaseGiftEntity> list=new ArrayList<>();
    ShipAddressEntity shipAddress;



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    int version = TUIKitConstants.JSON_VERSION_UNKNOWN;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public int getVersion() {
        return version;
    }


    public void setVersion(int version) {
        this.version = version;
    }

    public String getFormUser() {
        return formUser;
    }

    public void setFormUser(String formUser) {
        this.formUser = formUser;
    }

    public ArrayList<BaseGiftEntity> getList() {
        return list;
    }

    public void setList(ArrayList<BaseGiftEntity> list) {
        this.list = list;
    }

    public ShipAddressEntity getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(ShipAddressEntity shipAddress) {
        this.shipAddress = shipAddress;
    }
}
