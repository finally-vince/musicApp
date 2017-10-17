package com.moliying.mlymusicapp.ui;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.adapter.CommonFragmentAdapter;
import com.moliying.mlymusicapp.fragment.AlbumImageFragment;
import com.moliying.mlymusicapp.fragment.LrcFragment;
import com.moliying.mlymusicapp.service.PlayService;
import com.moliying.mlymusicapp.utils.AppUtils;
import com.moliying.mlymusicapp.utils.DownloadUtils;
import com.moliying.mlymusicapp.utils.MediaUtils;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.utils.MlyException;
import com.moliying.mlymusicapp.vo.MessageEvent;
import com.moliying.mlymusicapp.vo.Mp3Info;
import com.moliying.mlymusicapp.vo.NetMusic;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.ex.DbException;

import java.lang.ref.WeakReference;


/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/7.
 */
public class PlayActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int UPDATE_PLAY_TIME = 0x100;
    private static final int UPDATE_LRC_TIME = 0x200;
    private static final String TAG = "PlayActivity";
    private TabLayout play_tab;
    private TextView textView1_title;
    private TextView textView1_songer;
    //    private ImageView imageView1_album;
    private ImageView imageView_fav;
    private ImageView imageView2_back;
    private ImageView imageView_download;
    private SeekBar seekBar1;
    private TextView textView1_start_time;
    private TextView textView1_end_time;
    private ImageView imageView_play_mode;
    private ImageView imageView_previous;
    private ImageView imageView_play_pause;
    private ImageView imageView_next;
    private ImageView imageView_play_list;
    private MyHandler myHandler;
    private ViewPager viewPager_album_lrc;
    private AlbumImageFragment albumImageFragment;
    private LrcFragment lrcFragment;

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: unbindPlayService()");
        unbindPlayService();
        super.onPause();
    }

    private static class MyHandler extends Handler {
        private WeakReference<PlayActivity> ref;

        public MyHandler(PlayActivity activity) {
            ref = new WeakReference<PlayActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayActivity activity = ref.get();
            if (activity != null) {
                switch (msg.what) {
                    case UPDATE_PLAY_TIME:
                        //更新播放时间
                        activity.textView1_start_time.setText(MediaUtils.formatTime(activity.playService.getCurrentProgress()));
                        break;
                    case UPDATE_LRC_TIME:
                        //同步歌词
                        activity.lrcFragment.seekLrcToTime(Long.parseLong(msg.obj.toString()));
                        break;
                }
            }
        }
    }

    ;

    @Override
    public void publish(long progress) {
        Log.i(TAG, "publish: "+progress);
        if(progress>0) {
            seekBar1.setProgress((int) progress);
            myHandler.obtainMessage(UPDATE_LRC_TIME, progress).sendToTarget();
            myHandler.obtainMessage(UPDATE_PLAY_TIME, progress).sendToTarget();
        }

    }

    //当前播放的歌曲和作者
    String songName = "";
    String artist = "";
    long playPosition;

    @Override
    public void change(long position) {
        Log.i(TAG, "change: position=" + position);
        this.playPosition = position;
        switch (playService.getType()) {
            case PlayService.TYPE_LOCAL:
                Mp3Info mp3Info = playService.getMp3Infos().get((int) position);
                songName = mp3Info.getTitle();
                artist = mp3Info.getArtist();
                textView1_title.setText(songName);
                textView1_songer.setText(artist);
                imageView_download.setVisibility(View.GONE);
                imageView_fav.setVisibility(View.VISIBLE);
                updateFavStatus(mp3Info);//更新是否为喜欢歌曲的状态
//                imageView1_album.setImageBitmap(MediaUtils.getArtwork(this,mp3Info.getId(),mp3Info.getAlbumId(),true,false));
                albumImageFragment.setBitmap(MediaUtils.getArtwork(this, mp3Info.getMp3InfoId(), mp3Info.getAlbumId(), true, false));
                break;
            case PlayService.TYPE_NET:
                NetMusic netMusic = playService.getNetMusics().get((int) position);
                songName = netMusic.music.get(3);
                artist = netMusic.music.get(5);
                textView1_title.setText(songName);
                textView1_songer.setText(artist);
                imageView_download.setVisibility(View.VISIBLE);
                imageView_fav.setVisibility(View.GONE);
                albumImageFragment.setBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.default_album_playing));
                break;
        }
        updateCommonStatus();
        if (lrcFragment != null) {
            lrcFragment.setSongNameAndArtist(songName, artist);
            lrcFragment.load();
        }
    }

    //查询并设置是否为喜欢歌曲
    private void updateFavStatus(Mp3Info mp3Info) {
        try {
            Mp3Info likeMp3Info = app.dbManager.selector(Mp3Info.class)
                    .where("mp3InfoId","=",mp3Info.getMp3InfoId()).findFirst();
            if (likeMp3Info != null && likeMp3Info.getIsLike()==1) {
                imageView_fav.setImageResource(R.mipmap.bt_playpage_button_like_hl);
            }else{
                imageView_fav.setImageResource(R.mipmap.bt_playpage_button_like_normal);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }


    //更新共有的组件状态
    private void updateCommonStatus() {
        if (playService.isPlaying()) {
            imageView_play_pause.setImageResource(R.drawable.button_pause_bg);
        } else {
            imageView_play_pause.setImageResource(R.drawable.button_play_bg);
        }
        seekBar1.setMax((int) playService.getDuration());
        textView1_end_time.setText(MediaUtils.formatTime(playService.getDuration()));
        switch (playService.getPlay_mode()) {
            case PlayService.ORDER_PLAY:
                imageView_play_mode.setImageResource(R.drawable.button_playmode_order_bg);
                break;
            case PlayService.RANDOM_PLAY:
                imageView_play_mode.setImageResource(R.drawable.button_playmode_random_bg);
                break;
            case PlayService.SINGLE_PLAY:
                imageView_play_mode.setImageResource(R.drawable.button_playmode_loop_bg);
                break;
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_music_play);
        viewPager_album_lrc = ((ViewPager) findViewById(R.id.viewpager_album_lrc));
        textView1_title = (TextView) findViewById(R.id.textView1_title);
        textView1_songer = (TextView) findViewById(R.id.textView1_songer);
//        imageView1_album = (ImageView) findViewById(R.id.imageView1_album);
        imageView_fav = (ImageView) findViewById(R.id.imageView_fav);
        imageView2_back = (ImageView) findViewById(R.id.imageView2_back);
        imageView_download = (ImageView) findViewById(R.id.imageView_download);
        textView1_start_time = (TextView) findViewById(R.id.textView1_start_time);
        textView1_end_time = (TextView) findViewById(R.id.textView1_end_time);
        imageView_play_mode = (ImageView) findViewById(R.id.imageView_play_mode);
        imageView_previous = (ImageView) findViewById(R.id.imageView_previous);
        imageView_play_pause = (ImageView) findViewById(R.id.imageView_play_pause);
        imageView_next = (ImageView) findViewById(R.id.imageView_next);
        imageView_play_list = (ImageView) findViewById(R.id.imageView_play_list);
        imageView_fav.setOnClickListener(this);
        imageView2_back.setOnClickListener(this);
        imageView_download.setOnClickListener(this);
        imageView_play_mode.setOnClickListener(this);
        imageView_previous.setOnClickListener(this);
        imageView_play_pause.setOnClickListener(this);
        imageView_next.setOnClickListener(this);
        imageView_play_list.setOnClickListener(this);
        initSeekBar();
        initFragment();
        play_tab = (TabLayout) findViewById(R.id.play_tab);

        String[] titles = getResources().getStringArray(R.array.album_lrc_title);
        viewPager_album_lrc.setAdapter(
                new CommonFragmentAdapter(getSupportFragmentManager(),
                        titles, new Fragment[]{albumImageFragment, lrcFragment}));
        play_tab.setupWithViewPager(viewPager_album_lrc);
        Log.d(TAG, "initView: ");
        myHandler = new MyHandler(this);

    }

    private void initSeekBar(){
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar1.setProgress(0);
        seekBar1.setMax(0);
    }


    private void initFragment() {
        albumImageFragment = AlbumImageFragment.newInstance();
        lrcFragment = LrcFragment.newInstance();
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void next(MessageEvent event) {
//
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateAlbumImage(MessageEvent event){
        if (MessageEventType.UPDATE_ALBUM_IMAGE == event.type) {
           change(playPosition);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_fav:
                imageView_fav();
                break;
            case R.id.imageView_play_mode:
                imageView_play_mode();
                break;
            case R.id.imageView_previous:
                imageView_previous();
                break;
            case R.id.imageView_play_pause:
                imageView_play_pause();
                break;
            case R.id.imageView_next:
                imageView_next();
                break;
            case R.id.imageView_play_list:
                imageView_play_list();
                break;
            case R.id.imageView_download:
                imageView_download();
                break;
            case R.id.imageView2_back:
                imageView2_back();
                break;
        }
    }

    //关闭
    private void imageView2_back() {
        finish();
    }

    //下载
    private void imageView_download() {
        toast(getString(R.string.downloading));
        NetMusic netMusic = playService.getNetMusics().get((int) playPosition);
        DownloadUtils.getInstance()
                .downloadMusic(this,netMusic.music.get(2),songName)
                .setListener(new DownloadUtils.OnDownloadListener() {
                    @Override
                    public void onDownload(String result, MlyException e) {
                        if(e==null){
                            toast(getString(R.string.download_success));
                            AppUtils.scanFileAsync(PlayActivity.this, result);
                        }else{
                            toast(e.toString());
                        }
                    }
                });
    }

    //播放列表按钮
    private void imageView_play_list() {
    }

    //下一首
    private void imageView_next() {
        playService.next();
    }

    private void imageView_play_pause() {
        if (playService.isPlaying()) {
            playService.pause();
        } else {
            playService.play();
        }
    }

    private void imageView_previous() {
        playService.prev();
    }

    //播放模式切换
    private void imageView_play_mode() {
        int mode = playService.getPlay_mode();
        if (mode == PlayService.ORDER_PLAY) {
            playService.setPlay_mode(PlayService.RANDOM_PLAY);
            imageView_play_mode.setImageResource(R.drawable.button_playmode_random_bg);
        } else if (mode == PlayService.RANDOM_PLAY) {
            playService.setPlay_mode(PlayService.SINGLE_PLAY);
            imageView_play_mode.setImageResource(R.drawable.button_playmode_loop_bg);
        } else if (mode == PlayService.SINGLE_PLAY) {
            playService.setPlay_mode(PlayService.ORDER_PLAY);
            imageView_play_mode.setImageResource(R.drawable.button_playmode_order_bg);
        }
    }

    //我喜欢
    private void imageView_fav() {
        Mp3Info mp3Info = playService.getMp3Infos().get((int) playPosition);
        try {
            Mp3Info likeMp3Info = app.dbManager.selector(Mp3Info.class)
                    .where("mp3InfoId","=",mp3Info.getMp3InfoId()).findFirst();
            if(likeMp3Info!=null){
                int like = likeMp3Info.getIsLike()==0?1:0;
                likeMp3Info.setIsLike(like);
                app.dbManager.update(likeMp3Info);
                Log.i(TAG, "imageView_fav: update"+like);
            }else{
                mp3Info.setIsLike(1);
                app.dbManager.save(mp3Info);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().postSticky(new MessageEvent(
                MessageEventType.QUERY_MY_LIKE_MUSIC_COUNT));
        change(playPosition);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            playService.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //playService.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //playService.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
