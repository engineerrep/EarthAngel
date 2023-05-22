package com.curvydatingplus.widget.clip;

import android.graphics.Bitmap;

public class ImageClipData {

    private Bitmap bitmap;
    private Bitmap cropBitmap;
    private int crop_x;

    private int crop_y;

    private int crop_witdh;

    private int crop_height;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getCrop_x() {
        return crop_x;
    }

    public void setCrop_x(int crop_x) {
        this.crop_x = crop_x;
    }

    public int getCrop_y() {
        return crop_y;
    }

    public void setCrop_y(int crop_y) {
        this.crop_y = crop_y;
    }

    public int getCrop_witdh() {
        return crop_witdh;
    }

    public void setCrop_witdh(int crop_witdh) {
        this.crop_witdh = crop_witdh;
    }

    public int getCrop_height() {
        return crop_height;
    }

    public void setCrop_height(int crop_height) {
        this.crop_height = crop_height;
    }

    public Bitmap getCropBitmap() {
        return cropBitmap;
    }

    public void setCropBitmap(Bitmap cropBitmap) {
        this.cropBitmap = cropBitmap;
    }
}
