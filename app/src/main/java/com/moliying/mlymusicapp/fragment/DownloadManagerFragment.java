package com.moliying.mlymusicapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.adapter.DownloadManagerListAdapter;
import com.moliying.mlymusicapp.utils.Constant;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadManagerFragment extends BaseFragment {

    private static final String TAG = "DownloadManagerFragment";
    private ListView listView_my_music;
    private OnDownloadManagerListener onDownloadManagerListener;
    private DownloadManagerListAdapter adapter;
    private Toolbar toolbar;

    public interface OnDownloadManagerListener{
        public void loadDownloadManagerData();
    }
    public DownloadManagerFragment() {
    }

    public static DownloadManagerFragment newInstance() {

        Bundle args = new Bundle();

        DownloadManagerFragment fragment = new DownloadManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDownloadManagerListener = (OnDownloadManagerListener) getActivity();

    }

    @Override
    public void onDetach() {
        onDownloadManagerListener = null;
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_manager, container, false);
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
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.footer_layout,null);
        listView_my_music.addFooterView(footerView);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void initData() {
        adapter = new DownloadManagerListAdapter(getContext(),scanDownloadMusicCount());
        listView_my_music.setAdapter(adapter);
    }

    //扫描下载目录里的音乐文件
    private File[] scanDownloadMusicCount(){
        File file = new File(Environment.getExternalStorageDirectory()+ Constant.DIR_MLY_MUSIC);
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if(Constant.DIR_LRC.endsWith(s)){
                    return false;
                }
                return true;
            }
        });
        Log.i(TAG, "scanDownloadMusicCount: "+files.length);
        return files;
    }

}
