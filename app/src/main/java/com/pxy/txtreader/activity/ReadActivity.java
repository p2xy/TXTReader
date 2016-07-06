package com.pxy.txtreader.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pxy.txtreader.BaseApplication;
import com.pxy.txtreader.R;
import com.pxy.txtreader.adapter.BookmarkListAdapter;
import com.pxy.txtreader.adapter.OutlineListAdapter;
import com.pxy.txtreader.bean.Book;
import com.pxy.txtreader.bean.Mark;
import com.pxy.txtreader.bean.Outline;
import com.pxy.txtreader.common.Util;
import com.pxy.txtreader.io.IOManager;
import com.pxy.txtreader.io.TextManage;
import com.pxy.txtreader.receiver.BatteryReceiver;
import com.pxy.txtreader.view.PageView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity {

    private static final int FIND_OUTLINES = 1;
    private static final int PARSE_OUTLINES = 2;
    private String charset = "gbk";
    private List<Book> books;
    private List<Outline> outlines;
    private int currentBookIndex;
    private int currentOutlineIndex;
    private ProgressDialog dialog;
    private PageView pageView;
    private PageCache upPageCache;
    private PageCache currentPageCache;
    private PageCache downPageCache;
    private int startX;
    private int pageWidth;
    private int pageHeight;
    private int actionUpOrDowm = 0;
    private long lastReadPoint;
    private BatteryReceiver receiver;
    private PopupWindow menu;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIND_OUTLINES:
                    pageView.invalidate();
                    break;
                case PARSE_OUTLINES:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (outlines.size() == 0) {
                        Toast.makeText(ReadActivity.this, "打开书失败", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        pageView.invalidate();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_read);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pageView = (PageView) findViewById(R.id.page);
        outlines = new ArrayList<>();
        books = ((BaseApplication) getApplication()).getBooks();
        currentBookIndex = getIntent().getIntExtra("currentBookIndex", 0);
        lastReadPoint = books.get(currentBookIndex).getRead();
        currentOutlineIndex = 0;
        pageWidth = Util.getScreenWidth(this);
        pageHeight = Util.getScreenHeight(this);
        getCofig();
        registerBatteryReceiver();//注册获取电量的receiver
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        Book book = books.get(currentBookIndex);
        book.setRead(currentPageCache.pages.get(currentPageCache.currentPageIndex).start);
        book.setLastreadtime(System.currentTimeMillis());
        new IOManager(this).updateBook(book);
        saveCofig();
    }

    /**
     * 保存配置信息
     */
    private void saveCofig() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentPageStyleIndex", currentPageStyleIndex);
        editor.putInt("pagingMode", pageView.getPagingMode());
        editor.putInt("textSize", textSize);
        editor.commit();
    }

    /**
     * 获取配置信息
     */
    private void getCofig() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        currentPageStyleIndex = preferences.getInt("currentPageStyleIndex", 0);
        textColor = TEXT_COLOR_LIST[currentPageStyleIndex];
        backgroundColor = BACKGROUND_COLOR_LIST[currentPageStyleIndex];
        int temp = preferences.getInt("pagingMode", PageView.NONE);
        pageView.setPagingMode(temp);
        textSize = preferences.getInt("textSize", 40);
    }

    private ImageView ivUp;
    private ImageView ivDown;
    private SeekBar sbProgress;
    private TextView tvOutline;
    private TextView tvMark;
    private TextView tvSetting;

    /**
     * 初始化底部菜单
     */
    private void initBottomPopMenu() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_menu, null);
        tvOutline = (TextView) view.findViewById(R.id.tv_outline);
        tvMark = (TextView) view.findViewById(R.id.tv_mark);
        tvSetting = (TextView) view.findViewById(R.id.tv_setting);
        ivUp = (ImageView) view.findViewById(R.id.iv_up);
        ivDown = (ImageView) view.findViewById(R.id.iv_down);
        sbProgress = (SeekBar) view.findViewById(R.id.sb_progress);
        menu = new PopupWindow(view);
        sbProgress.setMax(outlines.size() - 1);
        menu.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        menu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        menu.setOutsideTouchable(true);
        menu.setBackgroundDrawable(new BitmapDrawable());
        menu.setAnimationStyle(R.style.AnimationBottomMenu);
        tvOutline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.dismiss();
                showOutlineDialog();
            }
        });
        tvMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBookmarkWindow();
                menu.dismiss();
            }
        });
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.dismiss();
                showSettingPopupWindow();
            }
        });
        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentOutlineIndex > 0) {
                    currentOutlineIndex--;
                    sbProgress.setProgress(currentOutlineIndex);
                    currentPageCache = findCacheByOutlineIndex(currentOutlineIndex);
                    pageView.setCurrentPageBitmap(generatePageBitmap(0, currentPageCache.pages));
                    pageView.invalidate();
                }
            }
        });
        ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentOutlineIndex < outlines.size() - 1) {
                    currentOutlineIndex++;
                    sbProgress.setProgress(currentOutlineIndex);
                    currentPageCache = findCacheByOutlineIndex(currentOutlineIndex);
                    pageView.setCurrentPageBitmap(generatePageBitmap(0, currentPageCache.pages));
                    pageView.invalidate();
                }
            }
        });
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentOutlineIndex = progress;
                currentPageCache = findCacheByOutlineIndex(currentOutlineIndex);
                pageView.setCurrentPageBitmap(generatePageBitmap(0, currentPageCache.pages));
                pageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 显示底部菜单
     */
    private void showBottomMenu() {
        if (menu == null) {
            initBottomPopMenu();
        }
        if (!menu.isShowing() && (outlineDialog == null || !outlineDialog.isShowing())
                && (setting == null || !setting.isShowing()) &&
                (mBookmarkWindow == null || !mBookmarkWindow.isShowing())) {
            sbProgress.setProgress(currentOutlineIndex);
            menu.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        }
    }

    private PopupWindow setting;
    private static final int[] TEXT_COLOR_LIST = new int[]{Color.BLACK, Color.WHITE, Color.WHITE, Color.WHITE};
    private static final int[] BACKGROUND_COLOR_LIST = new int[]{Color.WHITE, Color.RED, Color.GREEN, Color.BLUE};
    private int currentPageStyleIndex = 0;

    /**
     * 初始化设置视图
     */
    private void initSettingPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.setting, null);
        final Button pagingSlip = (Button) view.findViewById(R.id.paging_slip_btn);
        final Button pagingCover = (Button) view.findViewById(R.id.paging_cover_btn);
        final Button pagingNone = (Button) view.findViewById(R.id.paging_none_btn);
        final ImageView pageStyle1 = (ImageView) view.findViewById(R.id.page_style_iv_1);
        final ImageView pageStyle2 = (ImageView) view.findViewById(R.id.page_style_iv_2);
        final ImageView pageStyle3 = (ImageView) view.findViewById(R.id.page_style_iv_3);
        final ImageView pageStyle4 = (ImageView) view.findViewById(R.id.page_style_iv_4);
        Button frontPlus = (Button) view.findViewById(R.id.front_plus_btn);
        Button frontSub = (Button) view.findViewById(R.id.front_sub_btn);
        final TextView frontShow = (TextView) view.findViewById(R.id.front_show_tv);
        pagingSlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagingSlip.setBackgroundResource(R.drawable.paging_mode_btn_selected);
                pagingCover.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pagingNone.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pageView.setPagingMode(PageView.HORIZONTAL_SLIP);
            }
        });
        pagingCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagingSlip.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pagingCover.setBackgroundResource(R.drawable.paging_mode_btn_selected);
                pagingNone.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pageView.setPagingMode(PageView.HORIZONTAL_COVER);
            }
        });
        pagingNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagingSlip.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pagingCover.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pagingNone.setBackgroundResource(R.drawable.paging_mode_btn_selected);
                pageView.setPagingMode(PageView.NONE);
            }
        });
        pageStyle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageStyle1.setBackgroundResource(R.drawable.page_background_style_selected);
                pageStyle2.setBackground(null);
                pageStyle3.setBackground(null);
                pageStyle4.setBackground(null);
                currentPageStyleIndex = 0;
                backgroundColor = BACKGROUND_COLOR_LIST[currentPageStyleIndex];
                textColor = TEXT_COLOR_LIST[currentPageStyleIndex];
                outlineDialog = null;
                mBookmarkWindow = null;
                pageView.setCurrentPageBitmap(generatePageBitmap(currentPageCache.currentPageIndex, currentPageCache.pages));
                pageView.invalidate();
            }
        });
        pageStyle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageStyle2.setBackgroundResource(R.drawable.page_background_style_selected);
                pageStyle1.setBackground(null);
                pageStyle3.setBackground(null);
                pageStyle4.setBackground(null);
                currentPageStyleIndex = 1;
                backgroundColor = BACKGROUND_COLOR_LIST[currentPageStyleIndex];
                textColor = TEXT_COLOR_LIST[currentPageStyleIndex];
                outlineDialog = null;
                mBookmarkWindow = null;
                pageView.setCurrentPageBitmap(generatePageBitmap(currentPageCache.currentPageIndex, currentPageCache.pages));
                pageView.invalidate();
            }
        });
        pageStyle3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageStyle3.setBackgroundResource(R.drawable.page_background_style_selected);
                pageStyle2.setBackground(null);
                pageStyle1.setBackground(null);
                pageStyle4.setBackground(null);
                currentPageStyleIndex = 2;
                backgroundColor = BACKGROUND_COLOR_LIST[currentPageStyleIndex];
                textColor = TEXT_COLOR_LIST[currentPageStyleIndex];
                outlineDialog = null;
                mBookmarkWindow = null;
                pageView.setCurrentPageBitmap(generatePageBitmap(currentPageCache.currentPageIndex, currentPageCache.pages));
                pageView.invalidate();
            }
        });
        pageStyle4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageStyle4.setBackgroundResource(R.drawable.page_background_style_selected);
                pageStyle2.setBackground(null);
                pageStyle3.setBackground(null);
                pageStyle1.setBackground(null);
                currentPageStyleIndex = 3;
                backgroundColor = BACKGROUND_COLOR_LIST[currentPageStyleIndex];
                textColor = TEXT_COLOR_LIST[currentPageStyleIndex];
                outlineDialog = null;
                mBookmarkWindow = null;
                pageView.setCurrentPageBitmap(generatePageBitmap(currentPageCache.currentPageIndex, currentPageCache.pages));
                pageView.invalidate();
            }
        });
        frontSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textSize > 20) {
                    textSize--;
                }
                frontShow.setText(textSize + "");
                currentPageCache = findOutlineCache(currentPageCache.pages.get(currentPageCache.currentPageIndex).start);
                pageView.setCurrentPageBitmap(generatePageBitmap(currentPageCache.currentPageIndex, currentPageCache.pages));
                pageView.invalidate();
            }
        });
        frontPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textSize < 80) {
                    textSize++;
                }
                frontShow.setText(textSize + "");
                currentPageCache = findOutlineCache(currentPageCache.pages.get(currentPageCache.currentPageIndex).start);
                pageView.setCurrentPageBitmap(generatePageBitmap(currentPageCache.currentPageIndex, currentPageCache.pages));
                pageView.invalidate();
            }
        });
        switch (pageView.getPagingMode()) {
            case PageView.HORIZONTAL_SLIP:
                pagingSlip.setBackgroundResource(R.drawable.paging_mode_btn_selected);
                pagingCover.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pagingNone.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                break;
            case PageView.HORIZONTAL_COVER:
                pagingSlip.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pagingCover.setBackgroundResource(R.drawable.paging_mode_btn_selected);
                pagingNone.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                break;
            case PageView.NONE:
                pagingSlip.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pagingCover.setBackgroundResource(R.drawable.paging_mode_btn_normal);
                pagingNone.setBackgroundResource(R.drawable.paging_mode_btn_selected);
                break;
        }
        switch (currentPageStyleIndex) {
            case 0:
                pageStyle1.setBackgroundResource(R.drawable.page_background_style_selected);
                pageStyle2.setBackground(null);
                pageStyle3.setBackground(null);
                pageStyle4.setBackground(null);
                break;
            case 1:
                pageStyle2.setBackgroundResource(R.drawable.page_background_style_selected);
                pageStyle1.setBackground(null);
                pageStyle3.setBackground(null);
                pageStyle4.setBackground(null);
                break;
            case 2:
                pageStyle3.setBackgroundResource(R.drawable.page_background_style_selected);
                pageStyle2.setBackground(null);
                pageStyle1.setBackground(null);
                pageStyle4.setBackground(null);
                break;
            case 3:
                pageStyle4.setBackgroundResource(R.drawable.page_background_style_selected);
                pageStyle2.setBackground(null);
                pageStyle3.setBackground(null);
                pageStyle1.setBackground(null);
                break;
        }
        frontShow.setText(textSize + "");
        setting = new PopupWindow(view);
        setting.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setting.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setting.setOutsideTouchable(true);
        setting.setBackgroundDrawable(new BitmapDrawable());
        setting.setAnimationStyle(R.style.AnimationBottomMenu);
    }

    /**
     * 显示设置视图
     */
    private void showSettingPopupWindow() {
        if (setting == null) {
            initSettingPopupWindow();
        }
        setting.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private PopupWindow outlineDialog;
    private ListView lvOutlines;
    private OutlineListAdapter adapter1;

    /**
     * 初始化目录popupwindow
     */
    private void initOutlineDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_outline, null);
        view.setBackgroundColor(backgroundColor);
        lvOutlines = (ListView) view.findViewById(R.id.lv_outline);
        adapter1 = new OutlineListAdapter(this, outlines, currentOutlineIndex);
        lvOutlines.setAdapter(adapter1);
        lvOutlines.setSelection(currentOutlineIndex);
        lvOutlines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPageCache = findCacheByOutlineIndex(currentOutlineIndex);
                pageView.setCurrentPageBitmap(generatePageBitmap(0, currentPageCache.pages));
                outlineDialog.dismiss();
                pageView.invalidate();
            }
        });
        outlineDialog = new PopupWindow(view);
        outlineDialog.setWidth((int) (pageWidth * 0.8));
        outlineDialog.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        outlineDialog.setOutsideTouchable(true);
        outlineDialog.setBackgroundDrawable(new BitmapDrawable());
        outlineDialog.setAnimationStyle(R.style.AnimationLeftOutline);
        outlineDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 弹出目录窗口
     */
    private void showOutlineDialog() {
        if (outlineDialog == null) {
            initOutlineDialog();
        }
        if (!outlineDialog.isShowing()) {
            adapter1.setCurrentOutlineIndex(currentOutlineIndex);
            outlineDialog.showAtLocation(getWindow().getDecorView(), Gravity.LEFT, 0, 0);
            lvOutlines.setSelection(currentOutlineIndex);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.7f;
            getWindow().setAttributes(lp);
        }
    }

    private PopupWindow mBookmarkWindow;
    private List<Mark> marks;

    /**
     * 初始化书签窗口
     */
    private void initBookmarkWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.bookmark_window, null);
        final ListView bookmarks = (ListView) view.findViewById(R.id.bookmark_lv);
        final EditText bookmarkName = (EditText) view.findViewById(R.id.bookmark_name_et);
        ImageView addBookmark = (ImageView) view.findViewById(R.id.add_bookmark_iv);
        view.setBackgroundColor(backgroundColor);
        final BookmarkListAdapter adapter2 = new BookmarkListAdapter(this, marks);
        bookmarks.setAdapter(adapter2);
        addBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = bookmarkName.getText().toString();
                Mark mark = new Mark();
                mark.setPath(books.get(currentBookIndex).getPath());
                mark.setCreatetime(System.currentTimeMillis());
                mark.setRead(currentPageCache.pages.get(currentPageCache.currentPageIndex).start);
                if (name.isEmpty()) {
                    mark.setName("书签" + marks.size());
                } else {
                    mark.setName(name);
                }
                IOManager ioManager = new IOManager(ReadActivity.this);
                ioManager.saveMark(mark);
                ioManager.getAllMark(books.get(currentBookIndex).getPath(), marks);
                adapter2.notifyDataSetChanged();
                bookmarks.setSelection(marks.size() - 1);
                bookmarkName.getText().clear();
            }
        });
        mBookmarkWindow = new PopupWindow(view, (int) (pageWidth * 0.9), (int) (pageHeight * 0.5), true);
        mBookmarkWindow.setOutsideTouchable(true);
        mBookmarkWindow.setBackgroundDrawable(new BitmapDrawable());
        mBookmarkWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 显示书签窗口
     */
    private void showBookmarkWindow() {
        if (mBookmarkWindow == null) {
            initBookmarkWindow();
        }
        if (!mBookmarkWindow.isShowing()) {
            mBookmarkWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.7f;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            showBottomMenu();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (menu != null && menu.isShowing()) {
                menu.dismiss();
                return true;
            }
            if (outlineDialog != null && outlineDialog.isShowing()) {
                outlineDialog.dismiss();
                return true;
            }
            if (setting != null && setting.isShowing()) {
                setting.dismiss();
                return true;
            }
            if (mBookmarkWindow != null && mBookmarkWindow.isShowing()) {
                mBookmarkWindow.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (books.get(currentBookIndex).getInit() == 0) {
            dialog = ProgressDialog.show(ReadActivity.this, "", "正在解析目录中...", false, false);
            new Thread() {
                @Override
                public void run() {
                    //解析章节
                    if (TextManage.parseOutline(books.get(currentBookIndex), ReadActivity.this)) {
                        //查找全部章节
                        IOManager ioManager = new IOManager(ReadActivity.this);
                        ioManager.getAllOutline(books.get(currentBookIndex).getPath(), outlines);
                        currentPageCache = findOutlineCache(0);
                        lastReadPoint = 0;
                        pageView.setCurrentPageBitmap(generatePageBitmap(currentPageCache.currentPageIndex, currentPageCache.pages));
                        currentOutlineIndex = currentPageCache.outlineIndex;
                        //查找全部书签
                        marks = new ArrayList<>();
                        ioManager.getAllMark(books.get(currentBookIndex).getPath(), marks);
                    }
                    handler.sendEmptyMessage(PARSE_OUTLINES);
                }
            }.start();
        } else {
            //查找全部章节目录
            IOManager ioManager = new IOManager(ReadActivity.this);
            ioManager.getAllOutline(books.get(currentBookIndex).getPath(), outlines);
            currentPageCache = findOutlineCache(lastReadPoint);
            pageView.setCurrentPageBitmap(generatePageBitmap(currentPageCache.currentPageIndex, currentPageCache.pages));
            currentOutlineIndex = currentPageCache.outlineIndex;
            pageView.invalidate();
            marks = new ArrayList<>();
            ioManager.getAllMark(books.get(currentBookIndex).getPath(), marks);
        }
    }

    /**
     * 设置事件监听
     */
    private void initEvent() {
        pageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int offsetX = (int) event.getX() - startX;
                        if (actionUpOrDowm == 0 && offsetX < 0) {
                            actionUpOrDowm = 1;
                            findDowmBitmap();
                            if (downPageCache == null) {
                                Toast.makeText(ReadActivity.this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
                                actionUpOrDowm = 0;
                            } else {
                                pageView.setDownPageBitmap(generatePageBitmap(downPageCache.currentPageIndex, downPageCache.pages));
                            }
                        }
                        if (actionUpOrDowm == 0 && offsetX > 0) {
                            actionUpOrDowm = -1;
                            findUpBitmap();
                            if (upPageCache == null) {
                                Toast.makeText(ReadActivity.this, "前面没有了", Toast.LENGTH_SHORT).show();
                                actionUpOrDowm = 0;
                            } else {
                                pageView.setUpPageBitmap(generatePageBitmap(upPageCache.currentPageIndex, upPageCache.pages));
                            }
                        }
                        if (actionUpOrDowm == 1) {
                            if (offsetX >= 0) {
                                pageView.setCurrentOffsetX(0);
                                actionUpOrDowm = 0;
                            } else {
                                pageView.setCurrentOffsetX(offsetX);
                            }
                            pageView.invalidate();
                        }
                        if (actionUpOrDowm == -1) {
                            if (offsetX <= 0) {
                                actionUpOrDowm = 0;
                                pageView.setCurrentOffsetX(0);
                                pageView.setUpOffsetX(-pageView.getWidth());
                            } else {
                                if (pageView.getPagingMode() == pageView.HORIZONTAL_SLIP) {
                                    pageView.setCurrentOffsetX(offsetX);
                                } else if (pageView.getPagingMode() == pageView.HORIZONTAL_COVER) {
                                    pageView.setUpOffsetX(-pageView.getWidth() + offsetX);
                                }
                            }
                            pageView.invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (actionUpOrDowm == 0 && startX > pageView.getWidth() * 0.25 &&
                                startX < pageView.getWidth() * 0.75 && event.getY() > pageView.getHeight() * 0.25 &&
                                event.getY() < pageView.getHeight() * 0.75) {
                            showBottomMenu();
                        } else if (actionUpOrDowm == 1) {
                            actionUpOrDowm = 0;
                            pageView.setCurrentOffsetX(0);
                            pageView.setUpOffsetX(-pageView.getWidth());
                            pageView.setCurrentPageBitmap(pageView.getDownPageBitmap());
                            currentOutlineIndex = downPageCache.outlineIndex;
                            currentPageCache = downPageCache;
                            pageView.invalidate();
                        } else if (actionUpOrDowm == -1) {
                            actionUpOrDowm = 0;
                            pageView.setCurrentOffsetX(0);
                            pageView.setUpOffsetX(-pageView.getWidth());
                            pageView.setCurrentPageBitmap(pageView.getUpPageBitmap());
                            currentOutlineIndex = upPageCache.outlineIndex;
                            currentPageCache = upPageCache;
                            pageView.invalidate();
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 注册获取电量的receiver
     */
    private void registerBatteryReceiver() {
        //注册广播接受者java代码
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //创建广播接受者对象
        receiver = new BatteryReceiver();
        //注册receiver
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 获取电量比 0~100;
     *
     * @return
     */
    private int getBatteryRatio() {
        if (receiver != null) {
            return receiver.getRatio();
        } else return 100;
    }

    /**
     * 根据页面的起始位置找到该页面所属章节
     *
     * @param point
     * @return
     */
    private int findOutlineByPoint(long point) {
        int tempOutlineIndex = currentOutlineIndex;
        while (tempOutlineIndex > -1 && tempOutlineIndex < outlines.size()) {
            if (point >= outlines.get(tempOutlineIndex).getBegin() && point < outlines.get(tempOutlineIndex).getEnd()) {
                break;
            } else if (point >= outlines.get(tempOutlineIndex).getEnd()) {
                tempOutlineIndex++;
            } else {
                tempOutlineIndex--;
            }
        }
        System.out.println(tempOutlineIndex);
        return tempOutlineIndex;
    }

    private class Page {
        public long start;
        public List<String> lines;
    }

    private class PageCache {
        public int outlineIndex;
        public int currentPageIndex;
        public List<Page> pages;
    }

    /**
     * 找到指定章节的首页
     *
     * @param outlineIndex
     * @return
     */
    private PageCache findCacheByOutlineIndex(int outlineIndex) {
        PageCache pageCache = new PageCache();
        pageCache.outlineIndex = outlineIndex;
        pageCache.pages = getTextPages(outlines.get(outlineIndex));
        pageCache.currentPageIndex = 0;
        return pageCache;
    }

    /**
     * 获取指定位置所属章节的页面cache
     *
     * @param point
     * @return
     */
    private PageCache findOutlineCache(long point) {
        PageCache pageCache = new PageCache();
        pageCache.outlineIndex = findOutlineByPoint(point);
        pageCache.pages = getTextPages(outlines.get(pageCache.outlineIndex));
        for (int i = 0; i < pageCache.pages.size() - 1; i++) {
            if (point >= pageCache.pages.get(i).start && point < pageCache.pages.get(i + 1).start) {
                pageCache.currentPageIndex = i;
            }
        }
        return pageCache;
    }

    /**
     * 得到一章全部的page
     *
     * @param outline
     * @return
     */
    private List<Page> getTextPages(Outline outline) {
        String text = readOutlineText(outline);
        List<Page> pages = new ArrayList<>();
        int y = 0;
        int i = 0;
        int topAreaHeight = 40;
        int bottomAreaHeight = 40;
        int padding = (int) (1.5 * textSize);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
        y = topAreaHeight + padding;
        String line = "";
        Page page = new Page();
        for (i = 0; i < text.length(); i++) {
            if (y == topAreaHeight + padding) {
                page = new Page();
                page.start = i + outline.getBegin();
                page.lines = new ArrayList<>();
            }
            if (i == 0) {
                int chapterTextSize = (int) (1.2 * textSize);
                String temp = outline.getName();
                page.lines.add(temp);
                paint.setTextSize(chapterTextSize);
                while (temp.length() > 0) {
                    int count = paint.breakText(temp, false, pageWidth - 2 * padding, null);
                    y += 1.5 * chapterTextSize;
                    temp = temp.substring(count);
                }

                y += 0.5 * chapterTextSize;
            }
            paint.setTextSize(textSize);
            char ch = text.charAt(i);
            if (ch == '\n' || ch == '\r') {
                //遇到换行
                if (line.length() > 0) {
                    y += 2 * textSize;
                    page.lines.add(line);
                    page.lines.add("\n");
                    line = "";
                }
            } else {
                line += ch;
                float lineWidth = paint.measureText(line);
                if (lineWidth > pageWidth - 2 * padding) {
                    //超出一行，自动换行
                    y += 1.5 * textSize;
                    page.lines.add(line.substring(0, line.length() - 1));
                    page.lines.add("");
                    line = "" + ch;
                }
            }
            //是否超出最大高度
            if (y + textSize + padding + bottomAreaHeight > pageHeight) {
                pages.add(page);
                y = topAreaHeight + padding;
                continue;
            }
        }
        if (y > topAreaHeight + padding) {
            //一页未画满,则证明是最后一页
            pages.add(page);
        }
        return pages;
    }

    private int textColor = Color.BLACK;
    private int textSize = 40;
    private int backgroundColor = Color.WHITE;

    /**
     * 获取一章中指定page的bitmap
     *
     * @param PageIndex
     * @param pages
     * @return
     */
    private Bitmap generatePageBitmap(int PageIndex, List<Page> pages) {

        Page page = pages.get(PageIndex);
        Bitmap bitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        int y = 0;
        int textSize0 = 32;
        int textColor0 = Color.GRAY;
        int topAreaHeight = 40;
        int padding = (int) (1.5 * textSize);
        int chapterTextSize = (int) (1.2 * textSize);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));

        //画背景
        canvas.drawColor(backgroundColor);
        paint.setTextSize(textSize0);
        paint.setColor(textColor0);
        y = topAreaHeight - 5;
        //画顶端的章节和书名
        String bookName = books.get(currentBookIndex).getName().replace(".txt", "");
        canvas.drawText(bookName, padding, y, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(pages.get(0).lines.get(0), pageWidth - padding, y, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        //画进度
        String ss = Util.getPercentString(books.get(currentBookIndex).getTotal(), page.start);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(ss, pageWidth - padding, pageHeight - 15, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        //画电量
        Path path1 = new Path();
        path1.moveTo(padding, pageHeight - 20);
        path1.lineTo(padding, pageHeight - 30);
        path1.lineTo(padding + 2, pageHeight - 30);
        path1.lineTo(padding + 2, pageHeight - 40);
        path1.lineTo(padding + 52, pageHeight - 40);
        path1.lineTo(padding + 52, pageHeight - 10);
        path1.lineTo(padding + 2, pageHeight - 10);
        path1.lineTo(padding + 2, pageHeight - 20);
        path1.close();
        int per = 46 * getBatteryRatio() / 100;
        Rect r = new Rect(padding + 4, pageHeight - 38, padding + 4 + per, pageHeight - 12);
        Paint p = new Paint();
        p.setColor(textColor0);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path1, p);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(r, p);
        //画时间
        String time = Util.getCurrentTimeString();
        canvas.drawText(time, padding + 60, pageHeight - 15, paint);
        y = topAreaHeight + padding;
        paint.setColor(textColor);
        for (String line : page.lines) {
            if (PageIndex == 0) {
                //章节开始页
                paint.setTextSize(chapterTextSize);
                while (line.length() > 0) {
                    int count = paint.breakText(line, false, pageWidth - 2 * padding, null);
                    y += chapterTextSize;
                    canvas.drawText(line.substring(0, count), padding, y, paint);
                    line = line.substring(count);
                    y += 0.5 * chapterTextSize;
                }
                y += 0.5 * chapterTextSize;
                PageIndex = -1;
                continue;
            }
            paint.setTextSize(textSize);
            if (line == "\n") {
                y += textSize;
            } else if (line.length() == 0) {
                y += 0.5 * textSize;
            } else {
                y += textSize;
                canvas.drawText(line, padding, y, paint);
            }
        }
        return bitmap;
    }

    /**
     * 读取一个章节的全部文字
     *
     * @param outline 章节
     * @return String 文字
     */
    private String readOutlineText(Outline outline) {
        String re = null;
        int length = (int) (outline.getEnd() - outline.getBegin());
        char[] chars = new char[length];
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(outline.getPath()), charset));
            br.skip(outline.getBegin());
            br.read(chars, 0, length);
            re = new String(chars);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }


    /**
     * 找到下一页bitmap
     */
    private void findDowmBitmap() {
        if (currentPageCache.outlineIndex == outlines.size() - 1) {
            downPageCache = null;
        } else if (currentPageCache.currentPageIndex == currentPageCache.pages.size() - 1) {
            downPageCache = new PageCache();
            downPageCache.outlineIndex = currentPageCache.outlineIndex + 1;
            downPageCache.pages = getTextPages(outlines.get(downPageCache.outlineIndex));
            downPageCache.currentPageIndex = 0;
        } else {
            downPageCache = new PageCache();
            downPageCache.outlineIndex = currentPageCache.outlineIndex;
            downPageCache.pages = currentPageCache.pages;
            downPageCache.currentPageIndex = currentPageCache.currentPageIndex + 1;
        }
    }

    /**
     * 找到上一页bitmap
     */
    private void findUpBitmap() {
        if (currentPageCache.outlineIndex == 0) {
            upPageCache = null;
        } else if (currentPageCache.currentPageIndex == 0) {
            upPageCache = new PageCache();
            upPageCache.outlineIndex = currentPageCache.outlineIndex - 1;
            upPageCache.pages = getTextPages(outlines.get(upPageCache.outlineIndex));
            upPageCache.currentPageIndex = upPageCache.pages.size() - 1;
        } else {
            upPageCache = new PageCache();
            upPageCache.outlineIndex = currentPageCache.outlineIndex;
            upPageCache.pages = currentPageCache.pages;
            upPageCache.currentPageIndex = currentPageCache.currentPageIndex - 1;
        }
    }
}
