package com.pxy.txtreader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pxy.txtreader.bean.Book;

import java.util.List;

/**
 * Created by pxy on 2016/4/3.
 */
public class BaseBookAdapter extends BaseAdapter {

    protected boolean deleteMode = false;
    protected Context mContext;
    protected List<Book> books;

    public void setDeleteMode(boolean mode) {
        this.deleteMode = mode;
    }

    public boolean isDeleteMode() {
        return deleteMode;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
