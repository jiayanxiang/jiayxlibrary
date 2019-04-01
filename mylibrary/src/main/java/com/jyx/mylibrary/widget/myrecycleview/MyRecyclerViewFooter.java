package com.jyx.mylibrary.widget.myrecycleview;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyx.mylibrary.R;
import com.jyx.mylibrary.utils.ScreenUtil;
import com.jyx.mylibrary.utils.ValuesGainUtil;


/**
 * Created by limxing on 16/7/23.
 * <p>
 * https://github.com/limxing
 * Blog: http://www.leefeng.me
 */
public class MyRecyclerViewFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;
    private static final String TAG = "LFRecyclerViewFooter";

    private Context mContext;
    private int mState;
    private View mContentView;
    private View mProgressBar;
    private TextView mHintView;
    private RelativeLayout lfrecyclerview_footer_state;
    private LoadView lfrecyclerview_footer_loadview;

    public MyRecyclerViewFooter(Context context) {
        super(context);
        initView(context);
    }

    public MyRecyclerViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public int getmState() {
        return mState;
    }


    public void setState(int state) {
        mState = state;
        mHintView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mHintView.setVisibility(View.INVISIBLE);
        if (state == STATE_READY) {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.lfrecyclerview_footer_hint_ready);
        } else if (state == STATE_LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(GONE);
            mHintView.setVisibility(View.GONE);
            mHintView.setText(R.string.lfrecyclerview_footer_hint_normal);
        }
    }

    public void setBottomMargin(int height) {
        if (height < 0) return;
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        return lp.bottomMargin;
    }


    /**
     * normal status
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }


    /**
     * loading status
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        mContentView.setVisibility(View.GONE);
    }

    /**
     * show footer
     */
    public void show() {
        mContentView.setVisibility(View.VISIBLE);
    }

    private void initView(Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.lfrecyclerview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        lfrecyclerview_footer_state = (RelativeLayout) moreView.findViewById(R.id.lfrecyclerview_footer_state);
        lfrecyclerview_footer_state.setVisibility(View.GONE);
        mContentView = moreView.findViewById(R.id.lfrecyclerview_footer_content);
        mProgressBar = moreView.findViewById(R.id.lfrecyclerview_footer_progressbar);
        mHintView = (TextView) moreView.findViewById(R.id.lfrecyclerview_footer_hint_textview);
        mHintView.setVisibility(GONE);
        lfrecyclerview_footer_loadview = (LoadView) moreView.findViewById(R.id.lfrecyclerview_footer_loadview);
        lfrecyclerview_footer_state.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public TextView getmHintView() {
        return mHintView;
    }

    /**
     * 设置是否显示有无数据有好提示
     *
     * @param isNone isNone
     */
    public void setNoneDataState(boolean isNone) {
        if (isNone) {
            lfrecyclerview_footer_state.setVisibility(View.VISIBLE);
            setNoDateView(R.layout.my_recycle_view_empty);
        } else {
            lfrecyclerview_footer_state.setVisibility(View.GONE);
        }

    }

    public void setNoDateView(View view) {
        lfrecyclerview_footer_state.removeAllViews();
        lfrecyclerview_footer_state.addView(view);
    }

    public void setNoDateView(int layoutId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = ScreenUtil.getScreenHeight(getContext()) / 4;
        view.setLayoutParams(layoutParams);
        lfrecyclerview_footer_state.removeAllViews();
        lfrecyclerview_footer_state.addView(view);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
//        mContext = null;
//        mContentView = null;
//        mProgressBar = null;
//        mHintView = null;
//        lfrecyclerview_footer_state = null;
//        lfrecyclerview_footer_loadview = null;

    }

    public void setOnNodataViewListener(OnClickListener listener) {
        lfrecyclerview_footer_state.setFocusable(true);
        lfrecyclerview_footer_state.setClickable(true);
        lfrecyclerview_footer_state.setOnClickListener(listener);
    }

    public void setContentViewBackground(int color){
        mContentView.setBackgroundColor(ValuesGainUtil.getInstance().getValuesColor(getContext(),color));
    }
}
