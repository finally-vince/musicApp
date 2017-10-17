package com.moliying.mlymusicapp.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.vo.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class AlbumImageFragment extends BaseFragment {

    private ImageView imageView1_album;
    private Bitmap bitmap;

    public AlbumImageFragment() {
        // Required empty public constructor
    }

    public static AlbumImageFragment newInstance() {

        Bundle args = new Bundle();
        AlbumImageFragment fragment = new AlbumImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //设置要显示的图片
    public void setBitmap(Bitmap bitmap) {
        if(imageView1_album!=null) {
            imageView1_album.setImageBitmap(bitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_image, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        imageView1_album = (ImageView) view.findViewById(R.id.imageView1_album);
    }

    @Override
    public void initData() {
        EventBus.getDefault().post(new MessageEvent(MessageEventType.UPDATE_ALBUM_IMAGE));
    }
}
