package com.example.danganguan;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;

import static constantandutil.Constant.*;

import constantandutil.CommonModel;
import constantandutil.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GainiActivity extends BaseActivity implements View.OnClickListener{
    private EditText etgaini;
    private String number;
    private TitleLayout2 titlelayout;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaini);
        editor = getSharedPreferences("loginfo", MODE_PRIVATE).edit();
        number=getIntent().getStringExtra("number");
        etgaini=findViewById(R.id.etgaini);
        findViewById(R.id.Gaini).setOnClickListener(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        titlelayout.setTitle("更改昵称");
        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();
        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
    }

    @Override
    public void onClick(View view) {
        gaini(etgaini.getText().toString());
    }

    private void gaini(final String nicheng) {
        //先在sp里面修改再发送网络请求
        editor.putString("name", nicheng);
        editor.apply();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //用于获取response
                    OkHttpClient client=new OkHttpClient();
                    //该操作完成搭建request
                    Request request=new Request.Builder()
                            .url(URL_gaini+"?nicheng="+ nicheng/*URLEncoder.encode(URLEncoder.encode(nicheng,"UTF-8"),"UTF-8")*/+"&number="+number)
                            .build();
                    //该操作用于发送网络请求得到回应
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (e instanceof SocketTimeoutException) {
                                //判断超时异常
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GainiActivity.this,"连接超时，请稍后重试",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            if (e instanceof ConnectException) {
                                ////判断连接异常，
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GainiActivity.this,"连接错误，请检查您的网络",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String reponseData=response.body().string();
                            final CommonModel model= GsonUtil.fromJson(reponseData,CommonModel.class);
                            if (model.code==200){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GainiActivity.this,model.message,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });


                    }catch (Exception e){
                        e.printStackTrace();
                    }

            }
        }).start();
    }
}
