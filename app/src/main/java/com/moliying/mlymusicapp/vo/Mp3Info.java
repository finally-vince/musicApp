package com.moliying.mlymusicapp.vo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * descreption: 音乐对象
 * company: moliying.com
 * Created by vince on 16/6/20.
 */
@Table(name="Mp3Info")
public class Mp3Info {

    @Column(name="id",isId = true)
	private long id;
    @Column(name = "mp3InfoId")
    private long mp3InfoId;//在收藏音乐时用于保存原始ID
    @Column(name = "playTime")
    private long playTime;//最近播放时间
    @Column(name = "isLike")
    private int isLike; //1 喜欢  0 默认
    @Column(name="title")
	private String title;//歌名
    @Column(name="artist" )
	private String artist;//艺术家
    @Column(name="album")
	private String album;//专辑
    @Column(name="albumId")
	private long albumId;//
    @Column(name="duration")
	private long duration;//时长
    @Column(name="size")
	private long size;//大小
    @Column(name="url")
	private String url;//路径
    @Column(name="isMusic")
	private int isMusic;//是否为音乐


    public long getMp3InfoId() {
        return mp3InfoId;
    }

    public void setMp3InfoId(long mp3InfoId) {
        this.mp3InfoId = mp3InfoId;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public long getAlbumId() {
		return albumId;
	}
	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getIsMusic() {
		return isMusic;
	}
	public void setIsMusic(int isMusic) {
		this.isMusic = isMusic;
	}

    @Override
    public String toString() {
        return "Mp3Info{" +
                "album='" + album + '\'' +
                ", id=" + id +
                ", mp3InfoId=" + mp3InfoId +
                ", playTime=" + playTime +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", isMusic=" + isMusic +
                '}';
    }
}
