package com.example.danganguan;

        import android.content.Intent;
        import android.content.SharedPreferences;

        import android.os.Build;
        import android.support.v7.app.ActionBar;
        import android.os.Bundle;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.net.ConnectException;
        import java.net.SocketTimeoutException;
        import java.net.URLDecoder;
        import java.net.URLEncoder;
        import java.util.concurrent.TimeUnit;

        import constantandutil.CommonModel;
        import constantandutil.GsonUtil;
        import okhttp3.Call;
        import okhttp3.Callback;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.Response;

        import static constantandutil.Constant.URL_Login;

public class LoginActivity extends com.example.danganguan.BaseActivity implements View.OnClickListener{
    private SharedPreferences.Editor editor;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        com.example.danganguan.TitleLayout titlelayout=(com.example.danganguan.TitleLayout)findViewById(R.id.titlelayout1);
        titlelayout.setTitle("登陆");
        //三个按钮用于相应事件
        Button button = (Button) findViewById(R.id.login);
        button.setOnClickListener(this);
        findViewById(R.id.btnzhuce).setOnClickListener(this);
        findViewById(R.id.btnwangji).setOnClickListener(this);

        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
    }
    @Override
    public void onClick(View view) {
        //按钮点击事件，三个，如果点击了登陆则从后台获取账号密码
        //如果点击了注册，则跳转到注册页面，
        //如果点击了忘记密码，则跳转到忘记密码页面，前期先用身份证号码来进行验证，后期尝试发送手机短信
        switch (view.getId()){
            case R.id.login:
                //先判断再修改状态
                //从后台获取的信息一致，手机号和密码，则跳转，并且修改状态
                // URL_Login = URL + "/LoginServlet";
                //两个text用于获取账号密码
                EditText etshoujihao=findViewById(R.id.etshoujihao);
                EditText etmima=findViewById(R.id.etmima);
                login(etshoujihao.getText().toString(),etmima.getText().toString());
                break;
            case R.id.btnwangji:
                //跳转到忘记密码页面
                startActivity(new Intent(LoginActivity.this, com.example.danganguan.wangjiActivity.class));
                break;
            case R.id.btnzhuce:
                //跳转到注册界面
                startActivity(new Intent(LoginActivity.this, com.example.danganguan.RegistActivity.class));
                break;
        }
    }
    private void login(final String number, final String password) {
        //开启新线程来发起网络请求
        //用于获取response
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        //该操作完成搭建request
        Request request = new Request.Builder()
                //访问本服务器的LoginServlet,并且附上账号密码 get请求
                .url(URL_Login + "?number=" + number + "&password=" + md5(password))
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
                            Toast.makeText(LoginActivity.this,"连接超时，请稍后重试",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (e instanceof ConnectException) {
                    //判断连接异常，
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,"连接错误，请检查您的网络",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //可以将其转为json然后获取其中的值
                final String reponseData=response.body().string();
                //以上都是可以正常的就下面这个model出错了
                final CommonModel model= GsonUtil.fromJson(reponseData,CommonModel.class);
                if (model.code==200){
                    //返回吐司提示。
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,model.message,Toast.LENGTH_SHORT).show();
                        }
                    });
                    String i = URLDecoder.decode(model.data.name,"UTF-8");
                    editor = getSharedPreferences("loginfo", MODE_PRIVATE).edit();
                    editor.putBoolean("Logined", true);
                    editor.putString("number",number);
                    editor.putString("name", model.data.name/*URLDecoder.decode(URLDecoder.decode(model.data.name,"utf-8"),"utf-8")*/);
                    editor.putString("image",model.data.path);
                    editor.putString("password",password);
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, com.example.danganguan.MainActivity.class));
                    finish();
                }else if (model.code==100){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,model.message,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });}}