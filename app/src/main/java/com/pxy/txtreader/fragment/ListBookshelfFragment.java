package com.pxy.txtreader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pxy.txtreader.BaseApplication;
import com.pxy.txtreader.R;
import com.pxy.txtreader.adapter.BookListViewAdapter;

/**
 * Created by pxy on 2016/4/3.
 */
public class ListBookshelfFragment extends BaseBookshelfFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        books = ((BaseApplication) getActivity().getApplication()).getBooks();
        adapter = new BookListViewAdapter(getActivity(), books);
        View view = inflater.inflate(R.layout.fragment_list_bookshelf,container,false);
        bookshelf = (ListView) view.findViewById(R.id.lv_bookshelf);
        init(view);
        return view;
    }
}
