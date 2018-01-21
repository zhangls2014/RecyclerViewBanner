package cn.zhangls.banner.banner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

import cn.zhangls.banner.R;

/**
 * 自定义 banner 视图
 *
 * @author zhangls
 */
public final class BannerRecyclerView extends FrameLayout {

    /**
     * 图片 RecyclerView
     */
    protected RecyclerView mRecyclerView;
    /**
     * 指示器 RecyclerView
     */
    protected RecyclerView indicatorView;
    /**
     * 指示器选中图片
     */
    private Drawable mSelectedDrawable;
    /**
     * 指示器未选中图片
     */
    private Drawable mUnselectedDrawable;
    /**
     * 指示器适配器
     */
    private IndicatorAdapter indicatorAdapter;
    /**
     * 图片适配器
     */
    private BannerAdapter bannerAdapter;
    /**
     * 自动播放时间间隔，单位「秒」，默认值：3 秒
     */
    private int autoPlayInterval = 3;
    /**
     * 自动播放时间单位，秒
     */
    private static final int AUTO_PLAY_SYMBOL = 1000;
    /**
     * 自动播放消息常量
     */
    private static final int MSG_AUTO_PLAY = 1024;
    /**
     * 是否初始化图片数据的标识符
     */
    private boolean hasInit;
    /**
     * 图片数量
     */
    private int bannerSize = 1;
    /**
     * 当前页码
     */
    private int currentIndex;
    /**
     * 图片是否正在播放标识符，防止多次发送自动播放的 message，导致自动播放混乱
     */
    private boolean isPlaying;
    /**
     * Handle 发送延迟消息，实现自动播放
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_AUTO_PLAY) {
                // 自动播放逻辑：滚动到下一页，并切换指示器状态
                mRecyclerView.smoothScrollToPosition(++currentIndex % bannerSize);
                refreshIndicator();
                // 发送延迟消息
                mHandler.sendEmptyMessageDelayed(MSG_AUTO_PLAY, autoPlayInterval * AUTO_PLAY_SYMBOL);
            }
            return false;
        }
    });


    public BannerRecyclerView(Context context) {
        this(context, null);
    }

    public BannerRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化界面
     *
     * @param context 上下文对象
     */
    private void initView(Context context) {
        // 绘制默认选中状态图形
        GradientDrawable selectedGradientDrawable = new GradientDrawable();
        selectedGradientDrawable.setShape(GradientDrawable.OVAL);
        selectedGradientDrawable.setColor(getColor(R.color.colorAccent));
        selectedGradientDrawable.setSize(dp2px(5), dp2px(5));
        selectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
        mSelectedDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        // 绘制默认未选中状态图形
        GradientDrawable unSelectedGradientDrawable = new GradientDrawable();
        unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
        unSelectedGradientDrawable.setColor(getColor(R.color.colorPrimaryDark));
        unSelectedGradientDrawable.setSize(dp2px(5), dp2px(5));
        unSelectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
        mUnselectedDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});

        // 图片 RecyclerView 部分
        mRecyclerView = new RecyclerView(context);
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        LayoutParams vpLayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mRecyclerView, vpLayoutParams);
        // 指示器 RecyclerView 部分
        indicatorView = new RecyclerView(context);
        indicatorView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        indicatorAdapter = new IndicatorAdapter();
        indicatorView.setAdapter(indicatorAdapter);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        addView(indicatorView, params);
    }

    /**
     * 设置是否自动播放（上锁）
     *
     * @param playing 开始播放
     */
    private synchronized void setPlaying(boolean playing) {
        if (hasInit) {
            if (!isPlaying && playing && bannerAdapter != null && bannerAdapter.getItemCount() > 1) {
                mHandler.sendEmptyMessageDelayed(MSG_AUTO_PLAY, autoPlayInterval * AUTO_PLAY_SYMBOL);
                isPlaying = true;
            } else if (isPlaying && !playing) {
                mHandler.removeMessages(MSG_AUTO_PLAY);
                isPlaying = false;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            setPlaying(true);
        } else {
            setPlaying(false);
        }
    }

    /**
     * 改变导航的指示点
     */
    private synchronized void refreshIndicator() {
        if (bannerSize > 1) {
            indicatorAdapter.setPosition(currentIndex % bannerSize);
            indicatorAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 将 dp 数值的长度转换为 px 长度值
     *
     * @param dp dp 数值
     * @return px 数值
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 获取颜色
     */
    private int getColor(@ColorRes int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    /**
     * 标示点适配器
     */
    private class IndicatorAdapter extends RecyclerView.Adapter {

        int currentPosition = 0;

        void setPosition(int currentPosition) {
            this.currentPosition = currentPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ImageView bannerPoint = new ImageView(getContext());
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dp2px(3), dp2px(3), dp2px(3), dp2px(3));
            bannerPoint.setLayoutParams(lp);
            return new RecyclerView.ViewHolder(bannerPoint) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageView bannerPoint = (ImageView) holder.itemView;
            bannerPoint.setImageDrawable(currentPosition == position ? mSelectedDrawable : mUnselectedDrawable);
        }

        @Override
        public int getItemCount() {
            return bannerSize;
        }
    }


    /*
     * ==============================公开 api====================================
     */

    /**
     * 设置轮播数据集，初始化数据方法
     */
    public void initBannerImageView(@NonNull List<String> newList, OnItemClickListener onItemClickListener) {
        hasInit = false;
        setPlaying(false);
        bannerSize = newList.size();
        if (bannerSize > 1) {
            indicatorView.setVisibility(VISIBLE);
            currentIndex = Integer.MAX_VALUE / 2;
            mRecyclerView.scrollToPosition(currentIndex);
            bannerAdapter = new BannerAdapter(mRecyclerView.getContext(), newList, onItemClickListener);
            mRecyclerView.setAdapter(bannerAdapter);
            indicatorAdapter.notifyDataSetChanged();
            hasInit = true;
            setPlaying(true);
        } else {
            indicatorView.setVisibility(GONE);
            currentIndex = 0;
        }
    }

    /**
     * 设置自动播放时间间隔
     *
     * @param autoPlayInterval 自动播放时间间隔，单位「秒」，默认值：3 秒
     */
    public void setAutoPlayInterval(int autoPlayInterval) {
        this.autoPlayInterval = autoPlayInterval;
    }
}