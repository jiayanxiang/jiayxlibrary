package com.jyx.mylibrary.widget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyx.mylibrary.R;
import com.jyx.mylibrary.utils.DisplayUtils;
import com.jyx.mylibrary.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jyx
 * @ctime 2017/12/22:10:03
 * @explain 九图，多图展示自由适配
 */

public class NineFreedomGridLayout extends ViewGroup {

    public static final float DEFUALT_SPACING = 5f;
    private int MAX_COUNT = 9;

    protected Context mContext;
    private float mSpacing = DEFUALT_SPACING;
    private int mColumns;
    private int mRows;
    private int mTotalWidth;
    private int mSingleWidth;

    private boolean mIsShowAll = false;
    private boolean mIsFirst = true;
    private List<String> mStringList = new ArrayList<>();

    private int showNumber = 0;

    public NineFreedomGridLayout(Context context) {
        super(context);
        init(context);
    }

    public NineFreedomGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridLayout);

        mSpacing = typedArray.getDimension(R.styleable.NineGridLayout_sapcing, DEFUALT_SPACING);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        if (getListSize(mStringList) == 0) {
            setVisibility(GONE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mTotalWidth = right - left;
        mSingleWidth = (int) ((mTotalWidth - mSpacing * (mColumns - 1)) / mColumns);
        if (mIsFirst) {
            notifyDataSetChanged();
            mIsFirst = false;
        }
    }

    /**
     * 设置间隔
     *
     * @param spacing
     */
    public void setSpacing(float spacing) {
        mSpacing = spacing;
    }

    /**
     * 设置是否显示所有图片（超过最大数时）
     *
     * @param isShowAll
     */
    public void setIsShowAll(boolean isShowAll) {
        mIsShowAll = isShowAll;
    }

    /**
     * 设置显示数量
     *
     * @param number
     */
    public void setShowNumber(int number) {
        this.showNumber = number;
    }

    /**
     * 显示最大数量
     *
     * @param maxCount
     */
    public void setMaxCount(int maxCount) {
    }

    /**
     * 添加数据
     *
     * @param stringList
     */
    public void setStringList(List<String> stringList) {
        //如果数据集合为空，不显示此控件
        if (getListSize(stringList) == 0) {
            setVisibility(GONE);
            removeAllViews();
            return;
        }

        if (getListSize(stringList) ==1){
            if (StringUtils.isObjectEmpty(stringList.get(0))){
                setVisibility(GONE);
                removeAllViews();
                return;
            }
        }
        setVisibility(VISIBLE);

        //清空原有数据，重新添加新数据
        mStringList.clear();
        mStringList.addAll(stringList);
        generateChildrenLayout(mStringList.size());//根据图片数量设置行列

        if (!mIsFirst) {
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param
     */
    public void setStringList(String[] strings) {
        //如果数据集合为空，不显示此控件
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            stringList.add(strings[i]);
        }
        if (getListSize(stringList) == 0) {
            setVisibility(GONE);
            removeAllViews();
            return;
        }
        setVisibility(VISIBLE);

        //清空原有数据，重新添加新数据
        mStringList.clear();
        mStringList.addAll(stringList);
        generateChildrenLayout(mStringList.size());//根据图片数量设置行列

        if (!mIsFirst) {
            notifyDataSetChanged();
        }
    }

    /**
     * 更新数据
     */
    public void notifyDataSetChanged() {
        removeAllViews();//移除所有子控件

        int size = getListSize(mStringList);//图片数量
        if (size > 0) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }

        if (size == 1) {
            String String = mStringList.get(0);
            ImageView imageView = createImageView(0, String);

            //避免在ListView中一张图未加载成功时，布局高度受其他item影响
            LayoutParams params = getLayoutParams();
            params.height = mSingleWidth * 2 / 3;
            setLayoutParams(params);
            imageView.layout(0, 0, mSingleWidth, mSingleWidth);

            boolean isShowDefualt = displayOneImage(imageView, String, mTotalWidth);
            if (isShowDefualt) {
                layoutImageView(imageView, 0, String, false);
            } else {
                addView(imageView);
            }
            return;
        }

        generateChildrenLayout(size);//根据图片数量设置行列

        layoutParams();//确定控件高度

        for (int i = 0; i < size; i++) {
            String String = mStringList.get(i);
            ImageView imageView;
            if (!mIsShowAll) {
                if (i < MAX_COUNT - 1) {//前8张
                    imageView = createImageView(i, String);//创建新ImageView
                    layoutImageView(imageView, i, String, false);//ImageView绘制
                } else { //第9张时
                    if (size <= MAX_COUNT) {//刚好第9张
                        imageView = createImageView(i, String);
                        layoutImageView(imageView, i, String, false);
                    } else {//超过9张
                        imageView = createImageView(i, String);
                        layoutImageView(imageView, i, String, true);
                        break;
                    }
                }
            } else {
                imageView = createImageView(i, String);
                layoutImageView(imageView, i, String, false);
            }
        }
    }

    /**
     * 确定控件高度
     */
    private void layoutParams() {
        Point displaySize = DisplayUtils.getDisplaySize(getContext());
//        LayoutParams params = getLayoutParams();
//        //根据子view数量确定高度
//        if (showNumber == 2) {
//            params.height = displaySize.x / 3 - DisplayUtils.dip2px(getContext(), 16);
//        } else {
//            params.height = (displaySize.x / 3 - DisplayUtils.dip2px(getContext(), 16)) * 2 / 3;
//        }

        int singleHeight = mSingleWidth;

        //根据子view数量确定高度
        LayoutParams params = getLayoutParams();
//        params.height = displaySize.x * 2 / 9;
        if (showNumber == 1) {
            params.height = mSingleWidth * 2 / 3;
        } else {
            params.height = (int) (singleHeight * mRows + mSpacing * (mRows - 1));
        }
        setLayoutParams(params);
    }

    /**
     * 创建新ImageView
     *
     * @param i
     * @param string
     * @return
     */
    private ImageView createImageView(final int i, final String string) {
        MyRectangleView imageView = new MyRectangleView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getContext()).load(string).into(imageView);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreviewUtlis.setMutliImage(getContext(), mStringList, i);

                if (mOnClickImageListener != null) {
                    mOnClickImageListener.onClick(i, string);
                }

                if (onClickLayoutListener != null) {
                    onClickLayoutListener.onClick(i, mStringList);
                }
            }
        });
        return imageView;
    }

    /**
     * @param imageView
     * @param String
     * @param showNumFlag 是否在最大值的图片上显示还有未显示的图片张数
     */
    private void layoutImageView(ImageView imageView, int i, String String, boolean showNumFlag) {
        int singleWidth = (int) ((mTotalWidth - mSpacing * (mColumns - 1)) / mColumns);
        int singleHeight = singleWidth;

        if (showNumber == 1) {
            singleHeight = singleWidth * 2 / 3;
        }

        int[] position = findPosition(i);
        int left = (int) ((singleWidth + mSpacing) * position[1]);
        int top = (int) ((singleHeight + mSpacing) * position[0]);
        int right = left + singleWidth;
        int bottom = top + singleHeight;

        imageView.layout(left, top, right, bottom);

        addView(imageView);
        if (showNumFlag) {//添加超过最大显示数量的文本
            int overCount = getListSize(mStringList) - MAX_COUNT;
            if (overCount > 0) {
                float textSize = 30;
                final TextView textView = new TextView(mContext);
                textView.setText("+" + String.valueOf(overCount));
                textView.setTextColor(Color.WHITE);
                textView.setPadding(0, singleHeight / 2 - getFontHeight(textSize), 0, 0);
                textView.setTextSize(textSize);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundColor(Color.BLACK);
                textView.getBackground().setAlpha(120);

                textView.layout(left, top, right, bottom);
                addView(textView);
            }
        }
        displayImage(imageView, String);
    }

    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mColumns; j++) {
                if ((i * mColumns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }

    /**
     * 根据图片个数确定行列数量
     *
     * @param length
     */
    private void generateChildrenLayout(int length) {
        if (length == 1) {
            mRows = 1;
            mColumns = 1;
        } else if (length == 2) {
            mColumns = 2;
            mRows = 1;
        } else if (length == 4) {
            mColumns = 2;
            mRows = 2;
        } else {
            mColumns = 3;
            if (mIsShowAll) {
                mRows = length / 3;
                int b = length % 3;
                if (b > 0) {
                    mRows++;
                }
            } else {
                mRows = (int) Math.ceil((double) length / 3);
                mRows = mRows >= 3 ? 3 : mRows;
            }
        }
    }

    protected void setOneImageLayoutParams(ImageView imageView, int width, int height) {
        imageView.setLayoutParams(new LayoutParams(width, height));
        imageView.layout(0, 0, width, height);

        LayoutParams params = getLayoutParams();
//        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }

    private int getListSize(List<String> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

    private int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * @param imageView
     * @param String
     * @param parentWidth 父控件宽度
     * @return true 代表按照九宫格默认大小显示，false 代表按照自定义宽高显示
     */
    public boolean displayOneImage(ImageView imageView, String String, int parentWidth) {
        return true;
    }

    ;

    public void displayImage(ImageView imageView, String String) {

    }


    private OnClickImageListener mOnClickImageListener;

    public void setOnClickImageListener(OnClickImageListener onClickImageListener) {
        mOnClickImageListener = onClickImageListener;
    }

    public interface OnClickImageListener {
        void onClick(int position, String string);
    }

    private OnClickLayoutListener onClickLayoutListener;

    public void setOnClickLayoutListener(OnClickLayoutListener onClickLayoutListener) {
        this.onClickLayoutListener = onClickLayoutListener;
    }

    public interface OnClickLayoutListener {
        void onClick(int position, List<String> mStringList);
    }

    //加载完成后重新设置高度
    public ViewGroup.LayoutParams setRefreshLayoutParams(Context context, int length) {
        Point displaySize = DisplayUtils.getDisplaySize(context);
        int mColumns = 1;
        int mRows = 1;
        if (length == 1) {
            mRows = 1;
            mColumns = 1;
        } else if (length == 2) {
            mColumns = 2;
            mRows = 1;
        } else if (length == 4) {
            mColumns = 2;
            mRows = 2;
        } else {
            mColumns = 3;
            mRows = (int) Math.ceil((double) length / 3);
            mRows = mRows >= 3 ? 3 : mRows;
        }
        int mSingleWidth = (int) ((displaySize.x - NineFreedomGridLayout.DEFUALT_SPACING * (mColumns - 1)) / mColumns);
        int height = (int) (mSingleWidth * mRows + NineFreedomGridLayout.DEFUALT_SPACING * (mRows - 1));
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (length == 1) {
            layoutParams.height = mSingleWidth * 2 / 3;
        } else {
            layoutParams.height = height;
        }
        return layoutParams;
    }
}

