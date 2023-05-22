package com.earth.angel.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.earth.libbase.network.request.PhotoAddDetail;

import java.util.ArrayList;

public class ContactUtils {
    public static ArrayList<PhotoAddDetail> getAllContacts(Context context) {
        ArrayList<PhotoAddDetail> contacts = new ArrayList<PhotoAddDetail>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //新建一个联系人实例
            PhotoAddDetail temp = new PhotoAddDetail();
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            temp.setFriendMobilePhoneName(name);
            //获取联系人电话号码
            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (phoneCursor.moveToNext()) {
                String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone = phone.replace("-", "");
                phone = phone.replace(" ", "");
                temp.setFriendMobilePhoneNumber(phone);
            }

     /*       //获取联系人备注信息
            Cursor noteCursor = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Nickname.NAME},
                    ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'",
                    new String[]{contactId}, null);
            if (noteCursor.moveToFirst()) {
                do {
                    String note = noteCursor.getString(noteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                    temp.setNote(note);
                } while (noteCursor.moveToNext());
            }*/
            if(temp.getFriendMobilePhoneNumber()!=null&&temp.getFriendMobilePhoneName()!=null){
                if(!temp.getFriendMobilePhoneNumber().isEmpty()&&!temp.getFriendMobilePhoneName().isEmpty()){
                    contacts.add(temp);
                }
            }


            //记得要把cursor给close掉
            phoneCursor.close();
          //  noteCursor.close();
        }
        cursor.close();
        return contacts;
    }
}
