package com.earth.angel.user.side;

public class SortModel {

	private String imgUrl="";
    private String name="";
    private String userid="";

    private String letters="";//显示拼音的首字母
    private boolean select=false;
    private boolean isjoin=false;

    public boolean isIsjoin() {
        return isjoin;
    }

    public void setIsjoin(boolean isjoin) {
        this.isjoin = isjoin;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }
}
