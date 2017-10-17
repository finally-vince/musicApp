package com.moliying.mlymusicapp.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;

/**
 * description:
 * company: moliying.com
 * Created by vince on 2017/3/13.
 */

public class OkHttp implements Http {
    //创建okHttpClient对象
    OkHttpClient mOkHttpClient = new OkHttpClient();
    @Override
    public void get(String url, RequestListener listener) {
        //创建一个Request
        final Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                listener.response(response.body().bytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void post(String url, HashMap<String, String> params, RequestListener listener) {
        //post方法的实现略过...
    }
}
