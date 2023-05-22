package com.earth.libbase.entity;

import java.io.Serializable;

public class PhotoCutEntity implements Serializable {
    private String SdPath;// sd卡位置
  //  private String Path;// 服务器地址
  //  private String changePath;// 抠图服务器位置
   // private Boolean check =true;

/*    public PhotoCutEntity(String sdPath, String path, String changePath, Boolean check) {
        SdPath = sdPath;
        Path = path;
        this.changePath = changePath;
        this.check = check;
    }*/

    public PhotoCutEntity(String sdPath) {
       this.SdPath = sdPath;
    }

    public String getSdPath() {
        return SdPath;
    }

    public void setSdPath(String sdPath) {
        SdPath = sdPath;
    }

 /*   public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getChangePath() {
        return changePath;
    }

    public void setChangePath(String changePath) {
        this.changePath = changePath;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }*/
}
