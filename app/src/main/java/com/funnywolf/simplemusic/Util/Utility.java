package com.funnywolf.simplemusic.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.funnywolf.simplemusic.Database.MusicItem;
import com.funnywolf.simplemusic.Database.MusicList;
import com.funnywolf.simplemusic.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utility {
    private static final String TAG = "SimpleMusic-Utility";

    private static OkHttpClient client = new OkHttpClient();
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    public static Response getOkHttpResponse(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            return null;
        }
    }
    public static String getOkHttpResponseString(String url) {
        Response response = getOkHttpResponse(url);
        if(response == null)
            return null;
        try {
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }
    public static byte[] getOkHttpResponseBytes(String url) {
        Response response = getOkHttpResponse(url);
        if(response == null)
            return null;
        try {
            return response.body().bytes();
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    private static final String requestBingPic =
            "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
    public static void loadBingPicture(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = Message.obtain(handler, MainActivity.MSG_BACKGROUND, "");
                do {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",
                            Locale.getDefault());
                    String imagePath = String.format(Locale.getDefault(),
                            "%s/BingPic_%s.jpg",
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getPath(),
                            sdf.format(new Date()));
                    File imageFile = new File(imagePath);
                    if (!imageFile.exists()) {
                        String responseString = getOkHttpResponseString(requestBingPic);
                        if (responseString == null) {
                            break;
                        }
                        String partUrl;
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            JSONArray jsonArray = jsonObject.getJSONArray("images");
                            partUrl = jsonArray.getJSONObject(0).getString("url");
                        } catch (JSONException e) {
                            break;
                        }
                        String imageUrl = "https://www.bing.com" + partUrl;
                        byte[] bytes = getOkHttpResponseBytes(imageUrl);
                        if (bytes == null) {
                            break;
                        }
                        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                            fos.write(bytes);
                            fos.flush();
                        } catch (IOException e) {
                            break;
                        }
                    }
                    msg.obj = imagePath;
                }while (false);
                handler.sendMessage(msg);

            }
        }).start();
    }

    public static void getAllMusic(Activity activity, MusicList<MusicItem> list) {
        if(activity == null || list == null)
            return;
        Cursor cursor = activity.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null,
                        MediaStore.Audio.Media.DATE_ADDED);
        if(cursor != null && cursor.moveToLast()) {
            while(!cursor.isBeforeFirst()){
                long id = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                Long size = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                list.add(new MusicItem(id, name, title, artist, path, duration, size));
                cursor.moveToPrevious();
            }
            cursor.close();
        }
    }
}
