package com.moliying.mlymusicapp.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.moliying.mlymusicapp.MoliyingApp;
import com.moliying.mlymusicapp.service.PlayService;
import com.squareup.leakcanary.RefWatcher;

import org.greenrobot.eventbus.EventBus;

/**
 * descreption:
 * company: moliying.com
 * Created by vince on 16/7/20.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected PlayService playService;
    PlayService.PlayBinder playBinder;

    public MoliyingApp app;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, PlayService.class));
        initView();
        app = (MoliyingApp) getApplication();
        Log.i(TAG, "onCreate: " + app.list);
        app.list.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MoliyingApp.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //退出功能
    public void exit() {
        int size = app.list.size();
        for (int i = 0; i < size; i++) {
            app.list.get(i).finish();
        }
    }

    public void toast(String info) {
        Toast.makeText(BaseActivity.this, info, Toast.LENGTH_SHORT).show();
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.i(TAG, "onServiceConnected: " + IResultReceiver.Stub.asInterface(service));
//            IResultReceiver iResultReceiver = IResultReceiver.Stub.asInterface(service);
//            IBinder iBinder = iResultReceiver.asBinder();
//            Log.i(TAG, "onServiceConnected: " + iBinder.getClass());
//
//            playBinder = (PlayService.PlayBinder) iBinder;
//            Log.i(TAG, "onServiceConnected: " + playBinder.getClass());

            playBinder = (PlayService.PlayBinder)service;
            playService = playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playService.getCurrentPosition());
            Log.i(TAG, "onServiceConnected: onChange--"+playService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
            isBound = false;
        }
    };

    private PlayService.MusicUpdateListener musicUpdateListener =
            new PlayService.MusicUpdateListener() {
                @Override
                public void onPublish(long progress) {
                    //更新进度
                    publish(progress);
                }

                @Override
                public void onChange(long position) {
                    //改变状态
                    change(position);
                }
            };


    //用于更新进度
    public abstract void publish(long progress);

    //切换
    public abstract void change(long position);

    /**
     * 初始化视图组件
     */
    public abstract void initView();

    //绑定服务
    public void bindPlayService() {
        if (!isBound) {
            Intent intent = new Intent(this, PlayService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    //解除绑定服务
    public void unbindPlayService() {
        if (isBound) {
            unbindService(conn);
            isBound = false;
        }
    }
}
