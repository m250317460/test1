package com.example.danganguan;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import constantandutil.CommonModel;
import constantandutil.GsonUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static constantandutil.Constant.*;

public class GaimiActivity extends BaseActivity implements View.OnClickListener{
    private EditText etgaimi;
    private EditText etgaimi2;
    private EditText etjiumi;
    private String number;
    private TitleLayout2 titlelayout;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaimi);
        Intent intent=getIntent();
        number=intent.getStringExtra("number");
        password=MainActivity.password;
        etgaimi=findViewById(R.id.etgaimi);
        etgaimi2=findViewById(R.id.etgaimi2);
        etjiumi=findViewById(R.id.etjiumi);
        findViewById(R.id.Gaimi).setOnClickListener(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        titlelayout.setTitle("更改密码");
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
        //点击确定之后就从Edittext中获取数据，传入后台，并且改变前端显示
        gaimi(etjiumi.getText().toString(),etgaimi.getText().toString(),etgaimi2.getText().toString());
    }
    private void gaimi(String jiumi,final String mima,String mima2) {
        //先判断旧密码一不一样
        if (jiumi.equals(password)){
            if (mima.equals(mima2)){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //用于获取response
                    OkHttpClient client = new OkHttpClient();
                    //该操作完成搭建request
                    Request request=new Request.Builder()
                    //访问本服务器的ChangemmServlet,并且附上账号密码
                            .url(URL_gaimi+"?mima="+md5(mima)+"&number="+number)
                            .build();
                    //该操作用于发送网络请求
                    Response response=client.newCall(request).execute();
                    final String reponseData=response.body().string();
                    final CommonModel model= GsonUtil.fromJson(reponseData,CommonModel.class);
                    if (model.code==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GaimiActivity.this,model.message,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GaimiActivity.this,"两次新密码不一致",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GaimiActivity.this,"原密码不正确",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
