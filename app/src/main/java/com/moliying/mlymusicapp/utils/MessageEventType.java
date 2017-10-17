package com.moliying.mlymusicapp.utils;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/12.
 */
public enum MessageEventType {
    SHOW_MY_MUSIC_LIST_FRAGMENT, //显示我的音乐列表
    SHOW_INDEX_FRAGMENT,        //显示主页
    SHOW_NET_NDB_FRAGMENT,      //显示内地榜界面
    SHOW_NET_GTB_FRAGMENT,      //显示港台榜界面
    SHOW_NET_LXB_FRAGMENT,      //显示流行榜界面
    SHOW_LATELY_PLAY_LIST_FRAGMENT, //显示最近播放列表
    SHOW_MY_LIKE_MUSIC_LIST_FRAGMENT,   //显示我喜欢的歌曲列表
    UPDATE_PLAY_RECORD_COUNT,       //更新播放记录总数
    UPDATE_MY_LIKE_MUSIC_COUNT,     //更新我喜欢的歌曲总数
    LOAD_LATELY_PLAY_LIST,          //加载最近播放列表
    LOAD_MY_LIKE_MUSIC_LIST,        //加载我喜欢的歌曲列表
    EXIT_APP,                       //退出APP
    UPDATE_LRC,                     //更新歌词
    UPDATE_ALBUM_IMAGE,             //更新播放界面的专辑图片
    QUERY_MY_LIKE_MUSIC_COUNT,      //查询我的
    UPDATE_LOCAL_MUSIC_COUNT,       //更新本地音乐总数
    DOWNLOAD_MUSIC_COUNT,           //下载音乐总数
    SHOW_DOWNLOAD_MANAGER_FRAGMENT;
}
