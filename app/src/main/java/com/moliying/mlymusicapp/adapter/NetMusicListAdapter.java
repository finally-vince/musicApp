package com.moliying.mlymusicapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.vo.NetMusic;

import java.util.ArrayList;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/21.
 */
public class NetMusicListAdapter extends BaseAdapter {
    private static final String TAG = "NetMusicListAdapter";
    public Context context;
    public ArrayList<NetMusic> netMusics;

    public void setNetMusics(ArrayList<NetMusic> netMusics) {
//        Log.i(TAG, "setNetMusics: --"+netMusics);
        this.netMusics = netMusics;
        notifyDataSetInvalidated();
    }

    public NetMusicListAdapter(Context context, ArrayList<NetMusic> netMusics) {
        this.context = context;
        this.netMusics = netMusics;
    }

    @Override
    public int getCount() {
        return netMusics.size();
    }

    @Override
    public Object getItem(int position) {
        return netMusics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
//        Log.i(TAG, "getView: --"+netMusics);
        ViewHolder vh;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.net_item_music_list,null);
            vh = new ViewHolder();
            vh.textView_title = (TextView) convertView.findViewById(R.id.textView1_title);
            vh.textView2_singer = (TextView) convertView.findViewById(R.id.textView2_singer);
            vh.textView3_url = (TextView) convertView.findViewById(R.id.textView3_url);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        NetMusic netMusic = netMusics.get(position);

        vh.textView3_url.setText(netMusic.music.get(2));
        vh.textView_title.setText(netMusic.music.get(3));
        vh.textView2_singer.setText(netMusic.music.get(5));
        return convertView;
    }
    public static class ViewHolder{
        TextView textView_title;
        TextView textView2_singer;
        TextView textView3_url;
    }
}
