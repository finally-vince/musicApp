package com.moliying.mlymusicapp.adapter.base;

import android.util.SparseArray;
import android.view.View;

/**
 * descreption: 简化所有ViewHolder
 * company: moliying.com
 * Created by vince on 16/07/16.
 */
public class ViewHolder {

    public static <T extends View> T getView(View view, int id, View.OnClickListener listener){
//        ViewHolder viewHolder = (ViewHolder) view.getTag();
        //HashMap<Integer,object> map = new HashMap<>();
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if(viewHolder==null){
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }
        View v = viewHolder.get(id);
        if(v==null){
            v = view.findViewById(id);
            if(listener!=null) v.setOnClickListener(listener);
            viewHolder.put(id,v);
        }
        return (T) v;
    }


}
