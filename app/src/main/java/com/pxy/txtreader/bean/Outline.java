package com.pxy.txtreader.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pxy on 2016/3/22.
 */
public class Outline implements Parcelable{
    private int id;
    private String path;
    private String name;
    private long begin;
    private long end;

    protected Outline(Parcel in) {
        id = in.readInt();
        path = in.readString();
        name = in.readString();
        begin = in.readLong();
        end = in.readLong();
    }

    public static final Creator<Outline> CREATOR = new Creator<Outline>() {
        @Override
        public Outline createFromParcel(Parcel in) {
            return new Outline(in);
        }

        @Override
        public Outline[] newArray(int size) {
            return new Outline[size];
        }
    };

    @Override
    public String toString() {
        return "Outline{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                '}';
    }

    public Outline() {
    }

    public Outline(String path, String name, long begin) {
        this.path = path;
        this.name = name;
        this.begin = begin;
    }

    public Outline(String path, String name, long begin, long end) {
        this.path = path;
        this.name = name;
        this.begin = begin;
        this.end = end;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
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

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeLong(begin);
        dest.writeLong(end);
    }
}