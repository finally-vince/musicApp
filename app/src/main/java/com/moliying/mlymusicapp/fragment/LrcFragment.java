package com.moliying.mlymusicapp.fragment;


import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.utils.Constant;
import com.moliying.mlymusicapp.utils.DownloadUtils;
import com.moliying.mlymusicapp.utils.MlyException;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import douzi.android.view.DefaultLrcBuilder;
import douzi.android.view.ILrcBuilder;
import douzi.android.view.ILrcView;
import douzi.android.view.LrcRow;
import douzi.android.view.LrcView;


public class LrcFragment extends BaseFragment {

    private String songName,artist;


    private LrcView lrcView;
    public LrcFragment() {
        // Required empty public constructor
    }

    public static LrcFragment newInstance() {

        Bundle args = new Bundle();
        LrcFragment fragment = new LrcFragment();
        fragment.setArguments(args);
        return fragment;
    }




    public void setSongNameAndArtist(String songName,String artist) {
        this.songName = songName;
        this.artist = artist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lrc, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        lrcView = (LrcView) view.findViewById(R.id.lrcView);
        //设置滚动事件
        lrcView.setListener(new ILrcView.LrcViewListener() {
            @Override
            public void onLrcSeeked(int newPosition, LrcRow row) {

            }
        });
        lrcView.setLoadingTipText(getString(R.string.loading_lrc));

//        lrcView.setBackgroundResource(R.mipmap);
//        lrcView.getBackground().setAlpha(150);
    }

    @Override
    public void initData() {
        load();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    //加载歌词
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void load() {
        File lrcDirFile = new File(Environment.getExternalStorageDirectory() + Constant.DIR_LRC+songName+"-"+artist+".lrc");
        if(lrcDirFile.exists()){
            loadLrc(lrcDirFile.getPath());

        }else {
            //下载歌词
            DownloadUtils.getInstance().downloadLRC(getContext(),songName, artist)
                    .setListener(new DownloadUtils.OnDownloadListener() {
                        @Override
                        public void onDownload(String result, MlyException e) {
                            if(e==null){
                                loadLrc(result);
                            }else{
                                tipText(e.toString());
                                lrcView.setLrc(null);
                            }

                        }
                    });
        }
    }

    //设置同步播放的时间进度
    public void seekLrcToTime(long time){
        lrcView.seekLrcToTime(time);
    }
    //设置提示内容
    public void tipText(String text){
        lrcView.setLoadingTipText(text);
    }


    public void loadLrc(String lrcPath) {
        //加载歌词
        if(lrcPath!=null && lrcView!=null) {
            File lrcFile = new File(lrcPath);
            StringBuffer buf = new StringBuffer(1024 * 10);
            char[] chars = new char[1024];
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile)));
                int len = -1;
                while ((len = in.read(chars)) != -1) {
                    buf.append(chars, 0, len);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ILrcBuilder builder = new DefaultLrcBuilder();

            List<LrcRow> rows = builder.getLrcRows(buf.toString());
            lrcView.setLrc(rows);

        }
    }
}
