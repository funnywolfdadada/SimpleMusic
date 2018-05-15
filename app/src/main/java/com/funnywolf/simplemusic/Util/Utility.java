package com.funnywolf.simplemusic.Util;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utility {

    private static OkHttpClient client = new OkHttpClient();
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    private static final String requestBingPic =
            "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";

    public static void loadBingPicture(final Activity activity, final ImageView imageView) {
        sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(activity, "加载图片失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("images");
                    final String url = "https://www.bing.com"
                            + jsonArray.getJSONObject(0).getString("url");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RequestOptions options = new RequestOptions().centerCrop();
                            Glide.with(activity).load(url).apply(options).into(imageView);
                        }
                    });
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
