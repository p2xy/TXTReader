package com.pxy.txtreader.bean;

import java.util.Comparator;

/**
 * Created by pxy on 2016/3/25.
 */
public class FileBeanComparator implements Comparator<FileBean> {
    @Override
    public int compare(FileBean lhs, FileBean rhs) {
        if(lhs.isFile() == rhs.isFile()){
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }else{
            return lhs.isFile() ? 1 : -1;
        }
    }
}
