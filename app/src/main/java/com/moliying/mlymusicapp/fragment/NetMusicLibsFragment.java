package com.moliying.mlymusicapp.fragment;


import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.utils.JsoupUtils;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.vo.MessageEvent;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetMusicLibsFragment extends BaseFragment implements View.OnClickListener {

    private Banner banner;
    private LinearLayout linearLayout_ndb;
    private LinearLayout linearLayout_gtb;
    private LinearLayout linearLayout_lxb;
    private HashMap<String, String> bannerMap = new HashMap<>();


    public static NetMusicLibsFragment newInstance() {

        Bundle args = new Bundle();
        NetMusicLibsFragment fragment = new NetMusicLibsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_libs, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linearLayout_ndb:
                EventBus.getDefault().post(new MessageEvent(MessageEventType.SHOW_NET_NDB_FRAGMENT));
                break;
            case R.id.linearLayout_gtb:
                EventBus.getDefault().post(new MessageEvent(MessageEventType.SHOW_NET_GTB_FRAGMENT));
                break;
            case R.id.linearLayout_lxb:
                EventBus.getDefault().post(new MessageEvent(MessageEventType.SHOW_NET_LXB_FRAGMENT));
                break;
        }
    }

    @Override
    public void initView(View view) {
        banner = (Banner) view.findViewById(R.id.banner);
        linearLayout_ndb = (LinearLayout) view.findViewById(R.id.linearLayout_ndb);
        linearLayout_gtb = (LinearLayout) view.findViewById(R.id.linearLayout_gtb);
        linearLayout_lxb = (LinearLayout) view.findViewById(R.id.linearLayout_lxb);
        linearLayout_ndb.setOnClickListener(this);
        linearLayout_gtb.setOnClickListener(this);
        linearLayout_lxb.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if (bannerMap.size() == 0) {
            new JsoupUtils().getBannerImage(getContext(), new JsoupUtils.GetBannerImageListener() {
                @Override
                public void onSuccess(HashMap<String, String> data) {
                    if (data != null) {
                        bannerMap = data;
                        Iterator<String> keys = bannerMap.keySet().iterator();
                        ArrayList<String> images = new ArrayList<>();
                        while (keys.hasNext()) {
                            images.add(keys.next());
                        }
                        banner.setImages(images, new Banner.OnLoadImageListener() {
                            @Override
                            public void OnLoadImage(ImageView view, Object url) {
                                view.setAdjustViewBounds(true);
                                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                if (getContext() != null) {
                                    Glide.with(getContext())
                                            .load(url)
                                            .centerCrop()
                                            .crossFade()
                                            .into(view);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(NetworkErrorException e) {
                    toast(e.getMessage());
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
