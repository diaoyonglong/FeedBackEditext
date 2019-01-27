package com.diaoyonglong.editext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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


    private LinearLayout llAll;//主布局
    private EditText edtContent;//文本框
    private LinearLayout llTvNum;//字数显示LinearLayout
    private TextView tvNum;//字数显示TextView
    private View vLine;//底部横线

    private String TYPES = PERCENTAGE;//类型
    private int MaxNum = 1000;//最大字符

    //声明EdiText属性
    private int llBgolor;//背景色
    private String edtHint = "请输入...";//
    private int edtColor;//
    private float edtSize;//
    private int edtLineSpacing;//行间距
    private int edtPadding;//padding
    private int txtColor;//
    private float txtSize;//
    // 数字位置：LEFT RIGHT CENTER
    private int txtGravity = 3;

    /**
     * 默认值
     */
    // 字体大小
    private int defaultSize = 15;
    // 字体颜色
    private int defaultTextColor = Color.BLACK;
    // 背景颜色
    private int defaultBgColor = Color.GRAY;
    // text默认显示位置 1左2居中3右边
    private int defaultTxtGravity = 3;
    // text距离顶部距离margin
    private int txtMarginTop;

    public FeedBackEditext(Context context) {
        super(context);
    }

    public FeedBackEditext(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //填充初始数据
        initData(context, attrs);
        //初始化按钮的点击事件，并在点击事件中调用用户传入的接口
        initbtnClick();
    }

    public FeedBackEditext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //填充初始数据
        initData(context, attrs);
        //初始化按钮的点击事件，并在点击事件中调用用户传入的接口
        initbtnClick();
    }

    private void initData(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_editext, this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FeedBackEditext);
        edtHint = typedArray.getString(R.styleable.FeedBackEditext_edtHint);
        llBgolor = typedArray.getColor(R.styleable.FeedBackEditext_bgColor, defaultBgColor);
        edtColor = typedArray.getColor(R.styleable.FeedBackEditext_edtColor, defaultTextColor);
        edtSize = typedArray.getInteger(R.styleable.FeedBackEditext_edtSize, defaultSize);
        edtLineSpacing = typedArray.getInteger(R.styleable.FeedBackEditext_edtLineSpacing, 2);
        edtPadding = typedArray.getInteger(R.styleable.FeedBackEditext_edtPadding, 20);
        txtColor = typedArray.getColor(R.styleable.FeedBackEditext_textColor, defaultTextColor);
        txtSize = typedArray.getInteger(R.styleable.FeedBackEditext_textSize, defaultSize);
        MaxNum = typedArray.getInteger(R.styleable.FeedBackEditext_textMax, MaxNum);
        txtGravity = typedArray.getInteger(R.styleable.FeedBackEditext_textGravity, defaultTxtGravity);
        txtMarginTop = typedArray.getDimensionPixelSize(R.styleable.FeedBackEditext_textMarginTop, 50);

        //回收资源，这一句必须调用
        typedArray.recycle();

    }

    private void initbtnClick() {
        llAll = (LinearLayout) findViewById(R.id.ll_all);
        edtContent = (EditText) findViewById(R.id.edt_content);
        llTvNum = (LinearLayout) findViewById(R.id.layout_txt_num);
        tvNum = (TextView) findViewById(R.id.tv_num);
        vLine = (View) findViewById(R.id.v_line);

        edtContent.setTextSize(edtSize);
        edtContent.setTextColor(edtColor);
        edtContent.setLineSpacing(edtLineSpacing, 1);
        edtContent.setPadding(edtPadding, edtPadding, edtPadding, edtPadding);
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
        llTvNum.setLayoutParams(params);
    }

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
}
