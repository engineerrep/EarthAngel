package com.earth.libbase.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


import com.earth.libbase.network.RetrofitManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class ShareUtil {

    private static final String EMAIL_ADDRESS = "earthangelteam@outlook.com";
    private static final String ADDRESS = "https://www.earthangel.app:13002/share/index.html?id=";

    private static final String USERADDRESS = "https://www.earthangel.app:13002/share/index.html?link=HSGI";


    public static void share(Context context) {
     /*   LinkGenerator linkGenerator = ShareInviteHelper.generateInviteUrl(context);
        linkGenerator.setChannel(PreferencesHelper.INSTANCE.getToken(context));
        //   linkGenerator.addParameter("af_cost_value","2.5");
        //   linkGenerator.addParameter("af_cost_currency","USD");
        //   optional - set a brand domain to the user invite link
        //   linkGenerator.setBrandDomain("brand.domain.com");
        CreateOneLinkHttpTask.ResponseListener listener = new CreateOneLinkHttpTask.ResponseListener() {
            @Override
            public void onResponse(String s) {
                Log.d("Invite Link", s);
                // write logic to let user share the invite link
                String image = s.substring(s.lastIndexOf("/"));
                shareText(context, USERADDRESS+image+"&userId=" +PreferencesHelper.INSTANCE.getUserId(context)  , "EarthAngel");
                EventBus.getDefault().post(new ShareSuccessEvent());
            }

            @Override
            public void onResponseError(String s) {
                // handle response error
            }
        };
        linkGenerator.generateLink(context, listener);*/
    }
    public static void shareUser(Context context,String userid) {
     /*   LinkGenerator linkGenerator = ShareInviteHelper.generateInviteUrl(context);
        linkGenerator.setChannel(PreferencesHelper.INSTANCE.getToken(context));
        //   linkGenerator.addParameter("af_cost_value","2.5");
        //   linkGenerator.addParameter("af_cost_currency","USD");
        //   optional - set a brand domain to the user invite link
        //   linkGenerator.setBrandDomain("brand.domain.com");
        CreateOneLinkHttpTask.ResponseListener listener = new CreateOneLinkHttpTask.ResponseListener() {
            @Override
            public void onResponse(String s) {
                Log.d("Invite Link", s);
                // write logic to let user share the invite link
                String image = s.substring(s.lastIndexOf("/"));
                shareText(context, USERADDRESS+image+"&userId=" +userid , "EarthAngel");
                EventBus.getDefault().post(new ShareSuccessEvent());
            }

            @Override
            public void onResponseError(String s) {
                // handle response error
            }
        };
        linkGenerator.generateLink(context, listener);*/
    }
    public static void email(Context context) {
       /* LinkGenerator linkGenerator = ShareInviteHelper.generateInviteUrl(context);
        linkGenerator.setChannel(PreferencesHelper.INSTANCE.getToken(context));
        //   linkGenerator.addParameter("af_cost_value","2.5");
        //   linkGenerator.addParameter("af_cost_currency","USD");
        //   optional - set a brand domain to the user invite link
        //   linkGenerator.setBrandDomain("brand.domain.com");
        CreateOneLinkHttpTask.ResponseListener listener = new CreateOneLinkHttpTask.ResponseListener() {
            @Override
            public void onResponse(String s) {
                Log.d("Invite Link", s);
                // write logic to let user share the invite link
                String image = s.substring(s.lastIndexOf("/"));
                Intent email =new  Intent(Intent.ACTION_SEND);
                email.setType( "plain/text");
                String[] emailReciver = {"earthangelteam@outlook.com"};
                String emailTitle = "EarthAngel";
                String emailContent = USERADDRESS+image+"&userId=" +PreferencesHelper.INSTANCE.getUserId(context);
                email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
                email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
                email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
                context.startActivity(Intent.createChooser(email, "Email"));
            }

            @Override
            public void onResponseError(String s) {
                // handle response error
            }
        };
        linkGenerator.generateLink(context, listener);*/
    }
    public static void shareGoods(Context context, String userid, String id) {
/*        LinkGenerator linkGenerator = ShareInviteHelper.generateInviteUrl(context);
        linkGenerator.setChannel(PreferencesHelper.INSTANCE.getToken(context));
        //   linkGenerator.addParameter("af_cost_value","2.5");
        //   linkGenerator.addParameter("af_cost_currency","USD");
        //   optional - set a brand domain to the user invite link
        //   linkGenerator.setBrandDomain("brand.domain.com");
        CreateOneLinkHttpTask.ResponseListener listener = new CreateOneLinkHttpTask.ResponseListener() {
            @Override
            public void onResponse(String s) {
                Log.d("Invite Link", s);
                // write logic to let user share the invite link
                String image = s.substring(s.lastIndexOf("/"));

                shareText(context, ADDRESS + id + "&userId=" + userid + "&link=HSGI" + image, "EarthAngel");
            }
            @Override
            public void onResponseError(String s) {
                // handle response error
            }
        };
        linkGenerator.generateLink(context, listener);*/
    }

    public static void shareUserText(Context context, String userID) {
        //String user=RetrofitManager.BASE_URL+"share-test/user/?userID="+userID;
        String user="https://www.earthangel.app/share/homepage/?userID="+userID;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, user);
        context.startActivity(Intent.createChooser(intent, "EarthAngel"));
    }
    public static void shareGiftText(Context context, String itemID) {
      //  String gift=RetrofitManager.BASE_URL+"share-test/item/?itemID="+itemID;
        String gift="https://www.earthangel.app/share/item/?itemID="+itemID;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, gift);
        context.startActivity(Intent.createChooser(intent, "EarthAngel"));
    }
    public static void shareGroup(Context context, String group) {
     //   String gift=RetrofitManager.BASE_URL+"share-test/community/?communityID="+group;
        String groupStr="https://www.earthangel.app/share/community/?communityID="+group;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, groupStr);
        context.startActivity(Intent.createChooser(intent, "EarthAngel"));
    }

    public static void shareImage(Context context, Uri uri, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static void sendEmail(Context context, String title) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + EMAIL_ADDRESS));
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static void sendMoreImage(Context context, ArrayList<Uri> imageUris, String title) {
        Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        mulIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(mulIntent, "多图文件分享"));
    }
    public static void sendMessgage(Context context,String title ) {
        Uri smstoUri = Uri.parse("smsto:"); // 解析地址
        Intent intent = new Intent(Intent.ACTION_VIEW,smstoUri);
        intent.putExtra("address",""); // 没有电话号码的话为默认的，即显示的时候是为空的
        intent.putExtra("sms_body",title); // 设置发送的内容
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }
}
