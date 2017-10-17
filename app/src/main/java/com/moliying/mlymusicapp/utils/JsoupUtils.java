package com.moliying.mlymusicapp.utils;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.JsonToken;
import android.widget.Toast;

import com.moliying.mlymusicapp.vo.NetMusic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description:
 * company: www.moliying.com
 * Created by vince on 17/1/19.
 */
public class JsoupUtils {

    private static final String MP3_ROOT_URL = "http://mp3.sogou.com/";
    private static final int GET_BANNER_SUCCESS = 0x1;
    private static final int GET_BANNER_ERROR = 0x2;
    private static final int GET_NET_SUCCESS = 0x3;
    private static final int GET_NET_ERROR = 0x4;

    public static final int TYPE_NDB = 0x1;
    public static final int TYPE_GTB = 0x2;
    public static final int TYPE_LXB = 0x3;

    private static final String TAG = "JsoupUtils";
    private static ExecutorService es = Executors.newCachedThreadPool();

    private GetBannerImageListener getBannerImageListener;
    private GetNetMusicListener getNetMusicListener;


    public void toast(Context context){
        Toast.makeText(context, "当前网络状态不可用", Toast.LENGTH_SHORT).show();
    }

    public static interface GetBannerImageListener{
        public void onSuccess(HashMap<String,String> data);
        public void onError(NetworkErrorException e);
    }
    public static interface GetNetMusicListener{
        public void onSuccess(ArrayList<NetMusic> data);
        public void onError(NetworkErrorException e);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_BANNER_SUCCESS:
                    if( getBannerImageListener != null){
                        getBannerImageListener.onSuccess((HashMap<String, String>) msg.obj);
                    }
                    break;
                case GET_BANNER_ERROR:
                    if(getBannerImageListener != null) getBannerImageListener.onSuccess(null);
                    break;
                case GET_NET_SUCCESS:
                    if(getNetMusicListener!=null) {
                        getNetMusicListener.onSuccess((ArrayList<NetMusic>) msg.obj);
                    }
                    break;
                case GET_NET_ERROR:
                    if (getNetMusicListener!=null){
                        getNetMusicListener.onSuccess(null);
                    }
            }
        }
    };


    //获取歌曲
    public void getNetMusic(Context context,final int type,GetNetMusicListener listener){
        this.getNetMusicListener = listener;
        if(!AppUtils.isNetworkConnected(context)){
            getNetMusicListener.onError(new NetworkErrorException("当前网络不可用"));
            return;
        }
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(MP3_ROOT_URL).userAgent(Constant.USER_AGENT).timeout(100 * 1000).post();
                    String data = "";
                    if(TYPE_NDB==type) {//内地榜
                        data = doc.select("a[uigs=out_home_hot_list_china_playall]").attr("onclick").trim();
                    }else if(TYPE_GTB==type){//港台榜
                        data = doc.select("a[uigs=out_home_hot_list_jp_playall]").attr("onclick").trim();
                    }else if(TYPE_LXB==type){//流行榜
                        data = doc.select("a[uigs=out_home_hot_list_pop_playall]").attr("onclick").trim();
                    }
                    data = data.substring(data.indexOf("'") + 1, data.lastIndexOf("'"));
                    data = data.replaceAll("\\#", "\"");//把#转换为\
                    ArrayList<NetMusic> list = parseJson(data);
                    Message msg = handler.obtainMessage(GET_NET_SUCCESS,list);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    handler.sendEmptyMessage(GET_NET_ERROR);
                }
            }
        });
    }

    private ArrayList<NetMusic> parseJson(String data) {
        ArrayList<NetMusic> list = new ArrayList<>();
        if(data!=null) {
            StringReader in = new StringReader(data);
            JsonReader jr = new JsonReader(in);
            try {
                jr.beginArray();

                while (jr.hasNext()){
                    NetMusic netMusic = new NetMusic();
                    jr.beginArray();
                    while (jr.hasNext()){
                        if(jr.peek() != JsonToken.NULL)
                            netMusic.music.add(jr.nextString());
                        else{
                            netMusic.music.add(null);
                        }
                    }
                    list.add(netMusic);
                    jr.endArray();

                }
                jr.endArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Log.i(TAG, "parseJson: --"+list);
        return list;
    }

    //获取Banner图片
    public void getBannerImage(Context context,GetBannerImageListener listener){
        this.getBannerImageListener = listener;
        if(!AppUtils.isNetworkConnected(context)){
            getBannerImageListener.onError(new NetworkErrorException("当前网络不可用"));
            return;
        }
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(MP3_ROOT_URL)
                            .userAgent(Constant.USER_AGENT)
                            .timeout(100 * 1000).post();
                    //                System.out.println(doc);
                    Elements elements = doc.select("div.item");
                    //System.out.println(elements);
                    if(elements!=null) {
                        HashMap<String,String> bannerMap = new HashMap<>();
                        for (Element e : elements) {
                            String href = e.attr("href");
                            String imgUrl = e.getElementsByTag("img").attr("src").trim();
                            bannerMap.put(imgUrl,href);
                        }
                        Message msg = handler.obtainMessage(GET_BANNER_SUCCESS,bannerMap);
                        handler.sendMessage(msg);
                    }else{
                        handler.sendEmptyMessage(GET_BANNER_ERROR);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(GET_BANNER_ERROR);
                }
            }
        });
    }
}
