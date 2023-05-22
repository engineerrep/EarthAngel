package com.earth.libmine.ui;

import com.earth.libbase.baseentity.BaseGiftEntity;

import java.util.ArrayList;

public interface MineOnItemClick {
    void onItemClick();
    void onItemDelete( BaseGiftEntity str);
    void onItemDeleteALL(ArrayList<Long> list);
}
