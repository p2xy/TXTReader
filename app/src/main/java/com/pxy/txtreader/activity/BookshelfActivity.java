package com.pxy.txtreader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.pxy.txtreader.R;
import com.pxy.txtreader.fragment.BaseBookshelfFragment;
import com.pxy.txtreader.fragment.GridBookshelfFragment;
import com.pxy.txtreader.fragment.ListBookshelfFragment;

public class BookshelfActivity extends AppCompatActivity {

    private static final int GRID_SHOW = 1;
    private static final int LIST_SHOW = 2;
    private int showMode = GRID_SHOW;
    private BaseBookshelfFragment fragment;
    private boolean allSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragment = new GridBookshelfFragment();
        getFragmentManager().beginTransaction().add(R.id.bookshelf_fragment_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookshelf_menu, menu);
        menu.findItem(R.id.del_book).setVisible(false);
        menu.findItem(R.id.done_all).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.book_show_mode:
                if (showMode == GRID_SHOW) {
                    showMode = LIST_SHOW;
                    item.setIcon(R.drawable.ic_view_list_24dp);
                    fragment = new ListBookshelfFragment();
                    getFragmentManager().beginTransaction().replace(R.id.bookshelf_fragment_container, fragment).commit();
                } else if (showMode == LIST_SHOW) {
                    showMode = GRID_SHOW;
                    item.setIcon(R.drawable.ic_view_grid_24dp);
                    fragment = new GridBookshelfFragment();
                    getFragmentManager().beginTransaction().replace(R.id.bookshelf_fragment_container, fragment).commit();
                }
                break;
            case R.id.add_book:
                Intent intent = new Intent(this, SelectBookActivity.class);
                startActivity(intent);
                break;
            case R.id.del_book:
                new AlertDialog.Builder(this).setMessage("你确定要移除所选图书吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fragment.deleteBook();
                    }
                }).setNegativeButton("取消", null).create().show();
                break;
            case R.id.done_all:
                if(!fragment.isAllSelected()){
                    fragment.setAllSelected(true);
                }else{
                    fragment.setAllSelected(false);
                }
                break;
            case android.R.id.home:
                fragment.setDeleteMode(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (fragment.isDeleteMode()) {
            menu.findItem(R.id.add_book).setVisible(false);
            menu.findItem(R.id.book_show_mode).setVisible(false);
            menu.findItem(R.id.del_book).setVisible(true);
            menu.findItem(R.id.done_all).setVisible(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            if(fragment.isAllSelected()){
                menu.findItem(R.id.done_all).setIcon(R.drawable.ic_clear_24dp);
            }else{
                menu.findItem(R.id.done_all).setIcon(R.drawable.ic_done_all_24dp);
            }
        } else {
            menu.findItem(R.id.del_book).setVisible(false);
            menu.findItem(R.id.done_all).setVisible(false);
            menu.findItem(R.id.add_book).setVisible(true);
            menu.findItem(R.id.book_show_mode).setVisible(true);
            if(showMode == GRID_SHOW){
                menu.findItem(R.id.book_show_mode).setIcon(R.drawable.ic_view_grid_24dp);
            }else{
                menu.findItem(R.id.book_show_mode).setIcon(R.drawable.ic_view_list_24dp);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragment.isDeleteMode() && fragment != null) {
                fragment.setDeleteMode(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
