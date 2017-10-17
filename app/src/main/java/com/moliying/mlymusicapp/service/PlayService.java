package com.moliying.mlymusicapp.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.moliying.mlymusicapp.MoliyingApp;
import com.moliying.mlymusicapp.utils.MediaUtils;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.vo.MessageEvent;
import com.moliying.mlymusicapp.vo.Mp3Info;
import com.moliying.mlymusicapp.vo.NetMusic;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 音乐播放的服务组件
 * 实现功能：
 * 1、播放
 * 2、暂停
 * 3、上一首
 * 4、下一首
 * 5、获取当前的播放进度
 */
public class PlayService extends Service
        implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener {

    public static final String ACTION_PLAY_PAUSE = "mly.music.action.PLAY_PAUSE"; //通知栏播放暂停ACTION
    public static final String ACTION_NEXT = "mly.music.action.NEXT"; //通知栏下一首
    public static final String ACTION_EXIT = "mly.music.action.EXIT"; //退出应用
    public static final int N_ID = 0x1; //通知的ID
    private static final String TAG = "PlayService";
    private MediaPlayer mPlayer;
    private int currentPosition;//当前正在播放的位置
    ArrayList<Mp3Info> mp3Infos;
    ArrayList<NetMusic> netMusics;



    //当前播放类型
    public static final int TYPE_LOCAL = 0x1;
    public static final int TYPE_NET = 0x2;
    private int type = TYPE_LOCAL;//默认播放本地

    private MusicUpdateListener musicUpdateListener;

    private ExecutorService es = Executors.newSingleThreadExecutor();

    private boolean isPause = false;

    //播放模式
    public static final int ORDER_PLAY = 1;
    public static final int RANDOM_PLAY = 2;
    public static final int SINGLE_PLAY = 3;
    private int play_mode = ORDER_PLAY;

    public ArrayList<NetMusic> getNetMusics() {
        return netMusics;
    }

    public void setNetMusics(ArrayList<NetMusic> netMusics) {
        this.netMusics = netMusics;
    }

    //当前播放类型(本地或网络)
    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * @param play_mode ORDER_PLAY = 1;
     *                  RANDOM_PLAY = 2;
     *                  SINGLE_PLAY = 3;
     */
    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public int getPlay_mode() {
        return play_mode;
    }

    public boolean isPause() {
        return isPause;
    }

    public PlayService() {}

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
        //play(0);
    }

    public ArrayList<Mp3Info> getMp3Infos() {
        return mp3Infos;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }


    private Random random = new Random();

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "onCompletion: "+currentPosition);
        //根据不同的播放模式
        switch (play_mode) {
            case ORDER_PLAY:
                next();
                break;
            case RANDOM_PLAY:
                switch (type){
                    case TYPE_LOCAL:
                        play(random.nextInt(mp3Infos.size()));
                        break;
                    case TYPE_NET:
                        play(random.nextInt(netMusics.size()));
                        break;
                }
                break;
            case SINGLE_PLAY:
                play(currentPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    //播放网络音乐时使用,缓冲完成后播放
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        es.execute(new UpdateStatusRunnable());
        if (musicUpdateListener != null) {
            musicUpdateListener.onChange(currentPosition);
        }
    }


    private PlayBinder playBinder;
    public class PlayBinder extends Binder{
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        playBinder = new PlayBinder();
        return playBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: PlayService unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MoliyingApp app = (MoliyingApp) getApplication();
        currentPosition = app.sp.getInt("currentPosition", 0);
        play_mode = app.sp.getInt("play_mode", PlayService.ORDER_PLAY);

        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnPreparedListener(this);
        mp3Infos = MediaUtils.getMp3Infos(this);
    }

    //释放资源
    @Override
    public void onDestroy() {
        super.onDestroy();
        saveStatus();
        isUpdateStatus = false;
        if (es != null && !es.isShutdown()) {
            es.shutdown();
            es = null;
        }
        mPlayer.setOnCompletionListener(null);
        mPlayer.setOnPreparedListener(null);
        mPlayer.setOnErrorListener(null);
        mPlayer.release();
        mPlayer = null;
        mp3Infos = null;
        netMusics = null;
        musicUpdateListener = null;
        playBinder = null;
    }



    /**
     * 退出时保存当前播放状态
     */
    private void saveStatus(){
        //保存当前播放的一些状态值
        MoliyingApp app = (MoliyingApp) getApplication();
        SharedPreferences.Editor editor = app.sp.edit();
        editor.putInt("currentPosition",currentPosition);
        editor.putInt("play_mode", play_mode);
        editor.commit();
    }

    private boolean isUpdateStatus = false; //更新线程标记
    //更新播放状态的线程任务
    private class UpdateStatusRunnable implements Runnable {
        public UpdateStatusRunnable() {
            isUpdateStatus = true;
        }
        @Override
        public void run() {
            while (isUpdateStatus) {
                if (musicUpdateListener != null) {
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    //播放
    public void play(int position) {
        Log.i(TAG, "play: "+position);
        isPause = false;
        switch (type){
            //播放本地音乐
            case TYPE_LOCAL:
                Mp3Info mp3Info = null;
                if(mp3Infos==null)return;
                if (position < 0 || position >= mp3Infos.size()) {
                    position = 0;
                }
                mp3Info = mp3Infos.get(position);
//                System.out.println(mp3Info);
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                    mPlayer.prepare();
                    mPlayer.start();
                    currentPosition = position;
                    es.execute(new UpdateStatusRunnable());
                    if (musicUpdateListener != null) {
                        musicUpdateListener.onChange(currentPosition);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            //播放网络音乐
            case TYPE_NET:
                if (netMusics == null) {
                    return;
                }
                NetMusic netMusic;
                if (position < 0 || position >= netMusics.size()) {
                    position = 0;
                }
                netMusic = netMusics.get(position);
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(this, Uri.parse(netMusic.music.get(2)));
                    mPlayer.prepareAsync();
                    currentPosition = position;
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
        }
        mPlayer.setOnCompletionListener(this);
    }

    //暂停
    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
            isUpdateStatus = false;
            mPlayer.setOnCompletionListener(null);
            if(musicUpdateListener!=null)musicUpdateListener.onChange(currentPosition);
        }
    }

    //下一首
    public void next() {
        switch (type) {
            //本地音乐
            case TYPE_LOCAL:
                if (currentPosition + 1 > mp3Infos.size() - 1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                break;
            //网络音乐
            case TYPE_NET:
                if (currentPosition + 1 > netMusics.size() - 1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                break;
        }
        play(currentPosition);
    }

    //上一首
    public void prev() {
        switch (type) {
            //本地音乐
            case TYPE_LOCAL:
                if (currentPosition - 1 < 0) {
                    currentPosition = mp3Infos.size() - 1;
                } else {
                    currentPosition--;
                }
                break;
            //网络音乐
            case TYPE_NET:
                if (currentPosition - 1 < 0) {
                    currentPosition = netMusics.size() - 1;
                } else {
                    currentPosition--;
                }
                break;
        }
        play(currentPosition);
    }

    //暂停后播放
    public void play() {

        if(isPause){
            mPlayer.start();
            isPause = false;
            mPlayer.setOnCompletionListener(this);
            es.execute(new UpdateStatusRunnable());
            if(musicUpdateListener!=null)musicUpdateListener.onChange(currentPosition);
        }else{
            play(currentPosition);
        }
    }

    //是否正在播放
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    //获取当前正在播放的进度值
    public long getCurrentProgress() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    //播放音乐的总长度
    public long getDuration() {
        return mPlayer.getDuration();
    }

    public void seekTo(int msec) {
        mPlayer.seekTo(msec);
    }

    //更新状态的接口
    public interface MusicUpdateListener {
        public void onPublish(long progress); //发布实时播放进度
        public void onChange(long position);//更新当前播放音乐的状态
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            String action = intent.getAction();
            if (action != null) {
                if (ACTION_PLAY_PAUSE.equals(action)) {
                    if (isPlaying()) {
                        pause();
                    } else {
                        play();
                    }
                } else if (ACTION_NEXT.equals(action)) {
                    next();
                } else if (ACTION_EXIT.equals(action)) {
                    EventBus.getDefault().post(new MessageEvent(MessageEventType.EXIT_APP));
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
                    stopSelf();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
