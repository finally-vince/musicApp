package com.moliying.mlymusicapp.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.andraskindler.quickscroll.QuickScroll;
import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.adapter.MusicListAdapter;
import com.moliying.mlymusicapp.utils.MediaUtils;
import com.moliying.mlymusicapp.vo.Mp3Info;

import java.util.ArrayList;

/**
 * descreption:
 * company: moliying.com
 * Created by vince on 16/6/20.
 */
public class MyMusicListFragment extends Fragment implements OnItemClickListener{

    private ListView listView_my_music;
    private QuickScroll quickscroll;
    private ArrayList<Mp3Info> mp3Infos;
    private MusicListAdapter myMusicListAdapter;
    private Toolbar toolbar;
    private OnMyMusicListListener onMyMusicListListener;

    public static interface OnMyMusicListListener{
        public void play(ArrayList<Mp3Info> list,int position);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onMyMusicListListener = (OnMyMusicListListener) getActivity();

    }

    @Override
    public void onDetach() {
        onMyMusicListListener = null;
        super.onDetach();
    }

    public static MyMusicListFragment newInstance() {

        Bundle args = new Bundle();

        MyMusicListFragment fragment = new MyMusicListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,null);
        listView_my_music = (ListView) view.findViewById(R.id.listView_my_music);
        listView_my_music.setOnItemClickListener(this);
        View footerView = inflater.inflate(R.layout.footer_layout,null);
        listView_my_music.addFooterView(footerView);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        loadData();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_music_list_menu, menu);

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

    /**
     * 加载本地音乐列表
     */
    public void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(getActivity());
        myMusicListAdapter = new MusicListAdapter(getActivity(),mp3Infos);
        listView_my_music.setAdapter(myMusicListAdapter);


//        initQuickScroll();

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

        if(onMyMusicListListener!=null){
            onMyMusicListListener.play(mp3Infos,position);
        }


    }
}
