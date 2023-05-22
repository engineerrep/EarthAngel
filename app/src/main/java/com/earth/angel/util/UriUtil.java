package com.earth.angel.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.earth.angel.base.EarthAngelApp;

import java.io.File;
import java.io.IOException;




public class UriUtil {

    private static File cacheDir = EarthAngelApp.instance.getCacheDir();


    public enum FileType {
        IMG,
        AUDIO,
        VIDEO,
        FILE,
    }



    public final static String getFileProviderName() {
        return "content://" + EarthAngelApp.instance.getPackageName() + ".fileProvider";
    }

    public static File getTempFile(FileType type) {
        try {
            File file = File.createTempFile(type.toString(), ".jpg", cacheDir);
            file.deleteOnExit();
            file.getParentFile().mkdirs();
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static Uri getUriForFileWithIntent(Context context, Intent intent, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = getUriForFile24(context, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    private static Uri getUriForFile24(Context context, File file) {
        //return FileProvider.getUriForFile(context, ViewUtils.getString(R.string.provider_paths), file);
        return FileProvider.getUriForFile(context, EarthAngelApp.instance.getPackageName() + ".fileProvider", file);
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }



    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (IllegalArgumentException e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

}
