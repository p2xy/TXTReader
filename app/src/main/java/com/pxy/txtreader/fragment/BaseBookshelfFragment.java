package com.pxy.txtreader.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.pxy.txtreader.activity.ReadActivity;
import com.pxy.txtreader.adapter.BaseBookAdapter;
import com.pxy.txtreader.bean.Book;
import com.pxy.txtreader.io.IOManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pxy on 2016/4/3.
 */
public class BaseBookshelfFragment extends Fragment {
    protected List<Book> books;
    protected BaseBookAdapter adapter;
    protected AbsListView bookshelf;
    private boolean allSelected = false;

    @Override
    public void onResume() {
        super.onResume();
        invalidate();
    }

    /**
     * 是否被全选
     * @return
     */
    public boolean isAllSelected() {
        return allSelected;
    }

    /**
     * 选定或取消全选
     * @param allSelected
     */
    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
        for (Book book : books) {
            book.setDeleteSelected(allSelected);
        }
        invalidate();
    }

    /**
     * 初始化
     * @param view
     */
    protected void init(View view) {
        bookshelf.setAdapter(adapter);
        bookshelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.isDeleteMode()) {
                    Intent intent = new Intent(getActivity(), ReadActivity.class);
                    intent.putExtra("currentBookIndex", position);
                    startActivity(intent);
                } else {
                    boolean del = books.get(position).isDeleteSelected();
                    if (del) {
                        books.get(position).setDeleteSelected(false);
                        if (allSelected) {
                            allSelected = false;
                            getActivity().invalidateOptionsMenu();
                        }
                    } else {
                        books.get(position).setDeleteSelected(true);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        bookshelf.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setDeleteMode(true);
                return true;
            }
        });
    }

    /**
     * 删除书架上选中的书籍
     */
    public void deleteBook() {
        List<Book> del = new ArrayList<>();
        for (Book book : books) {
            if (book.isDeleteSelected()) {
                new IOManager(getActivity()).deleteBook(book.getPath());
                del.add(book);
            }
            book.setDeleteSelected(false);
        }
        for (Book book : del) {
            books.remove(book);
        }
        allSelected = false;
        adapter.setDeleteMode(false);
        invalidate();
    }

    /**
     * 更细视图
     */
    private void invalidate() {
        getActivity().invalidateOptionsMenu();
        adapter.notifyDataSetChanged();
    }


    /**
     * 改变删除模式
     * @param deleteMode
     */
    public void setDeleteMode(boolean deleteMode) {
        if (deleteMode) {
            adapter.setDeleteMode(true);
        } else {
            for (Book book : books) {
                book.setDeleteSelected(false);
            }
            adapter.setDeleteMode(false);
        }
        invalidate();
    }

    /**
     * 判断是否在删除模式
     * @return
     */
    public boolean isDeleteMode() {
        return adapter.isDeleteMode();
    }
}
