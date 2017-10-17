package com.moliying.mlymusicapp.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.fragment.DownloadManagerFragment;
import com.moliying.mlymusicapp.fragment.GTBFragment;
import com.moliying.mlymusicapp.fragment.IndexFragment;
import com.moliying.mlymusicapp.fragment.LXBFragment;
import com.moliying.mlymusicapp.fragment.LatelyPlayListFragment;
import com.moliying.mlymusicapp.fragment.LocalMusicFragment;
import com.moliying.mlymusicapp.fragment.MyLikeMusicListFragment;
import com.moliying.mlymusicapp.fragment.MyMusicListFragment;
import com.moliying.mlymusicapp.fragment.NDBFragment;
import com.moliying.mlymusicapp.fragment.listener.OnNetListener;
import com.moliying.mlymusicapp.service.PlayService;
import com.moliying.mlymusicapp.utils.MediaUtils;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.view.CardPopupWindow;
import com.moliying.mlymusicapp.vo.MessageEvent;
import com.moliying.mlymusicapp.vo.Mp3Info;
import com.moliying.mlymusicapp.vo.NetMusic;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;


/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/6.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener
        , MyMusicListFragment.OnMyMusicListListener
        , OnNetListener, LocalMusicFragment.UpdateListener
        , LatelyPlayListFragment.OnLatelyPlayListListener
        , MyLikeMusicListFragment.OnMyLikeMusicListener
        ,DownloadManagerFragment.OnDownloadManagerListener {

    private static final String TAG = "MainActivity";
    private FragmentManager fm;
    private NotificationManager nm;
    private IndexFragment indexFragment;
    private MyMusicListFragment myMusicListFragment;
    private LatelyPlayListFragment latelyPlayListFragment;
    private MyLikeMusicListFragment myLikeMusicListFragment;
    private DownloadManagerFragment downloadManagerFragment;
    private NDBFragment ndbFragment;
    private GTBFragment gtbFragment;
    private LXBFragment lxbFragment;
    private ImageView imageView1_album;
    private TextView textView1_song_name;
    private TextView textView2_singer;
    private ImageButton imageButton1_play_list;
    private ImageButton imageButton1_play_pause;
    private ImageButton imageButton2_next;
    private LinearLayout inc_bar;


    public void imageButton1_play_list() {
        new CardPopupWindow(this).showAsDropDown(inc_bar,0,0);
    }

    public void imageButton1_play_pause() {
        if (playService.isPlaying()) {
            playService.pause();
        } else {
            playService.play();
        }
    }

    public void imageButton2_next() {
        playService.next();
    }


    @Override
    public void publish(long progress) {

    }

    /**
     * 发送通知栏操作视图
     */
    private void sendNotification(Bitmap bitmap, String songName, String singer, boolean isPause) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_main);
        views.setTextColor(R.id.textView1_song_name,getResources().getColor(R.color.dark_black_text_color));
        views.setTextColor(R.id.textView2_singer,getResources().getColor(R.color.dark_black_text_color));
        //打开播放器主界面
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi_icon = PendingIntent.getActivity(this, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.imageView1_icon, pi_icon);

        //播放暂停
        Intent intentService = new Intent(PlayService.ACTION_PLAY_PAUSE);
        PendingIntent pi_play_pause = PendingIntent.getService(this, 0, intentService, 0);
        views.setOnClickPendingIntent(R.id.imageButton1_play_pause, pi_play_pause);
        //播放下一首
        Intent intentNext = new Intent(PlayService.ACTION_NEXT);
        PendingIntent pi_next = PendingIntent.getService(this, 0, intentNext, 0);
        views.setOnClickPendingIntent(R.id.imageButton2_next, pi_next);

        //关闭
        Intent intentClose = new Intent(PlayService.ACTION_EXIT);
        PendingIntent pi_close = PendingIntent.getService(this, 0, intentClose, 0);
        views.setOnClickPendingIntent(R.id.imageButton3_close, pi_close);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.music);
