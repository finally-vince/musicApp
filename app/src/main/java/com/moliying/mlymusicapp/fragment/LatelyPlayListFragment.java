package com.moliying.mlymusicapp.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.andraskindler.quickscroll.QuickScroll;
import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.adapter.MusicListAdapter;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.vo.MessageEvent;
import com.moliying.mlymusicapp.vo.Mp3Info;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * descreption:
 * company: moliying.com
 * Created by vince on 16/6/20.
 */
public class LatelyPlayListFragment extends BaseFragment implements OnItemClickListener{

    private static final String TAG = "LatelyPlayListFragment";
    private ListView listView_my_music;
    private QuickScroll quickscroll;
    private ArrayList<Mp3Info> mp3Infos;
    private MusicListAdapter myMusicListAdapter;
    private Toolbar toolbar;
    private OnLatelyPlayListListener latelyPlayListListener;

    public interface OnLatelyPlayListListener{
        public void loadLatelyPlayData();
        public void playLately(ArrayList<Mp3Info> mp3Infos,int position);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        latelyPlayListListener = (OnLatelyPlayListListener) getActivity();

    }

    @Override
    public void onDetach() {
        latelyPlayListListener = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
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

    public static LatelyPlayListFragment newInstance() {

        Bundle args = new Bundle();

        LatelyPlayListFragment fragment = new LatelyPlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lately_play_list_layout,null);
        initView(view);
        initData();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
        }
        return true;
    }

    @Override
    public void initView(View view) {
        listView_my_music = (ListView) view.findViewById(R.id.listView_my_music);
        listView_my_music.setOnItemClickListener(this);
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.footer_layout,null);
        listView_my_music.addFooterView(footerView);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    /**
     * 加载本地音乐列表
     */
    public void initData() {
        if(latelyPlayListListener!=null){
            latelyPlayListListener.loadLatelyPlayData();
        }
////        initQuickScroll();

    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void showLatelyPlayList(MessageEvent event){
        if (event.type == MessageEventType.LOAD_LATELY_PLAY_LIST){
            Log.i(TAG, "showLatelyPlayList: "+event.type);
            mp3Infos = (ArrayList<Mp3Info>) event.data;
            myMusicListAdapter = new MusicListAdapter(getActivity(),mp3Infos);
            listView_my_music.setAdapter(myMusicListAdapter);
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    private void initQuickScroll() {
        quickscroll.init(QuickScroll.TYPE_POPUP_WITH_HANDLE, listView_my_music, myMusicListAdapter, QuickScroll.STYLE_HOLO);
        quickscroll.setFixedSize(1);
        quickscroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
//        quickscroll.setIndicatorColor(bgColor, bgColor, Color.WHITE);
//        quickscroll.setHandlebarColor(bgColor, bgColor, bgColor);
//        quickscroll.setPopupColor(bgColor,0,0,Color.WHITE,0);
        quickscroll.setPopupColor(QuickScroll.BLUE_LIGHT, QuickScroll.BLUE_LIGHT_SEMITRANSPARENT, 1, Color.WHITE, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(latelyPlayListListener!=null){
            latelyPlayListListener.playLately(mp3Infos,position);
        }


    }
}
