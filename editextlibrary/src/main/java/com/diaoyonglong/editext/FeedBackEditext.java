package com.diaoyonglong.editext;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by diaoyonglong on 2019/1/26
 *
 * @desc Editext输入框 + 计数
 */

public class FeedBackEditext extends LinearLayout {

    //类型1(单数类型)：TextView显示总字数，然后根据输入递减.例：100，99，98
    //类型2(百分比类型)：TextView显示总字数和当前输入的字数，例：0/100，1/100，2/100
    public static final String SINGULAR = "Singular";//类型1(单数类型)
    public static final String PERCENTAGE = "Percentage";//类型2(百分比类型)

    //主布局
    private LinearLayout llAll;
    //文本框
    private EditText edtContent;
    //字数显示Layout
    private LinearLayout llTvNum;
    //字数显示TextView
    private TextView tvNum;
    //底部横线(暂时无用)
    private View vLine;

    /**
     * 声明EdiText属性
     */
    //整体背景色
    private int llBgolor;
    //整体padding
    private int llPadding;
    //默认文案
    private String edtHint;
    //默认文案 字体颜色
    private int edtHintColor;
    //EditText 字体颜色
    private int edtColor;
    //EditText 高度
    private int edtHeight;
    //EditText 字体大小
    private float edtSize;
    //EditText 行间距
    private int edtLineSpacing;
    //EditText 光标
    private int edtcursorDrawable;
    //计数 字体颜色
    private int txtColor;
    //计数 字体大小
    private float txtSize;
    //计数位置 left right center
    private int txtGravity = 3;
    //计数 类型
    private String TYPES = PERCENTAGE;
    //计数 最大字符
    private int MaxNum = 1000;
    /**
     * 默认值
     */
    // 字体大小
    private int defaultSize = 15;
    // 字体颜色
    private int defaultTextColor = getResources().getColor(R.color.color_666666);
    // 背景颜色
    private int defaultBgColor = getResources().getColor(R.color.color_F6F6F6);
    // /计数 text默认显示位置 1左 2中 3右
    private int defaultTxtGravity = 3;
    // /计数 text距离顶部距离margin
    private int txtMarginTop;

    public FeedBackEditext(Context context) {
        super(context);
    }

    public FeedBackEditext(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化
        initView(context, attrs);
        //填充初始数据
        initData();
    }

    public FeedBackEditext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        initView(context, attrs);
        //填充初始数据
        initData();
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_editext, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FeedBackEditext);

        llBgolor = typedArray.getColor(R.styleable.FeedBackEditext_bg_color, defaultBgColor);
        llPadding = typedArray.getDimensionPixelSize(R.styleable.FeedBackEditext_ll_padding, 20);
        edtColor = typedArray.getColor(R.styleable.FeedBackEditext_edt_color, defaultTextColor);
        edtSize = typedArray.getInteger(R.styleable.FeedBackEditext_edt_size, defaultSize);
        edtHeight = typedArray.getDimensionPixelSize(R.styleable.FeedBackEditext_edt_height, 150);
        edtHint = typedArray.getString(R.styleable.FeedBackEditext_edt_hint);
        edtHintColor = typedArray.getColor(R.styleable.FeedBackEditext_edt_hint_color, defaultBgColor);
        edtLineSpacing = typedArray.getInteger(R.styleable.FeedBackEditext_edt_line_spacing, 2);
        edtcursorDrawable = typedArray.getResourceId(R.styleable.FeedBackEditext_edt_cursor_drawable, R.drawable.base_cursor_color);
        txtColor = typedArray.getColor(R.styleable.FeedBackEditext_text_color, defaultTextColor);
        txtSize = typedArray.getInteger(R.styleable.FeedBackEditext_text_size, defaultSize);
        MaxNum = typedArray.getInteger(R.styleable.FeedBackEditext_text_max, MaxNum);
        txtGravity = typedArray.getInteger(R.styleable.FeedBackEditext_text_gravity, defaultTxtGravity);
        txtMarginTop = typedArray.getDimensionPixelSize(R.styleable.FeedBackEditext_text_margin_top, 10);

        //回收资源，这一句必须调用
        typedArray.recycle();

    }

    private void initData() {
        llAll = (LinearLayout) findViewById(R.id.ll_all);
        edtContent = (EditText) findViewById(R.id.edt_content);
        llTvNum = (LinearLayout) findViewById(R.id.layout_txt_num);
        tvNum = (TextView) findViewById(R.id.tv_num);
        vLine = (View) findViewById(R.id.v_line);

        llAll.setBackgroundColor(llBgolor);
        llAll.setPadding(llPadding, llPadding, llPadding, llPadding);
        edtContent.setTextSize(edtSize);
        edtContent.setTextColor(edtColor);
        edtContent.setHint(edtHint);
        edtContent.setHintTextColor(edtHintColor);
        edtContent.setLineSpacing(edtLineSpacing, 1);
        ViewGroup.LayoutParams edtlp = edtContent.getLayoutParams();
        edtlp.height = edtHeight;
        edtContent.setLayoutParams(edtlp);

        //设置光标颜色、粗细
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(edtContent, edtcursorDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvNum.setTextSize(txtSize);
        tvNum.setTextColor(txtColor);

        //计数类型
        if (TYPES.equals(SINGULAR)) {//类型1
            tvNum.setText(String.valueOf(MaxNum));
        } else if (TYPES.equals(PERCENTAGE)) {//类型2
            tvNum.setText(0 + "/" + MaxNum);
        }
        //设置长度
        edtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MaxNum)});
        //监听输入
        edtContent.addTextChangedListener(mTextWatcher);

        // 数字位置
        LayoutParams params = (LayoutParams) llTvNum.getLayoutParams();
        switch (txtGravity) {
            case 1:
                params.gravity = Gravity.LEFT;
                break;
            case 2:
                params.gravity = Gravity.CENTER_HORIZONTAL;
                break;
            case 3:
                params.gravity = Gravity.RIGHT;
                break;
        }
        params.topMargin = txtMarginTop;
        llTvNum.setLayoutParams(params);
    }

    /**
     * Editext 监听
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;

        public void afterTextChanged(Editable s) {
            editStart = edtContent.getSelectionStart();
            editEnd = edtContent.getSelectionEnd();
            // 先去掉监听器，否则会出现栈溢出
            edtContent.removeTextChangedListener(mTextWatcher);
            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            while (calculateLength(s.toString()) > MaxNum) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            // 恢复监听器
            edtContent.addTextChangedListener(mTextWatcher);
            setLeftCount();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    /**
     * 刷新剩余输入字数
     */
    private void setLeftCount() {
        if (TYPES.equals(SINGULAR)) {//类型1
            tvNum.setText(String.valueOf((MaxNum - getInputCount())));
        } else if (TYPES.equals(PERCENTAGE)) {//类型2
            tvNum.setText(MaxNum - (MaxNum - getInputCount()) + "/" + MaxNum);
        }
    }

    /**
     * 获取用户输入内容字数
     */
    private long getInputCount() {
        return calculateLength(edtContent.getText().toString());
    }

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点
     * 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param cs
     * @return
     */
    public static long calculateLength(CharSequence cs) {
        double len = 0;
        for (int i = 0; i < cs.length(); i++) {
            int tmp = (int) cs.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 1;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    /**
     * 获取 输入框内容
     *
     * @return
     */
    public String getContent() {
        return edtContent.getText().toString().trim();
    }
}
