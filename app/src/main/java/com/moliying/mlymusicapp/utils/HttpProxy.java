package com.moliying.mlymusicapp.utils;

import java.util.HashMap;

/**
 * description:
 * company: moliying.com
 * Created by vince on 2017/3/13.
 */

public class HttpProxy implements Http {
    private static HttpProxy httpProxy;
    private Http http;
    private HttpProxy(){
        http = new OkHttp();
    }
    public static HttpProxy getInstance() {
        if(httpProxy==null){
            synchronized (HttpProxy.class){
                if(httpProxy == null){
                    httpProxy = new HttpProxy();
                }
            }
        }
        return httpProxy;
    }

    @Override
    public void get(String url, RequestListener listener) {
        http.get(url,listener);
    }
    @Override
    public void post(String url, HashMap<String, String> params, RequestListener listener) {
        http.post(url,params,listener);
    }
}