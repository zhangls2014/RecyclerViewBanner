package cn.zhangls.banner.banner;

import android.view.View;

/**
 * 自定义 RecyclerView Item 点击事件接口
 *
 * @author zhangls
 */
public interface OnItemClickListener {

    /**
     * 点击事件
     *
     * @param view     点击视图
     * @param position 位置
     */
    void onItemClick(View view, int position);
}
