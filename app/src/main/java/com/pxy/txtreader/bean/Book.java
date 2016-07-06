package com.pxy.txtreader.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by pxy on 2016/3/22.
 */
public class Book implements Serializable {
    private String path;//路径
    private String name;//名称
    private int init;//是否解析目录
    private long total;//总字符数
    private long read;//当前已读字符数
    private long lastreadtime;//最后阅读时间
    private boolean deleteSelected;//是否被选定为删除对象
    private Bitmap cover;//封面

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public boolean isDeleteSelected() {
        return deleteSelected;
    }

    public void setDeleteSelected(boolean deleteSelected) {
        this.deleteSelected = deleteSelected;
    }

    public int getInit() {
        return init;
    }

    public void setInit(int init) {
        this.init = init;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getRead() {
        return read;
    }

    public void setRead(long read) {
        this.read = read;
    }

    public long getLastreadtime() {
        return lastreadtime;
    }

    public void setLastreadtime(long lastreadtime) {
        this.lastreadtime = lastreadtime;
    }


}
