package com.moliying.mlymusicapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.moliying.mlymusicapp.MoliyingApp;
import com.squareup.leakcanary.RefWatcher;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/12.
 */
public abstract class BaseFragment extends Fragment {

    public abstract void initView(View view);
    public abstract void initData();

    public void toast(String info){
        Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MoliyingApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
