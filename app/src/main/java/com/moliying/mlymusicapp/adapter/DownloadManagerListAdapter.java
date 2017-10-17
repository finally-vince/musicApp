package com.moliying.mlymusicapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.adapter.base.ViewHolder;

import java.io.File;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/27.
 */
public class DownloadManagerListAdapter extends BaseAdapter {
    private Context context;
    private File[] files;

    public DownloadManagerListAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        if(files!=null)return files.length;
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return files[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.download_manager_list_item,null);
        }
        TextView textView1_file_name = ViewHolder.getView(view,R.id.textView1_file_name,null);
        textView1_file_name.setText(files[position].getName());
        return view;
    }
}
