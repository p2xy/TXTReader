package com.pxy.txtreader.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pxy.txtreader.BaseApplication;
import com.pxy.txtreader.R;
import com.pxy.txtreader.adapter.FileListViewAdapter;
import com.pxy.txtreader.bean.Book;
import com.pxy.txtreader.bean.FileBean;
import com.pxy.txtreader.bean.FileBeanComparator;
import com.pxy.txtreader.io.IOManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectBookActivity extends AppCompatActivity {

    private static final String BASE_PATH = "/storage";
    private ListView lvFiles;
    private String currentPath;
    private TextView currentPathShow;
    private TextView backToPreviousPath;
    private List<FileBean> files;
    private FileListViewAdapter adapter;
    private List<Book> books;

    /**
     * 初始化对象
     */
    private void initData() {
        currentPath = BASE_PATH;
        files = new ArrayList<>();
        lvFiles = (ListView) findViewById(R.id.lv_files);
        currentPathShow = (TextView) findViewById(R.id.tv_path_show);
        backToPreviousPath = (TextView) findViewById(R.id.go_back_tv);
        adapter = new FileListViewAdapter(this, files);
        currentPathShow.setText(currentPath + "/");
        books = ((BaseApplication) getApplication()).getBooks();
        findFileList();
        lvFiles.setAdapter(adapter);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileBean file = files.get(position);
                if (file.isFile()) {
                    if (!file.isSelect()) {
                        addBookToBookshelf(position);
                    }
                    intoReadPage(currentPath + "/" + file.getName());
                } else {
                    //是文件夹，则打开
                    currentPath += "/" + files.get(position).getName();
                    findFileList();
                    lvFiles.setSelection(0);
                    adapter.notifyDataSetChanged();
                    currentPathShow.setText(currentPath + "/");
                }
            }
        });
        backToPreviousPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPath.equals(BASE_PATH)) {
                    return;
                } else {
                    while ((currentPath.charAt(currentPath.length() - 1)) != '/') {
                        currentPath = currentPath.substring(0, currentPath.length() - 1);
                    }
                    currentPath = currentPath.substring(0, currentPath.length() - 1);
                    findFileList();
                    lvFiles.setSelection(0);
                    adapter.notifyDataSetChanged();
                    currentPathShow.setText(currentPath + "/");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_book);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initData();
        initEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    /**
     * 选定书籍添加到书架
     *
     * @param position
     */
    private void addBookToBookshelf(int position) {
        Book book = new Book();
        book.setName(files.get(position).getName());
        book.setPath(currentPath + "/" + files.get(position).getName());
        book.setInit(0);
        new IOManager(this).saveBook(book);
        books.add(book);
        files.get(position).setIsSelect(true);
    }

    /**
     * 跳转到阅读界面
     */
    private void intoReadPage(String path) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getPath().equals(path)) {
                Intent intent = new Intent(this, ReadActivity.class);
                intent.putExtra("currentBookIndex", i);
                startActivity(intent);
            }
        }
    }

    /**
     * 查找当前目录下的文件夹和txt文件
     */
    private void findFileList() {
        ProgressDialog dialog = ProgressDialog.show(SelectBookActivity.this, "", "", false, false);
        files.clear();
        File file = new File(currentPath);
        if (file.exists()) {
            String[] fileStrs = file.list();
            if (fileStrs != null) {
                for (String str : fileStrs) {
                    File f = new File(currentPath + "/" + str);
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            //是文件夹
                            int c = 0;
                            if (f.list() != null) {
                                c = f.list().length;
                            }
                            files.add(new FileBean(str, false, false, c));
                        } else {
                            if (str.endsWith(".txt")) {
                                //是txt文件
                                boolean isSelected = false;
                                for (Book book : books) {
                                    if (book.getPath().equals(currentPath + "/" + str)) {
                                        isSelected = true;
                                    }
                                }
                                files.add(new FileBean(str, true, isSelected, f.length()));
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(files, new FileBeanComparator());
        dialog.dismiss();
    }
}
