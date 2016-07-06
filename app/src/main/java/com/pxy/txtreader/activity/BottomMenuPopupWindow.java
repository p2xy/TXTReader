package com.pxy.txtreader.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pxy.txtreader.R;

/**
 * Created by pxy on 2016/4/2.
 */
public class BottomMenuPopupWindow extends PopupWindow {
    private View mMenuView;
    private ImageView ivUp;
    private ImageView ivDown;
    private SeekBar sbProgress;
    private TextView tvOutline;
    private TextView tvMark;
    private TextView tvSetting;
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    interface CallBack {
        void outlineClick(View view);
        void upChapter(View view);
        void downChapter(View view);
        void seekBarChanged(SeekBar seekBar, int progress, boolean fromUser);
        void markClick(View view);
        void onSetting(View view);
    }
    public void setSeekBarProgross(int progross){
        sbProgress.setProgress(progross);
    }
    public BottomMenuPopupWindow(Context context,int seekBarMax) {
        super(context);
        mMenuView = LayoutInflater.from(context).inflate(R.layout.bottom_menu, null);
        tvOutline = (TextView) mMenuView.findViewById(R.id.tv_outline);
        tvMark = (TextView) mMenuView.findViewById(R.id.tv_mark);
        tvSetting = (TextView) mMenuView.findViewById(R.id.tv_setting);
        ivUp = (ImageView) mMenuView.findViewById(R.id.iv_up);
        ivDown = (ImageView) mMenuView.findViewById(R.id.iv_down);
        sbProgress = (SeekBar) mMenuView.findViewById(R.id.sb_progress);
        sbProgress.setMax(seekBarMax);
        tvOutline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.outlineClick(v);
            }
        });
        tvMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.markClick(v);
            }
        });
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.onSetting(v);
            }
        });
        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.upChapter(v);
            }
        });
        ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.downChapter(v);
            }
        });
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (callBack != null)
                    callBack.seekBarChanged(seekBar,progress,fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        // this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        // ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        //this.setBackgroundDrawable(dw);
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
