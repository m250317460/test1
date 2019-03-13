package com.example.danganguan;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import constantandutil.CommonModel;
import constantandutil.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static constantandutil.Constant.URL2_wjmm;
import static constantandutil.Constant.URL_gaimi;

public class wangjiActivity extends BaseActivity implements View.OnClickListener,wangji{
    private TitleLayout2 titlelayout;
    private EditText etzhanghao;
    private EditText etidc;
    private EditText etnewpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wangji);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        titlelayout.setTitle("忘记密码");
        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();
        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        etidc=findViewById(R.id.etidc);
        etzhanghao=findViewById(R.id.etzhanghao);
        etnewpass=findViewById(R.id.etnewpass);
        findViewById(R.id.wangji).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.wangji){
        wangji(etzhanghao.getText().toString(),etidc.getText().toString());}
        }

    private void wangji(String zhanghao, String idc) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(URL2_wjmm+"?num="+zhanghao+"&idc="+idc)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(wangjiActivity.this, "连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(wangjiActivity.this, "连接错误，请检查您的网络", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String reponseData = response.body().string();
                final CommonModel model = GsonUtil.fromJson(reponseData, CommonModel.class);
                if (model.code == 200) {
                    wangji(etnewpass.getText().toString());
                }else if (model.code==502){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(wangjiActivity.this, "身份证号不匹配", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if (model.code==401){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(wangjiActivity.this, "账号不存在", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void wangji(String mima) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(URL_gaimi+"?mima="+md5(mima)+"&number="+etzhanghao.getText().toString())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(wangjiActivity.this, "连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(wangjiActivity.this, "连接错误，请检查您的网络", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String reponseData = response.body().string();
                final CommonModel model = GsonUtil.fromJson(reponseData, CommonModel.class);
                if (model.code == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(wangjiActivity.this,model.message,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
