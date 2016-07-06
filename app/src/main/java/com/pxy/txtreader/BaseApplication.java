package com.pxy.txtreader;

import android.app.Application;

import com.pxy.txtreader.bean.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pxy on 2016/3/28.
 */
public class BaseApplication extends Application {

    private List<Book> books = new ArrayList<>();

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
