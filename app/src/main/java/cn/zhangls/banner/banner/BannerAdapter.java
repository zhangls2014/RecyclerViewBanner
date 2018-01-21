package cn.zhangls.banner.banner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.zhangls.banner.R;

/**
 * 图片加载适配器
 *
 * @author zhangls
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private OnItemClickListener onItemClickListener;
    private List<String> urlList;
    private Context context;

    public BannerAdapter(Context context, @NonNull List<String> urlList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.urlList = urlList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BannerViewHolder holder, int position) {
        String url = urlList.get(position % urlList.size());
        ImageView img = holder.imageView;
        Glide.with(context).load(url).into(img);
        img.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.imageView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urlList == null ? 0 : urlList.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        BannerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

}
