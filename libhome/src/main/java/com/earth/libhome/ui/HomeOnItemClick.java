package com.earth.libhome.ui;

import com.earth.libbase.baseentity.BaseGiftEntity;

public interface HomeOnItemClick {
    void onItemClick();
    void onItemDeleteClick();
    void onItemOffClick();

    void onItemDelete( BaseGiftEntity str);
}
