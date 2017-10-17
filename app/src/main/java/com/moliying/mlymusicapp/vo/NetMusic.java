package com.moliying.mlymusicapp.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/21.
 */
public class NetMusic implements Serializable{
    //2 url, 3歌名 ,5 歌手,7 专辑名
    public ArrayList<String> music = new ArrayList<>();

    @Override
    public String toString() {
        return "NetMusic{" +
                "music=" + music +
                '}';
    }
}
