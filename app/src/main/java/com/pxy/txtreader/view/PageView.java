package com.pxy.txtreader.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by pxy on 2016/3/30.
 */
public class PageView extends View {

    public static final int NONE = 0;//无
    public static final int HORIZONTAL_SLIP = 1;//水平滑动
    public static final int HORIZONTAL_COVER = 2;//水平覆盖
    private int pagingMode = NONE;//翻页模式，默认无
    //x轴偏移量
    private int currentOffsetX = 0;
    private int upOffsetX = 0;
    //页面bitmap
    private Bitmap upPageBitmap;
    private Bitmap currentPageBitmap;
    private Bitmap downPageBitmap;

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (pagingMode) {
            case NONE://无翻页效果
                if (currentPageBitmap != null) {
                    canvas.drawBitmap(currentPageBitmap, 0, 0, null);
                }
                break;
            case HORIZONTAL_SLIP://水平滑动
                if (currentPageBitmap != null) {
                    canvas.drawBitmap(currentPageBitmap, currentOffsetX, 0, null);
                }
                if (upPageBitmap != null) {
                    canvas.drawBitmap(upPageBitmap, currentOffsetX - getMeasuredWidth(), 0, null);
                }
                if (downPageBitmap != null) {
                    canvas.drawBitmap(downPageBitmap, currentOffsetX + getMeasuredWidth(), 0, null);
                }
                break;
            case HORIZONTAL_COVER://水平覆盖
                if (downPageBitmap != null) {
                    canvas.drawBitmap(downPageBitmap, 0, 0, null);
                }
                if (currentPageBitmap != null) {
                    canvas.drawBitmap(currentPageBitmap, currentOffsetX, 0, null);
                }
                if (upPageBitmap != null) {
                    canvas.drawBitmap(upPageBitmap, upOffsetX, 0, null);
                }
                break;
        }
    }

    public int getPagingMode() {
        return pagingMode;
    }

    public void setPagingMode(int pagingMode) {
        this.pagingMode = pagingMode;
    }

    public int getCurrentOffsetX() {
        return currentOffsetX;
    }

    public void setCurrentOffsetX(int currentOffsetX) {
        this.currentOffsetX = currentOffsetX;
    }

    public int getUpOffsetX() {
        return upOffsetX;
    }

    public void setUpOffsetX(int upOffsetX) {
        this.upOffsetX = upOffsetX;
    }

    public Bitmap getUpPageBitmap() {
        return upPageBitmap;
    }

    public void setUpPageBitmap(Bitmap upPageBitmap) {
        this.upPageBitmap = upPageBitmap;
    }

    public Bitmap getCurrentPageBitmap() {
        return currentPageBitmap;
    }

    public void setCurrentPageBitmap(Bitmap currentPageBitmap) {
        this.currentPageBitmap = currentPageBitmap;
    }

    public Bitmap getDownPageBitmap() {
        return downPageBitmap;
    }

    public void setDownPageBitmap(Bitmap downPageBitmap) {
        this.downPageBitmap = downPageBitmap;
    }
}
