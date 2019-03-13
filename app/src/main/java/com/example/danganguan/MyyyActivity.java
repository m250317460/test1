package com.example.danganguan;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import News.Myyy;
import News.News;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static constantandutil.Constant.URL2_myyy;
import static constantandutil.Constant.URL2_newslist;

public class MyyyActivity extends BaseActivity {
    RecyclerView recyclerView;
    yyAdapter adapter;
    private List<Myyy> myyyList=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private TitleLayout2 titlelayout;
    SwipeRefreshLayout  swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myyy);
        sharedPreferences=this.getSharedPreferences("loginfo",MODE_PRIVATE);
        recyclerView=findViewById(R.id.myyylist);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new yyAdapter(getYy());
        recyclerView.setAdapter(adapter);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        titlelayout.setTitle("我的预约");
        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();
        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        swipeRefreshLayout=findViewById(R.id.mySRlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new yyAdapter(getYy());
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private List<Myyy> getYy() {

        //发送网络请求
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        //该操作完成搭建request
        Request request = new Request.Builder()
                //根据跳转活动获取本新闻的id
                //这里写报文
                .url(URL2_myyy + "?phonum=" + sharedPreferences.getString("number",""))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    Looper.prepare();
                    Toast.makeText(MyyyActivity.this,"连接超时，请稍后重试",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    Looper.prepare();
                    Toast.makeText(MyyyActivity.this,"连接错误，请检查您的网络",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();

                if (responseData!="[]"){
                //返回一个json数组
                JSONArray jsonArray= null;
                try {
                    jsonArray = new JSONArray(responseData);

                    //循环获取数组中的值，并赋值给list
                    int i=0;
                    while(i<jsonArray.length()){
                        JSONObject jsonObject= null;
                        jsonObject = jsonArray.getJSONObject(i);
                        //这里需要news的标题和时间，可能需要id
                        Myyy news=new Myyy();
                        //这里设置参数
                        news.setNo(jsonObject.getString("No"));
                        news.setPhonum(jsonObject.getString("phonum"));
                        news.setTime(jsonObject.getString("time"));
                        news.setType1(jsonObject.getString("type1"));
                        news.setType2(jsonObject.getString("type2"));
                        myyyList.add(news);
                        i++;
                    }
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }else {
                    Myyy myyy = new Myyy();
                    myyy.setNo("您还未预约");
                    myyyList.add(myyy);
                }
            }
        });
        return myyyList;
    }

    class yyAdapter extends RecyclerView.Adapter<yyAdapter.ViewHolder>{
        private List<Myyy> myyyList;
        public yyAdapter(List<Myyy> list) {
            this.myyyList = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView yyText;
            public ViewHolder(View itemView) {
                super(itemView);
                yyText=itemView.findViewById(R.id.yy_title);
            }
        }

        @Override
        public yyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myyy_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }


        public void onBindViewHolder(yyAdapter.ViewHolder holder, int position) {
            Myyy yy=myyyList.get(position);
            holder.yyText.setText(yy.getNo()+" "+yy.getType1()+" "+yy.getType2()+" "+yy.getTime());
        }

        @Override
        public int getItemCount() {
            return myyyList.size();
        }

    }

}
