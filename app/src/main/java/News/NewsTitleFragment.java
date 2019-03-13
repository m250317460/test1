package News;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.danganguan.GlideImageLoader;
import com.example.danganguan.NewsContentActivity;
import com.example.danganguan.R;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.v7.widget.RecyclerView.*;
import static constantandutil.Constant.URL2_newslist;


/**
 * Created by dell on 2018/7/17. 这是一个通知的栏，
 * 用于展示新闻列表的碎片
 * 现在要做的是给recyclerview加一个header,需要分析一下上一个适配器写法的错误
 */

public class NewsTitleFragment extends Fragment {
    private boolean isTwoPane;
    private static int page=1;
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    private Banner banner;
    private SwipeRefreshLayout swipeRefreshLayout;
    static List<News> newsList=new ArrayList<>();
    RecyclerView newsTitleRecyclerView;
    NewsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState){
        //加载新闻列表的布局
         final View view=inflater.inflate(R.layout.news_title_frag,container,false);
        //创建recyclerView对象,并且赋值为news_title_frag内部的recycler_view，即本java对应的布局文件
        newsTitleRecyclerView=(RecyclerView)view.findViewById(R.id.news_title_recycler_view);

        //线性布局管理器，获取当前fragment以来的活动对象
        //newsTitleRecyclerView.addItemDecoration(new SpaceItemDecoration(0,30));
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        newsTitleRecyclerView.setLayoutManager(layoutManager);//把newsTitleView的布局设置为线性布局

        adapter=new NewsAdapter(getNews());
        newsTitleRecyclerView.setAdapter(adapter);
//在这里实现轮播图，目前暂定固定图片，放到服务器上之后可以从服务器申请url
        images.add("http://101.201.236.162/xmgkzl/newsimg/msjg.jpg");
        images.add("http://101.201.236.162/xmgkzl/newsimg/tm.jpg");
        images.add("http://101.201.236.162/xmgkzl/newsimg/jqr.jpg");
        images.add("http://101.201.236.162/xmgkzl/newsimg/ztbg.jpg");
        images.add("http://101.201.236.162/xmgkzl/newsimg/tsg.jpg");
        titles.add("韩旭教授名师讲堂");
        titles.add("我校与浙大开展战略合作");
        titles.add("首届中国认知学术大会开始啦");
        titles.add("对于毕业要求的理解");
        titles.add("校园美景");
        banner = view.findViewById(R.id.banner);
        //设置样式,默认为:Banner.NOT_INDICATOR(不显示指示器和标题)
        //可选样式如下:
        //1. Banner.CIRCLE_INDICATOR    显示圆形指示器
        //2. Banner.NUM_INDICATOR   显示数字指示器
        //3. Banner.NUM_INDICATOR_TITLE 显示数字指示器和标题
        //4. Banner.CIRCLE_INDICATOR_TITLE  显示圆形指示器和标题
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置轮播样式（没有标题默认为右边,有标题时默认左边）
        //可选样式:
        //Banner.LEFT   指示器居左
        //Banner.CENTER 指示器居中
        //Banner.RIGHT  指示器居右
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置轮播要显示的标题和图片对应（如果不传默认不显示标题）
        banner.setBannerTitles(titles);
        //设置是否自动轮播（不设置则默认自动）
        banner.isAutoPlay(true);
        //设置轮播图片间隔时间（不设置默认为2000）
        banner.setDelayTime(5000);
        //设置图片资源:可选图片网址/资源文件，默认用Glide加载,也可自定义图片的加载框架
        //所有设置参数方法都放在此方法之前执行
        //banner.setImages(images);
        banner.setImageLoader(new GlideImageLoader());
        //自定义图片加载框架
        banner.setImages(images);
        //设置点击事件，下标是从1开始
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                //Toast.makeText(, "你点击了" + position, Toast.LENGTH_SHORT).show();
            }
        });
        banner.start();
        swipeRefreshLayout=view.findViewById(R.id.SRlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NewsAdapter adapter1 = new NewsAdapter(getNews());
                newsTitleRecyclerView.setAdapter(adapter1);
                swipeRefreshLayout.setRefreshing(false);
             }
        });
        return view;
    }
    //用于得到新闻事例,并添加到新闻列表中
        //
        // 开启新线程进行网络通信
        // 在这里实现从数据库获取新闻内容
        // 需要获取id,time,以及标题

    private List<News> getNews(){
            //用于获取response
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            //该操作完成搭建request
            Request request = new Request.Builder()
                    //根据跳转活动获取本新闻的id
                    .url(URL2_newslist + "?p=" + page)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (e instanceof SocketTimeoutException) {
                        //判断超时异常
                        Looper.prepare();
                        Toast.makeText(getContext(),"连接超时，请稍后重试",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    if (e instanceof ConnectException) {
                        ////判断连接异常，
                        Looper.prepare();
                        Toast.makeText(getContext(),"连接错误，请检查您的网络",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    //返回一个json数组
                    JSONArray jsonArray= null;
                    try {
                        jsonArray = new JSONArray(responseData);
                    //循环获取数组中的值，并赋值给list
                    for (int i = 0;i<10;i++){
                        JSONObject jsonObject= null;
                        jsonObject = jsonArray.getJSONObject(i);
                        //这里需要news的标题和时间，可能需要id
                        News news=new News();
                        news.setTime(jsonObject.getString("time"));
                        news.setTitle(jsonObject.getString("title"));
                        news.setId(jsonObject.getString("id"));
                        news.setImg(jsonObject.getString("img"));
                        newsList.add(news);
                    }
                }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
    return newsList;
    }
    //用于设置随机长度的内容
    @Override
    //用于创建活动时候的动作，确定活动采用哪种格式
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
        //获取从Activity中传递过来每个item的数据集合
        private List<News> mNewsList;


        public NewsAdapter(List<News> list) {
            this.mNewsList = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    News news = mNewsList.get(holder.getAdapterPosition());
                    NewsContentActivity.actionStart(getActivity(),news.getTitle(),news.getId());
                }
            });
            return holder;
        }
        //绑定View，这里是根据返回的这个position的类型，从而进行绑定的，   HeaderView和FooterView, 就不同绑定了
        @Override
        public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
                News news=mNewsList.get(position);
                holder.newsTitleText.setText(news.getTitle());
                holder.newsTimeText.setText(news.getTime());
            Glide.with(getActivity()).load(news.getImg())
                    .into(holder.newsImage);
        }
        @Override
        public int getItemCount() {
            return mNewsList.size();
        }
        //返回正确的item个数
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView newsTitleText;
            TextView newsTimeText;
            Banner banner;
            ImageView newsImage;

            public ViewHolder(View itemView) {
                super(itemView);
                banner =itemView.findViewById(R.id.banner);
                newsTitleText=(TextView)itemView.findViewById(R.id.news_title);
                newsTimeText=itemView.findViewById(R.id.news_time);
                newsImage = itemView.findViewById(R.id.news_img);
            }
        }
    }



}
