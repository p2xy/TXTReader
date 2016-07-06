package com.pxy.txtreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.pxy.txtreader.BaseApplication;
import com.pxy.txtreader.R;
import com.pxy.txtreader.bean.Book;
import com.pxy.txtreader.io.IOManager;

import java.util.List;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Thread() {
            @Override
            public void run() {
                List<Book> books = ((BaseApplication) getApplication()).getBooks();
                new IOManager(Welcome.this).getAllBook(books);
                startActivity(new Intent(Welcome.this, BookshelfActivity.class));
                finish();
            }
        }.start();
    }
}
