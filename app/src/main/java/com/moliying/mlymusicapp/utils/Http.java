package com.moliying.mlymusicapp.utils;

import java.util.HashMap;

/**
 * description:
 * company: moliying.com
 * Created by vince on 2017/3/13.
 */

public interface Http {
    public void get(String url,final RequestListener listener);
    public void post(String url, HashMap<String,String> params,final RequestListener listener);

    public interface RequestListener{
        void response(byte[] bytes);
    }
}
