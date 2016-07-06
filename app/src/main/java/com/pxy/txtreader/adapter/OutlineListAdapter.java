package com.pxy.txtreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pxy.txtreader.R;
import com.pxy.txtreader.bean.Outline;

import java.util.List;

/**
 * Created by pxy on 2016/4/5.
 */
public class OutlineListAdapter extends BaseAdapter {
    private List<Outline> outlines;
    private Context context;
    private int currentOutlineIndex;

    public void setCurrentOutlineIndex(int currentOutlineIndex) {
        this.currentOutlineIndex = currentOutlineIndex;
    }

    public OutlineListAdapter(Context context, List<Outline> outlines, int currentOutlineIndex) {
        this.context = context;
        this.outlines = outlines;
        this.currentOutlineIndex = currentOutlineIndex;
    }

    @Override
    public int getCount() {
        return outlines.size();
    }

    @Override
    public Object getItem(int position) {
        return outlines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.outline_list_item, parent, false);
            holder = new ViewHolder();
            holder.chpater = (TextView) convertView.findViewById(R.id.tv_chapter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.chpater.setText("*  " + outlines.get(position).getName());
        holder.chpater.setTextColor(position == currentOutlineIndex ? Color.RED : Color.BLACK);
        return convertView;
    }

    private class ViewHolder {
        public TextView chpater;
    }
}
