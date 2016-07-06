package com.pxy.txtreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.pxy.txtreader.R;
import com.pxy.txtreader.bean.Book;

import java.util.List;

/**
 * Created by pxy on 2016/3/22.
 */
public class BookGridViewAdapter extends BaseBookAdapter {

    public BookGridViewAdapter(Context context, List<Book> books) {
        this.mContext = context;
        this.books = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item_book, parent, false);
            holder = new ViewHolder();
            holder.bookCover = (ImageView) convertView.findViewById(R.id.book_cover_iv);
            holder.bookName = (TextView) convertView.findViewById(R.id.book_name_tv);
            holder.selectDelete = (CheckBox) convertView.findViewById(R.id.delete_select_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bookName.setText(books.get(position).getName().replace(".txt", ""));
        holder.selectDelete.setVisibility(deleteMode ? View.VISIBLE : View.GONE);
        holder.selectDelete.setChecked(books.get(position).isDeleteSelected());
        return convertView;
    }

    private class ViewHolder {
        public ImageView bookCover;
        public TextView bookName;
        public CheckBox selectDelete;
    }
}
