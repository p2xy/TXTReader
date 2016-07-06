package com.pxy.txtreader.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pxy.txtreader.bean.Book;
import com.pxy.txtreader.bean.Mark;
import com.pxy.txtreader.bean.Outline;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理工具类
 * Created by pxy on 2016/3/22.
 */
public class IOManager {

    private DBHelper helper;

    public IOManager(Context context) {
        this.helper = new DBHelper(context);
    }

    public void updateBook(Book book) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("init", book.getInit());
        values.put("read", book.getRead());
        values.put("lastreadtime", book.getLastreadtime());
        values.put("total", book.getTotal());
        db.update("book", values, "path = ?", new String[]{book.getPath()});
        db.close();
    }

    /**
     * 插入书籍到数据库
     *
     * @param book
     */
    public void saveBook(Book book) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", book.getPath());
        values.put("name", book.getName());
        values.put("init", book.getInit());
        values.put("total", book.getTotal());
        values.put("read", book.getRead());
        values.put("lastreadtime", book.getLastreadtime());
        db.insert("book", null, values);
        db.close();
    }

    /**
     * 获取全部书籍数据的集合
     *
     * @return
     */
    public void getAllBook(List<Book> books) {
        books.clear();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("book", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setPath(cursor.getString(cursor.getColumnIndex("path")));
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            book.setInit(cursor.getInt(cursor.getColumnIndex("init")));
            book.setTotal(cursor.getLong(cursor.getColumnIndex("total")));
            book.setRead(cursor.getLong(cursor.getColumnIndex("read")));
            book.setLastreadtime(cursor.getLong(cursor.getColumnIndex("lastreadtime")));
            books.add(book);
        }
        cursor.close();
        db.close();
    }

    /**
     * 保存目录信息到数据库
     *
     * @param outlines
     */
    public void saveOutlines(List<Outline> outlines) {
        outlines.clear();
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (Outline outline : outlines) {
            values.clear();
            values.put("path", outline.getPath());
            values.put("name", outline.getName());
            values.put("begin", outline.getBegin());
            values.put("end", outline.getEnd());
            db.insert("outline", null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * 出入一条目录进数据库
     *
     * @param outline
     */
    public void savaOutline(Outline outline) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", outline.getPath());
        values.put("name", outline.getName());
        values.put("begin", outline.getBegin());
        values.put("end", outline.getEnd());
        db.insert("outline", null, values);
        db.close();
    }


    /**
     * 获取指定书籍的全部目录信息
     *
     * @param path 书籍路径
     * @return
     */
    public void getAllOutline(String path, List<Outline> outlines) {
        outlines.clear();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("outline", null, "path = ?", new String[]{path}, null, null, null);
        while (cursor.moveToNext()) {
            Outline outline = new Outline();
            outline.setId(cursor.getInt(cursor.getColumnIndex("id")));
            outline.setPath(cursor.getString(cursor.getColumnIndex("path")));
            outline.setName(cursor.getString(cursor.getColumnIndex("name")));
            outline.setBegin(cursor.getLong(cursor.getColumnIndex("begin")));
            outline.setEnd(cursor.getLong(cursor.getColumnIndex("end")));
            outlines.add(outline);
        }
        db.close();
    }

    /**
     * 保存书签
     *
     * @param mark
     */
    public void saveMark(Mark mark) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", mark.getPath());
        values.put("name", mark.getName());
        values.put("read", mark.getRead());
        values.put("createtime", mark.getCreatetime());
        db.insert("mark", null, values);
        db.close();
    }

    /**
     * 获取指定书籍的全部书签
     *
     * @param path 书籍id
     * @return
     */
    public void getAllMark(String path, List<Mark> marks) {
        marks.clear();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("mark", null, "path = ?", new String[]{path}, null, null, null);
        while (cursor.moveToNext()) {
            Mark mark = new Mark();
            mark.setId(cursor.getInt(cursor.getColumnIndex("id")));
            mark.setPath(cursor.getString(cursor.getColumnIndex("path")));
            mark.setName(cursor.getString(cursor.getColumnIndex("name")));
            mark.setRead(cursor.getLong(cursor.getColumnIndex("read")));
            mark.setCreatetime(cursor.getLong(cursor.getColumnIndex("createtime")));
            marks.add(mark);
        }
        db.close();
    }

    /**
     * 删除指定书签
     *
     * @param markid 书签id
     */
    public void deleteMark(int markid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("mark", "id = ?", new String[]{markid + ""});
        db.close();
    }

    /**
     * 删除指定书籍
     *
     * @param path
     */
    public void deleteBook(String path) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("book", "path = ?", new String[]{path});//删除该书book表中信息
        db.delete("outline", "path = ?", new String[]{path});//删除该书所有的目录
        db.delete("mark", "path = ?", new String[]{path});//删除该书所有的书签
        db.close();
    }

    /**
     * 通过书籍路径查找数据里的书籍数据
     *
     * @param path 路径
     * @return
     */
    public Book findBook(String path) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("book", null, "path = ?", new String[]{path}, null, null, null);
        if (cursor.moveToNext()) {
            Book book = new Book();
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            book.setPath(cursor.getString(cursor.getColumnIndex("path")));
            book.setInit(cursor.getInt(cursor.getColumnIndex("init")));
            book.setTotal(cursor.getLong(cursor.getColumnIndex("total")));
            book.setRead(cursor.getLong(cursor.getColumnIndex("read")));
            book.setLastreadtime(cursor.getLong(cursor.getColumnIndex("lastreadtime")));
            db.close();
            return book;
        }
        db.close();
        return null;
    }

    public static void parseOutline(int path) {
        List<Outline> outLines = new ArrayList<>();

    }
}
