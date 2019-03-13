package com.example.danganguan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;

import constantandutil.CommonModel;
import constantandutil.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static constantandutil.Constant.URL_Login;
import static constantandutil.Constant.URL_Register;

public class RegistActivity extends BaseActivity implements View.OnClickListener{
    //需要定义4个et来获取其中的值
    private EditText etpassword,etidcard,etnumber,etname;
    private TitleLayout2 titlelayout;
    BorderRelativeLayout borderRelativeLayout1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        titlelayout.setTitle("注册");
        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();
        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        etidcard=findViewById(R.id.etidcard);
        etpassword=findViewById(R.id.etpassword);
        etname=findViewById(R.id.etname);
        etnumber=findViewById(R.id.etnumber);
        findViewById(R.id.regist).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        //点击注册之后发送网络请求
        if (etidcard.getText().toString()==""||etpassword.getText().toString()==""||etnumber.getText().toString()==""||etname.getText().toString()=="")
        {
            Toast.makeText(RegistActivity.this,"注册表内容不完整",Toast.LENGTH_SHORT);
        }else {
        regist(etidcard.getText().toString(),etpassword.getText().toString(),
                etnumber.getText().toString(),etname.getText().toString());
    }}
    private void regist(final String idcard, final String password, final String number, final String name){
                    //用于获取response
                    OkHttpClient client = new OkHttpClient();
                    //该操作完成搭建request
        Request request = null;
            request = new Request.Builder()
                    //访问本服务器的RegistServlet,并且附上账号密码
                    .url(URL_Register+"?number="+number+"&password="+md5(password)+"&idcard="+idcard+"&name="+ /*URLEncoder.encode(URLEncoder.encode(name,"UTF-8"),"UTF-8")*/name)
                    .build();
        //该操作用于发送网络请求
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (e instanceof SocketTimeoutException) {
                                //判断超时异常
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegistActivity.this, "连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            if (e instanceof ConnectException) {
                                ////判断连接异常，
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegistActivity.this, "连接错误，请检查您的网络", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //可以将其转为json然后获取其中的值
                            final String reponseData = response.body().string();
                            //以上都是可以正常的就下面这个model出错了
                            final CommonModel model = GsonUtil.fromJson(reponseData, CommonModel.class);
                            /*服务器端code200代表注册成功，code100代表注册失败*/
                            if (model.code == 200) {
                                //返回吐司提示。
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegistActivity.this, model.message, Toast.LENGTH_SHORT).show();
                                    }

                                });
                                startActivity(new Intent(RegistActivity.this,LoginActivity.class));
                            } else if (model.code == 100) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegistActivity.this, model.message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else if (model.code==300){
                                //暂不进行处理
                            }
                        }
                    });
            }
        }

