package com.pxy.txtreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pxy.txtreader.R;
import com.pxy.txtreader.bean.FileBean;
import com.pxy.txtreader.common.Util;

import java.util.List;

/**
 * Created by pxy on 2016/3/24.
 */
public class FileListViewAdapter extends BaseAdapter {
    private List<FileBean> files;
    private Context context;

    public FileListViewAdapter(Context context, List<FileBean> files) {
        this.files = files;
        this.context = context;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_file, parent, false);
            holder = new ViewHolder();
            holder.fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_select);
            holder.ico = (ImageView) convertView.findViewById(R.id.iv_ico);
            holder.selected = (TextView) convertView.findViewById(R.id.tv_select);
            holder.count = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fileName.setText(files.get(position).getName());
        if (files.get(position).isFile()) {
            holder.ico.setImageResource(R.drawable.file);
            if (files.get(position).isSelect()) {
                holder.selected.setVisibility(View.VISIBLE);
                holder.selected.setText("已导入");
                holder.selected.setTextColor(Color.BLUE);
            } else {
                holder.selected.setVisibility(View.GONE);
                holder.selected.setText("");
            }
            holder.count.setText(Util.capacitySizeToString(files.get(position).getCapacity()));
        } else {
            holder.ico.setImageResource(R.drawable.ic_folder_24dp);
            holder.count.setText(files.get(position).getCapacity() + "项");
            holder.selected.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView fileName;
        public TextView textView;
        public ImageView ico;
        public TextView selected;
        public TextView count;
    }
}
