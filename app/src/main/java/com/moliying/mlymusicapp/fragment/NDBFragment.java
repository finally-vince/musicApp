package com.moliying.mlymusicapp.fragment;


import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.adapter.NetMusicListAdapter;
import com.moliying.mlymusicapp.fragment.listener.OnNetListener;
import com.moliying.mlymusicapp.utils.JsoupUtils;
import com.moliying.mlymusicapp.vo.NetMusic;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NDBFragment extends BaseFragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "NDBFragment";
    private Toolbar toolbar;
    private ListView listView_ndb;
    private LinearLayout loading_layout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<NetMusic> netMusics = new ArrayList<>();
    private NetMusicListAdapter adapter;
    private OnNetListener onNetListener;
    public NDBFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onNetListener = (OnNetListener) getActivity();
    }

    @Override
    public void onDetach() {
        onNetListener = null;
        super.onDetach();
    }

    public static NDBFragment newInstance() {
        Bundle args = new Bundle();
        NDBFragment fragment = new NDBFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_ndb, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        loading_layout = (LinearLayout) view.findViewById(R.id.loading_layout);
        loading_layout.setVisibility(View.GONE);
        listView_ndb = (ListView) view.findViewById(R.id.listView_ndb);
        listView_ndb.setVisibility(View.GONE);
        listView_ndb.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(0xfffc9630,0xffec5157,0xfff235ad);
        adapter = new NetMusicListAdapter(getContext(),netMusics);
        listView_ndb.setAdapter(adapter);
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.footer_layout,null);
        listView_ndb.addFooterView(footerView);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

    }
    @Override
    public void initData() {
        if(netMusics.size()==0) {
            loading_layout.setVisibility(View.VISIBLE);
            listView_ndb.setVisibility(View.GONE);

            new JsoupUtils().getNetMusic(getContext(),JsoupUtils.TYPE_NDB,
                    new JsoupUtils.GetNetMusicListener() {
                @Override
                public void onSuccess(ArrayList<NetMusic> data) {
                    //Log.i(TAG, "onSuccess: --"+data);
                    netMusics = data;
                    adapter.setNetMusics(netMusics);
                    loading_layout.setVisibility(View.GONE);
                    listView_ndb.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(NetworkErrorException e) {
                    toast(e.getMessage());
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }else{
            adapter.setNetMusics(netMusics);
            loading_layout.setVisibility(View.GONE);
            listView_ndb.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(onNetListener!=null){
            onNetListener.playNet(netMusics,position);
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }
}
