package com.funnywolf.simplemusic.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.funnywolf.simplemusic.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",
                        Locale.getDefault());
                String imagePath = String.format(Locale.getDefault(),
                        "%s/BingPic_%s.jpg",
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getPath(),
                        sdf.format(new Date()));
                File imageFile = new File(imagePath);
                if(!imageFile.exists()) {
                    String responseString = getOkHttpResponseString(requestBingPic);
                    if(responseString == null) {
                        handler.sendMessage(msg);
                        return;
                    }
                    String partUrl;
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONArray jsonArray = jsonObject.getJSONArray("images");
                        partUrl = jsonArray.getJSONObject(0).getString("url");
                    } catch (JSONException e) {
                        handler.sendMessage(msg);
                        return;
                    }
                    String imageUrl = "https://www.bing.com" + partUrl;
                    byte[] bytes = getOkHttpResponseBytes(imageUrl);
                    if(bytes == null) {
                        handler.sendMessage(msg);
                        return;
                    }
                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        fos.write(bytes);
                        fos.flush();
                    } catch (IOException e) {
                        handler.sendMessage(msg);
                        return;
                    }
                }
                msg.obj = imagePath;
                handler.sendMessage(msg);

            }
        }).start();
    }
}
