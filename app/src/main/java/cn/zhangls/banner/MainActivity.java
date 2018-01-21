package cn.zhangls.banner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import cn.zhangls.banner.banner.BannerRecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BannerRecyclerView bannerRecyclerView = findViewById(R.id.banner);

        final ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("http://imgsrc.baidu.com/forum/pic/item/d4c907f3d7ca7bcbcf643fcabe096b63f724a804.jpg");
        arrayList.add("http://d.hiphotos.baidu.com/zhidao/pic/item/7c1ed21b0ef41bd5e6c559a057da81cb38db3dcb.jpg");
        arrayList.add("http://a.hiphotos.baidu.com/zhidao/pic/item/d52a2834349b033b5b349bfe16ce36d3d539bd51.jpg");
        bannerRecyclerView.initBannerImageView(arrayList, (view, position) -> {
            Toast.makeText(this, "====position=====" + position, Toast.LENGTH_SHORT).show();
        });
    }
}
