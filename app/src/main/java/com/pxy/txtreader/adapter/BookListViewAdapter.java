package com.pxy.txtreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pxy.txtreader.R;
import com.pxy.txtreader.bean.Book;
import com.pxy.txtreader.common.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by pxy on 2016/3/22.
 */
public class BookListViewAdapter extends BaseBookAdapter {

    public BookListViewAdapter(Context mContext, List<Book> books) {
        this.books = books;
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_book, parent, false);
            holder = new ViewHolder();
            holder.selectDelete = (CheckBox) convertView.findViewById(R.id.cb_del);
            holder.bookName = (TextView) convertView.findViewById(R.id.tv_book_name1);
            holder.progress = (TextView) convertView.findViewById(R.id.tv_progress);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bookName.setText(books.get(position).getName().replace(".txt",""));
        holder.time.setText("最后一次阅读在" + new SimpleDateFormat("yyyy年MM月dd日hh时mm分").format(new Date(books.get(position).getLastreadtime())));
        holder.progress.setText("已读 " + Util.getPercentString(books.get(position).getTotal(), books.get(position).getRead()));
        holder.selectDelete.setVisibility(deleteMode ? View.VISIBLE : View.GONE);
        holder.selectDelete.setChecked(books.get(position).isDeleteSelected());
        return convertView;
    }

    private class ViewHolder {
        public CheckBox selectDelete;
        public TextView bookName;
        public TextView progress;
        public TextView time;
    }
}
