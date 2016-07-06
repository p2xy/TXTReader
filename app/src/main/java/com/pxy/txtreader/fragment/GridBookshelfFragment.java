package com.pxy.txtreader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.pxy.txtreader.BaseApplication;
import com.pxy.txtreader.R;
import com.pxy.txtreader.adapter.BookGridViewAdapter;

/**
 * Created by pxy on 2016/4/3.
 */
public class GridBookshelfFragment extends BaseBookshelfFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        books = ((BaseApplication) getActivity().getApplication()).getBooks();
        adapter = new BookGridViewAdapter(getActivity(), books);
        View view = inflater.inflate(R.layout.fragment_grid_bookshelf,container,false);
        bookshelf = (GridView)view.findViewById(R.id.gv_bookshelf);
        init(view);
        return view;
    }
}
