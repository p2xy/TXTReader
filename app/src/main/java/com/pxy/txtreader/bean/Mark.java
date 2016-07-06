package com.pxy.txtreader.bean;

/**
 * Created by pxy on 2016/3/22.
 */
public class Mark {

    private int id;
    private String path;
    private String name;
    private long read;
    private long createtime;

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRead() {
        return read;
    }

    public void setRead(long read) {
        this.read = read;
    }
}
