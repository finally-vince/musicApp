package com.moliying.mlymusicapp.fragment.listener;

import com.moliying.mlymusicapp.vo.NetMusic;

import java.util.ArrayList;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/22.
 */
public interface OnNetListener {
    public void playNet(ArrayList<NetMusic> netMusics, int position);
}
