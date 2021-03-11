package com.xiaojinzi.componentdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.xiaojinzi.componentdemo.R;

/**
 * Created by cxj on 2017/3/30.
 * 这是一个流式布局,但是并不是一个列表
 * 如果想实现很长的列表的形式,对不起这个不适合你
 * 流式布局这个可以放任何控件,但是每一个孩子高度都是一样的
 * 关于宽度,此控件并没有处理测量,都是直接采用父容器推荐的宽,所以不能放在水平的列表或者水平的滚动ScrollerView中
 * 关于高度,控件支持了包裹和填充父容器,也支持在竖直的列表和滚动ScrollerView中
 *
 * @note: 子控件不支持使用margin等属性
 */
public class XFlowLayout extends ViewGroup {

    public XFlowLayout(Context context) {
        this(context, null);
    }

    public XFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XFlowLayout);

        //获取横纵向的间距

        mHSpace = a.getDimensionPixelSize(R.styleable.XFlowLayout_h_space, dpToPx(10));
        mVSpace = a.getDimensionPixelSize(R.styleable.XFlowLayout_v_space, dpToPx(10));
        mMaxLines = a.getInt(R.styleable.XFlowLayout_maxlines, mMaxLines);
        mMaxColums = a.getInt(R.styleable.XFlowLayout_maxcolums, mMaxColums);

        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 拿到父容器推荐的宽和高以及计算模式

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 测量孩子的大小
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // 几行
        int lines = 1;
        int colums = 0;

        int contentLeft = getPaddingLeft();
        int contentTop = getPaddingTop();
        int contentRight = sizeWidth - getPaddingRight();

        // 初始化
        int left = contentLeft, top = contentTop;
        int lineStartIndex = 0, lineEndIndex = 0;

        // 循环所有的孩子
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            lineEndIndex = i;
            // 拿到每一个孩子
            View view = getChildAt(i);
            // 每一个孩子的测量宽度
            int childMeasuredWidth = view.getMeasuredWidth();

            // 如果满足说明这一个已经一行放不下了
            if (left + childMeasuredWidth > contentRight || (mMaxColums >= 1 && colums >= mMaxColums)) {
                // 如果到了最大的行数,就跳出,top就是当前的
                if (mMaxLines >= 1 && mMaxLines <= lines) {
                    break;
                }
                top += findMaxChildMaxHeight(lineStartIndex, lineEndIndex) + mVSpace;
                lineStartIndex = i;
                left = contentLeft;
                lines++;
                colums = 0;
            }

            left += childMeasuredWidth + mHSpace;
            colums++;

        }

        // 计算出全部的高度
        int measureHeight = top + findMaxChildMaxHeight(lineStartIndex, getChildCount() - 1) + getPaddingBottom();

        if (modeHeight == MeasureSpec.EXACTLY) {
            //直接使用父类推荐的宽和高
            setMeasuredDimension(sizeWidth, sizeHeight);
        } else if (modeHeight == MeasureSpec.AT_MOST) {
            setMeasuredDimension(sizeWidth, measureHeight > sizeHeight ? sizeHeight : measureHeight);
        } else {
            setMeasuredDimension(sizeWidth, measureHeight);
        }

    }

    /**
     * <1 表示不限制
     */
    private int mMaxLines = 0;

    /**
     * <1 表示不限制
     */
    private int mMaxColums = 0;

    /**
     * 每一个孩子的左右的间距
     * 20是默认值,单位是px
     */
    private int mHSpace = 20;

    /**
     * 每一行的上下的间距
     * 20是默认值,单位是px
     */
    private int mVSpace = 20;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //开始安排孩子的位置

        int contentLeft = getPaddingLeft();
        int contentTop = getPaddingTop();
        int contentRight = getWidth() - getPaddingRight();

        // 初始化
        int left = contentLeft, top = contentTop;
        int lineStartIndex = 0, lineEndIndex = 0;

        // 几行
        int lines = 1;
        int colums = 0;

        //循环所有的孩子
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            lineEndIndex = i;
            // 获取到每一个孩子
            View view = getChildAt(i);
            // 每一个孩子的测量宽度
            int childMeasuredWidth = view.getMeasuredWidth();

            // 如果满足说明这一个已经一行放不下了
            if (left + childMeasuredWidth > contentRight || (mMaxColums >= 1 && colums >= mMaxColums)) {
                // 如果到了最大的行数,就跳出,top就是当前的
                if (mMaxLines >= 1 && mMaxLines <= lines) {
                    break;
                }
                top += findMaxChildMaxHeight(lineStartIndex, lineEndIndex) + mVSpace;
                lineStartIndex = i;
                left = contentLeft;
                lines++;
                colums = 0;
            }

            //int dy = (mChildMaxHeight - view.getMeasuredHeight()) / 2;

            //安排孩子的位置
            view.layout(left, top, left + childMeasuredWidth, top + view.getMeasuredHeight());

            left += view.getMeasuredWidth() + mHSpace;
            colums++;

        }

    }

    /**
     * @param startIndex
     * @param endIndex
     */
    private int findMaxChildMaxHeight(int startIndex, int endIndex) {
        int result = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            View view = getChildAt(i);
            if (view.getMeasuredHeight() > result) {
                result = view.getMeasuredHeight();
            }
        }
        return result;
    }

    /**
     * dp的单位转换为px的
     *
     * @param dps
     * @return
     */
    public int dpToPx(int dps) {
        return Math.round(getResources().getDisplayMetrics().density * dps);
    }

    public void setHSpace(int hSpace) {
        this.mHSpace = hSpace;
    }

    public void setVSpace(int vSpace) {
        this.mVSpace = vSpace;
    }

}