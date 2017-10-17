package com.moliying.mlymusicapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.moliying.mlymusicapp.utils.Constant;
import com.moliying.mlymusicapp.vo.Mp3Info;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/6.
 */
public class MoliyingApp extends Application{

    private static final String TAG = "MoliyingApp";
    public static SharedPreferences sp;
    public DbManager dbManager;
    public static Context context;
    public ArrayList<Activity> list = new ArrayList<>();
    //当前的播放列表
    public ArrayList<Mp3Info> currPlayList = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        x.Ext.init(this);//初始化 XUtils
        sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);

        DbManager.DaoConfig config = new DbManager.DaoConfig()
                .setDbName(Constant.DB_NAME)
                .setDbVersion(Constant.VERSION)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启WAL,对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        Log.i(TAG, "onUpgrade: "+oldVersion);
                    }
                });
        dbManager = x.getDb(config);
//        try {
//            dbManager.dropDb();
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
        context = getApplicationContext();

        refWatcher = LeakCanary.install(this);

    }
    public static RefWatcher getRefWatcher(Context context) {
        MoliyingApp application = (MoliyingApp) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;


}
