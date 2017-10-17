package com.moliying.mlymusicapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.utils.Constant;
import com.moliying.mlymusicapp.utils.MediaUtils;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.vo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 本地音乐界面
 */
public class LocalMusicFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "LocalMusicFragment";

    private LinearLayout linearLayout_local_music;
    private LinearLayout linearLayout_lately_play;
    private LinearLayout linearLayout_download_manager;
    private RelativeLayout relativeLayout_like;
    private LinearLayout linearLayout_new_songsheet;
    private TextView textView_local_music_count;
    private TextView textView_recent_play;
    private TextView textView_download;
    private TextView textView_like;
    private UpdateListener updateListener;

    public static interface UpdateListener{
        public void updateData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateListener = (UpdateListener) getActivity();
    }

    @Override
    public void onDetach() {
        updateListener = null;
        super.onDetach();
    }

    public static LocalMusicFragment newInstance() {

        Bundle args = new Bundle();
        LocalMusicFragment fragment = new LocalMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //本地音乐
    void linearLayout_local_music(){
        EventBus.getDefault().post(
                new MessageEvent(
                        MessageEventType.SHOW_MY_MUSIC_LIST_FRAGMENT));
    }


    //最近播放
    void linearLayout_lately_play(){
        EventBus.getDefault().post(
                new MessageEvent(
                        MessageEventType.SHOW_LATELY_PLAY_LIST_FRAGMENT));
    }


    //下载管理
    void linearLayout_download_manager(){
        EventBus.getDefault().post(
                new MessageEvent(
                        MessageEventType.SHOW_DOWNLOAD_MANAGER_FRAGMENT));
    }

    //我喜欢的单曲
    void relativeLayout_like(){
        EventBus.getDefault().post(
                new MessageEvent(
                        MessageEventType.SHOW_MY_LIKE_MUSIC_LIST_FRAGMENT));
    }


    void linearLayout_new_songsheet(){

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        initView(view);
        //initData();
        return view;
    }

    @Override
    public void initData() {
        int count = MediaUtils.getMp3Count(getContext());
        textView_local_music_count.setText(count+" 首");
        if (updateListener!=null)updateListener.updateData();
        scanDownloadMusicCount();
    }

    //扫描下载目录里的音乐文件总数
    private void scanDownloadMusicCount(){
        File file = new File(Environment.getExternalStorageDirectory()+ Constant.DIR_MLY_MUSIC);
        String[] fileCount = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if(Constant.DIR_LRC.endsWith(s)){
                    return false;
                }
                return true;
            }
        });
        if (fileCount!=null) {
//        Log.i(TAG, "scanDownloadMusicCount: "+fileCount.length);
            textView_download.setText(fileCount.length + " 首");
        }
    }

    //更新下载管理音乐总数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void textView_download(MessageEvent event){
        if(event.type == MessageEventType.DOWNLOAD_MUSIC_COUNT) {
            textView_download.setText(event.data + " 首");
        }
    }

    //更新本地音乐总数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void textView_local_music_count(MessageEvent event){
        if(event.type == MessageEventType.UPDATE_LOCAL_MUSIC_COUNT) {
            textView_local_music_count.setText(event.data + " 首");
        }
    }

    //用户自主播放记录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setTextView_recent_play(MessageEvent event){
        if(event.type == MessageEventType.UPDATE_PLAY_RECORD_COUNT) {
            textView_recent_play.setText(event.data + " 首");
        }
    }

    //用户喜欢的单曲
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setTextView_like(MessageEvent event){
        if(event.type == MessageEventType.UPDATE_MY_LIKE_MUSIC_COUNT) {
            Log.i(TAG, "setTextView_like: "+event.data);
            textView_like.setText(event.data + " 首");
        }
    }

    @Override
    public void initView(View view) {
        linearLayout_local_music = (LinearLayout) view.findViewById(R.id.linearLayout_local_music);
        linearLayout_lately_play = (LinearLayout) view.findViewById(R.id.linearLayout_lately_play);
        linearLayout_download_manager = (LinearLayout) view.findViewById(R.id.linearLayout_download_manager);
        relativeLayout_like = (RelativeLayout) view.findViewById(R.id.relativeLayout_like);
        linearLayout_new_songsheet = (LinearLayout) view.findViewById(R.id.linearLayout_new_songsheet);
        textView_local_music_count = (TextView) view.findViewById(R.id.textView_local_music_count);
        textView_recent_play = (TextView) view.findViewById(R.id.textView_recent_play);
        textView_download = (TextView) view.findViewById(R.id.textView_download);
        textView_like = (TextView) view.findViewById(R.id.textView_like);
        linearLayout_local_music.setOnClickListener(this);
        linearLayout_lately_play.setOnClickListener(this);
        linearLayout_download_manager.setOnClickListener(this);
        relativeLayout_like.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linearLayout_local_music:
                //我的音乐
                linearLayout_local_music();
                break;
            case R.id.linearLayout_lately_play:
                //最近播放
                linearLayout_lately_play();
                break;
            case R.id.linearLayout_download_manager:
                //下载管理
                linearLayout_download_manager();
                break;
            case R.id.relativeLayout_like:
                //我喜欢的歌曲
                relativeLayout_like();
                break;
            case R.id.linearLayout_new_songsheet:
                //新建歌单
                linearLayout_new_songsheet();
                break;
        }
    }
}
