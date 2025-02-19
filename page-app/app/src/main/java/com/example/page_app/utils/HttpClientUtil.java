package com.example.page_app.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// 封装okhttp  感觉有点多此一举 嘻嘻 🤭
public class HttpClientUtil {

    private final String baseUrl = "http://192.168.1.94:8888";

    private Context context;

    public interface CallBack {
        void onSuccess(JSONObject res);

        void onFailure(Exception e);
    }


    public HttpClientUtil(Context context){
        this.context = context;
    }
    public void get(String url, CallBack callBack, Map<String, String> params) {
        OkHttpClient client = new OkHttpClient();

        // 构建带有参数的url
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = baseUrl + urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject res = new JSONObject(response.body().string());
                        callBack.onSuccess(res);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
//                    callBack.onFailure(new IOException("请求失败"+response));
                    Log.d("HttpClientUtil", "onResponse: " + response.body().string());
                    Toast.makeText(context, "服务器错误：" + response.body().string(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBack.onFailure(e);
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void post(String url, CallBack callBack, Map<String, Object> params) {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .post(requestBody)
                .url(baseUrl + url)
                .build();
        Log.d("HttpClientUtil", "post: " + baseUrl + url);
        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject res = new JSONObject(response.body().string());
                        callBack.onSuccess(res);
                    } catch (JSONException e) {
                        Log.d("HttpClientUtil", "Exception: " + response.body().string());
                        throw new RuntimeException(e);
                    }
                } else {
//                    callBack.onFailure(new IOException("请求失败"+response));
                    Log.d("HttpClientUtil", "onResponse: " + response.body().string());
//                    Toast.makeText(context, "服务器错误：" + response.body().string(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBack.onFailure(e);
//                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });

    }




}
