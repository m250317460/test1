package com.example.danganguan;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import News.NewsContentFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static constantandutil.Constant.URL2_newsmsg;

/*
* 新闻内容界面
* */
public class NewsContentActivity extends BaseActivity {
    private TitleLayout2 titlelayout;
    private String newscontent;
    public static void actionStart(Context context, String newsTitle, String newsId){
        Intent intent=new Intent(context,NewsContentActivity.class);
        intent.putExtra("news_title",newsTitle);
        intent.putExtra("news_id",newsId);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content);
        //---------------------------------------------------------------------------
        //该部分为了处理状态栏和标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        titlelayout.setTitle("新闻详情");
        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();

        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        //------------------------------------------------------------------------



        final String newsTitle=getIntent().getStringExtra("news_title");//获取传入的新闻标题
        final String newsid=getIntent().getStringExtra("news_id");//获取传入的新闻ID
        final NewsContentFragment newsContentFragment=
                (NewsContentFragment)getSupportFragmentManager().findFragmentById(R.id.news_content_fragment);
        //本行代码用于刷新fragment界面，在这里需要再次发送网络请求，发送新闻id，请求获取新闻内容
        // newsContentFragment.refresh(newsTitle,newsContent);//刷新fragment界面
        //用于获取response
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
                //该操作完成搭建request
        Request request = new Request.Builder()
                //根据跳转活动获取本新闻的id
                .url(URL2_newsmsg + "?id=" + newsid)
                .build();
        //该操作用于发送网络请求,返回一个json对象
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(responseData);
                    //这里需要设置newscontent
                    newscontent=jsonObject.getString("content");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newsContentFragment.refresh(newsTitle,newscontent);//刷新fragment界面
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });



    }
}
