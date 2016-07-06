package com.pxy.txtreader.io;

import android.content.Context;

import com.pxy.txtreader.bean.Book;
import com.pxy.txtreader.bean.Outline;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pxy on 2016/3/29.
 */
public class TextManage {
    private static final int TEXT_BLOCK_SIZE = 3000;
    private static String charset = "gbk";

    public static boolean parseOutline(Book book, Context context) {
        List<Outline> list = new ArrayList<>();
        try {
            String path = book.getPath();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
            IOManager manager = new IOManager(context);
            String line = "";
            long index = 0;
            int count = 0;
            char ch;
            int n;
            boolean flag = false;//是否已发现有章节目录
            while ((n = br.read()) != -1) {
                ch = (char) n;
                index++;
                if (flag) count++;
                if (ch == '\n' || ch == '\r') {//遇到换行符
                    if (line.length() > 0) {//当前行不是空白
                        String[] strs = line.split(" ");//获取以空格分隔的文字块
                        long temp = index - line.length() - 1;
                        if ((strs[0].startsWith("第") && strs[0].endsWith("章")) || (strs[0].startsWith("第") && strs[0].endsWith("集"))) {
                            //发现有章节名
                            flag = true;
                            if (temp > 0 && list.size() == 0) {
                                list.add(new Outline(path, "开始", 0));
                            }
                            Outline o = list.get(list.size() - 1);
                            o.setEnd(temp);//更新上一章节结束位置
                            manager.savaOutline(o);//保存到数据库
                            System.out.println(o);
                            list.add(new Outline(path, line, index));
                        } else if ((!flag) && count > TEXT_BLOCK_SIZE) {
                            //没有发现章节时，每TEXT_BLOCK_SIZE当作字符一个章节
                            if (temp > 0 && list.size() == 0) {
                                list.add(new Outline(path, "开始", 0));
                            }
                            Outline o = list.get(list.size() - 1);
                            o.setEnd(temp);//更新上一章节结束位置
                            manager.savaOutline(o);//保存到数据库
                            System.out.println(o);
                            list.add(new Outline(path, "目录(" + list.size() + ")", index));
                        }
                    }
                    line = "";
                } else {//不是换行符
                    line += ch;
                }
            }
            if (list.size() > 0) {
                Outline o = list.get(list.size() - 1);
                o.setEnd(index);//更新最后一章节结束位置
                manager.savaOutline(o);//保存到数据库
                book.setTotal(index);
                book.setInit(1);
                book.setRead(0);
                book.setLastreadtime(System.currentTimeMillis());
                manager.updateBook(book);
            } else if (index > 0) {
                Outline o = new Outline(path, "开始", 0, index);
                manager.savaOutline(o);
                book.setTotal(index);
                book.setInit(1);
                book.setRead(0);
                book.setLastreadtime(System.currentTimeMillis());
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size() == 0) return false;
        else return true;
    }

    private String readOutlineText(Outline outline) {
        String re = null;
        int length = (int) (outline.getEnd() - outline.getBegin());
        char[] chars = new char[length];
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(outline.getPath()), charset));
            br.skip(outline.getBegin());
            br.read(chars, 0, length);
            re = new String(chars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }
}