//                builder.setContentTitle(getTitle());
//                builder.setContentInfo("test");
//                builder.setTicker(getTitle());
        builder.setContent(views);
        builder.setOngoing(true);
//        builder.setContentIntent(pi_icon);

        views.setImageViewBitmap(R.id.imageView1_icon, bitmap);
        views.setTextViewText(R.id.textView1_song_name, songName);
        Log.i(TAG, "sendNotification: songName=" + songName);
        views.setTextViewText(R.id.textView2_singer, singer);
        if (isPause) {
            views.setImageViewResource(R.id.imageButton1_play_pause, R.mipmap.bt_notificationbar_play);
        } else {
            views.setImageViewResource(R.id.imageButton1_play_pause, R.mipmap.bt_notificationbar_pause);
        }

        nm.notify(PlayService.N_ID, builder.build());
        Log.i(TAG, "sendNotification: ");
    }

    @Override
    public void change(long position) {
        Log.i(TAG, "change: ");
        String songName = "";
        String singer = "";
        Bitmap bitmap = null;
        switch (playService.getType()) {
            case PlayService.TYPE_LOCAL:
                ArrayList<Mp3Info> mp3Infos = playService.getMp3Infos();
                if (mp3Infos != null) {
                    Mp3Info mp3Info =mp3Infos.get((int) position);
                    songName = mp3Info.getTitle();
                    singer = mp3Info.getArtist();
                    bitmap = MediaUtils.getArtwork(this, mp3Info.getMp3InfoId(), mp3Info.getAlbumId(), true, true);
                    imageView1_album.setImageBitmap(bitmap);
                    textView1_song_name.setText(songName);
                    textView2_singer.setText(singer);
                }
                break;
            case PlayService.TYPE_NET:
                ArrayList<NetMusic> netMusics = playService.getNetMusics();
                if (netMusics != null) {
                    NetMusic netMusic =netMusics.get((int) position);
                    songName = netMusic.music.get(3);
                    singer = netMusic.music.get(5);
                    textView1_song_name.setText(songName);
                    textView2_singer.setText(singer);
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.music);
                    imageView1_album.setImageBitmap(bitmap);
                }
                break;
        }
        updatePlayPauseStatus(songName, singer, bitmap);

    }

    //更新播放暂停按钮状态
    private void updatePlayPauseStatus(String songName, String singer, Bitmap bitmap) {
        Log.i(TAG, "updatePlayPauseStatus: " + playService.isPlaying());
        if (playService.isPlaying()) {
            imageButton1_play_pause.setImageResource(R.mipmap.bt_minibar_pause_normal);
            sendNotification(bitmap, songName, singer, false);
        } else {
            imageButton1_play_pause.setImageResource(R.mipmap.bt_minibar_play_normal);
            sendNotification(bitmap, songName, singer, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        unbindPlayService();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    //退出
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(MessageEvent event) {
        if (event.type == MessageEventType.EXIT_APP) {
            //nm.cancelAll();
            //stopService(new Intent(this, PlayService.class));
            exit();
        }
    }



    /**
     * 显示内地榜
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showNetMusicListNDBFragment(MessageEvent event) {
        if (event.type == MessageEventType.SHOW_NET_NDB_FRAGMENT) {
            if (ndbFragment == null) {
                ndbFragment = NDBFragment.newInstance();
            }
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.frame_layout_main, ndbFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if (indexFragment != null) {
                transaction.hide(indexFragment);
            }
        }
    }

    /**
     * 显示港台榜
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showNetMusicListGTBFragment(MessageEvent event) {
        if (event.type == MessageEventType.SHOW_NET_GTB_FRAGMENT) {
            if (gtbFragment == null) {
                gtbFragment = GTBFragment.newInstance();
            }
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.frame_layout_main, gtbFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if (indexFragment != null) {
                transaction.hide(indexFragment);
            }
        }
    }

    /**
     * 显示流行榜
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showNetMusicListXLBFragment(MessageEvent event) {
        if (event.type == MessageEventType.SHOW_NET_LXB_FRAGMENT) {
            if (lxbFragment == null) {
                lxbFragment = LXBFragment.newInstance();
            }
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.frame_layout_main, lxbFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if (indexFragment != null) {
                transaction.hide(indexFragment);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void myLikeMusicListFragment(MessageEvent event) {
        if (event.type == MessageEventType.SHOW_MY_LIKE_MUSIC_LIST_FRAGMENT) {
            if (myLikeMusicListFragment == null) {
                myLikeMusicListFragment = MyLikeMusicListFragment.newInstance();

            }
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.frame_layout_main, myLikeMusicListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if (indexFragment != null) {
                transaction.hide(indexFragment);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDownloadManagerFragment(MessageEvent event) {
        if (event.type == MessageEventType.SHOW_DOWNLOAD_MANAGER_FRAGMENT) {
            if (downloadManagerFragment == null) {
                downloadManagerFragment = DownloadManagerFragment.newInstance();

            }
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.frame_layout_main, downloadManagerFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if (indexFragment != null) {
                transaction.hide(indexFragment);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showLatelyPlayListFragment(MessageEvent event) {
        if (event.type == MessageEventType.SHOW_LATELY_PLAY_LIST_FRAGMENT) {
            if (latelyPlayListFragment == null) {
                latelyPlayListFragment = LatelyPlayListFragment.newInstance();

            }
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.frame_layout_main, latelyPlayListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if (indexFragment != null) {
                transaction.hide(indexFragment);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMyMusicListFragment(MessageEvent event) {
        if (event.type == MessageEventType.SHOW_MY_MUSIC_LIST_FRAGMENT) {
            if (myMusicListFragment == null) {
                myMusicListFragment = MyMusicListFragment.newInstance();

            }
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.frame_layout_main, myMusicListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if (indexFragment != null) {
                transaction.hide(indexFragment);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showIndexFragment(MessageEvent event) {
        if (event.type == MessageEventType.SHOW_INDEX_FRAGMENT) {
            if (indexFragment == null) {
                indexFragment = indexFragment.newInstance();
            }
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.frame_layout_main, indexFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if (myMusicListFragment != null) {
                transaction.hide(myMusicListFragment);
            }
            if (latelyPlayListFragment != null) {
                transaction.hide(latelyPlayListFragment);
            }
            if (myLikeMusicListFragment != null) {
                transaction.hide(myLikeMusicListFragment);
            }
            if(downloadManagerFragment != null){
                transaction.hide(downloadManagerFragment);
            }
            if (lxbFragment != null) {
                transaction.hide(lxbFragment);
            }
            if (gtbFragment != null) {
                transaction.hide(gtbFragment);
            }
            if (ndbFragment != null) {
                transaction.hide(ndbFragment);
            }
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        imageView1_album = (ImageView) findViewById(R.id.imageView1_album);
        textView1_song_name = (TextView) findViewById(R.id.textView1_song_name);
        textView2_singer = (TextView) findViewById(R.id.textView2_singer);
        imageButton1_play_list = (ImageButton) findViewById(R.id.imageButton1_play_list);
        imageButton1_play_pause = (ImageButton) findViewById(R.id.imageButton1_play_pause);
        imageButton2_next = (ImageButton) findViewById(R.id.imageButton2_next);
        inc_bar = (LinearLayout) findViewById(R.id.inc_bar);
        imageView1_album.setOnClickListener(this);
        imageButton1_play_list.setOnClickListener(this);
        imageButton1_play_pause.setOnClickListener(this);
        imageButton2_next.setOnClickListener(this);
        fm = getSupportFragmentManager();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showIndexFragment(new MessageEvent(MessageEventType.SHOW_INDEX_FRAGMENT));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && fm.getBackStackEntryCount() == 1) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView1_album:
                startPlayMusicUI();
                break;
            case R.id.imageButton1_play_list:
                imageButton1_play_list();
                break;
            case R.id.imageButton1_play_pause:
                imageButton1_play_pause();
                break;
            case R.id.imageButton2_next:
                imageButton2_next();
                break;
        }
    }

    //打开播放界面
    private void startPlayMusicUI() {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    private void queryPlayRecordCount() {
        try {
            long count = app.dbManager.selector(Mp3Info.class).where("playTime", "<>", "0").count();
            MessageEvent event = new MessageEvent(MessageEventType.UPDATE_PLAY_RECORD_COUNT);
            event.data = count;
            EventBus.getDefault().post(event);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private void queryMyLikeMusicCount() {
        try {
            long count = app.dbManager.selector(Mp3Info.class).where("isLike", "=", "1").count();
            MessageEvent event = new MessageEvent(MessageEventType.UPDATE_MY_LIKE_MUSIC_COUNT);
            event.data = count;
            EventBus.getDefault().post(event);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updateMyLikeMusicCount(MessageEvent event){
        if (event.type == MessageEventType.QUERY_MY_LIKE_MUSIC_COUNT) {
            queryMyLikeMusicCount();
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    @Override
    public void loadLatelyPlayData() {
        try {
            List<Mp3Info> list = app.dbManager.selector(Mp3Info.class)
                    .where("playTime", "<>", "0")
                    .orderBy("playTime", true).findAll();
            if (list == null || list.size() == 0) {
                return;
            }
            MessageEvent event = new MessageEvent(MessageEventType.LOAD_LATELY_PLAY_LIST);
            event.data = list;
            Log.i(TAG, "loadLatelyPlayData: " + list);
            EventBus.getDefault().postSticky(event);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playLately(ArrayList<Mp3Info> mp3Infos, int position) {
        playService.setType(PlayService.TYPE_LOCAL);
        playService.setMp3Infos(mp3Infos);
        playService.play(position);
    }

    @Override
    public void play(ArrayList<Mp3Info> list, int position) {
        playService.setType(PlayService.TYPE_LOCAL);
        playService.setMp3Infos(list);
        playService.play(position);
        savePlayRecord(position);


    }

    //保存播放记录
    private void savePlayRecord(int position) {
        //保存用户自主点击的播放记录
        Mp3Info mp3Info = playService.getMp3Infos().get(position);
        try {
            Mp3Info playRecord = app.dbManager.selector(Mp3Info.class)
                    .where("mp3InfoId", "=", mp3Info.getMp3InfoId())
                    .findFirst();
            Log.i(TAG, "play: playRecord--" + playRecord);
            if (playRecord == null) {
                mp3Info.setPlayTime(System.currentTimeMillis());
                Log.i(TAG, "play: mp3Info--" + mp3Info);
                app.dbManager.save(mp3Info);
            } else {
                playRecord.setPlayTime(System.currentTimeMillis());
                app.dbManager.update(playRecord, "playTime");
            }
            queryPlayRecordCount();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playNet(ArrayList<NetMusic> netMusics, int position) {
        playService.setType(PlayService.TYPE_NET);
        playService.setNetMusics(netMusics);
        playService.play(position);
    }

    @Override
    public void updateData() {
        queryPlayRecordCount();
        queryMyLikeMusicCount();
    }

    @Override
    public void loadMyLikeMusicData() {
        try {
            List<Mp3Info> list = app.dbManager.selector(Mp3Info.class)
                    .where("isLike", "=", "1")
                    .orderBy("playTime", true)
                    .findAll();
            if (list == null || list.size() == 0) {
                return;
            }
            MessageEvent event = new MessageEvent(MessageEventType.LOAD_MY_LIKE_MUSIC_LIST);
            event.data = list;
            EventBus.getDefault().postSticky(event);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //收藏歌曲回调接口方法
    @Override
    public void playMyLikeMusic(ArrayList<Mp3Info> mp3Infos, int position) {
        playService.setType(PlayService.TYPE_LOCAL);
        playService.setMp3Infos(mp3Infos);
        playService.play(position);
    }

    //下载管理回调接口方法
    @Override
    public void loadDownloadManagerData() {

    }

}
