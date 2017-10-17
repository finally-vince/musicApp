package com.moliying.mlymusicapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.adapter.MusicListAdapter;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.vo.MessageEvent;
import com.moliying.mlymusicapp.vo.Mp3Info;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class MyLikeMusicListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "MyLikeMusicListFragment";
    private ListView listView_like;
    private ArrayList<Mp3Info> likeMp3Infos = new ArrayList<>();
    private MusicListAdapter adapter;
    private Toolbar toolbar;

    private OnMyLikeMusicListener onMyLikeMusicListener;

    public interface OnMyLikeMusicListener{
        public void loadMyLikeMusicData();
        public void playMyLikeMusic(ArrayList<Mp3Info> mp3Infos,int position);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onMyLikeMusicListener = (OnMyLikeMusicListener) getActivity();
    }

    @Override
    public void onDetach() {
        onMyLikeMusicListener = null;
        super.onDetach();
    }

    public static MyLikeMusicListFragment newInstance() {

        Bundle args = new Bundle();

        MyLikeMusicListFragment fragment = new MyLikeMusicListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_like_music_list, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        listView_like = (ListView) view.findViewById(R.id.listView_like);
        listView_like.setOnItemClickListener(this);
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.footer_layout,null);
        listView_like.addFooterView(footerView);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        initData();
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
    public void initData() {
        adapter = new MusicListAdapter(getContext(),likeMp3Infos);
        listView_like.setAdapter(adapter);
        if (onMyLikeMusicListener!=null) {
            onMyLikeMusicListener.loadMyLikeMusicData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void showMyLikeMusicList(MessageEvent event){
        if (event.type == MessageEventType.LOAD_MY_LIKE_MUSIC_LIST){
            Log.i(TAG, "showMyLikeMusicList: "+event.type);
            likeMp3Infos = (ArrayList<Mp3Info>) event.data;
            adapter.setMp3Infos(likeMp3Infos);
            adapter.notifyDataSetChanged();
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        onMyLikeMusicListener.playMyLikeMusic(likeMp3Infos,position);
    }

}
