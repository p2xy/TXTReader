package com.pxy.txtreader.bean;

/**
 * Created by pxy on 2016/3/24.
 */
public class FileBean{
    private String name;
    private boolean isFile;
    private boolean isSelect;
    private long capacity;


    public FileBean(String name, boolean isFile, boolean isSelect) {
        this.name = name;
        this.isFile = isFile;
        this.isSelect = isSelect;
    }

    public FileBean(String name, boolean isFile, boolean isSelect, long capacity) {
        this.name = name;
        this.isFile = isFile;
        this.isSelect = isSelect;
        this.capacity = capacity;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(int count) {
        this.capacity = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
