package com.moliying.mlymusicapp.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/10.
 */
public class HttpUtils {
    private static final String TAG = "HttpUtils";
    //创建okHttpClient对象
    OkHttpClient mOkHttpClient = new OkHttpClient();

    /**
     * get请求无参请求
     * @param url
     * @param listener
     */
    public void get(String url,final RequestListener listener){
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

    public interface RequestListener{
        void response(byte[] bytes);
    }
}
