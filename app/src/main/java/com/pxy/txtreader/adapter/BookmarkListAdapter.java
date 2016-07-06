package com.pxy.txtreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pxy.txtreader.R;
import com.pxy.txtreader.bean.Mark;

import java.util.List;

/**
 * Created by pxy on 2016/4/15.
 */
public class BookmarkListAdapter extends BaseAdapter {
    private List<Mark> marks;
    private Context mContext;

    public BookmarkListAdapter(Context mContext, List<Mark> marks) {
        this.mContext = mContext;
        this.marks = marks;
    }

    @Override
    public int getCount() {
        return marks.size();
    }

    @Override
    public Object getItem(int position) {
        return marks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bookmark_list_item,parent,false);
            holder = new ViewHolder();
            holder.bookmark = (TextView) convertView.findViewById(R.id.bookmark_name_tv);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete_bookmark_iv);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bookmark.setText(marks.get(position).getName());
        return convertView;
    }

    private class ViewHolder{
        public TextView bookmark;
        public ImageView delete;
    }
}
