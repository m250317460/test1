package com.example.danganguan;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class hdzxActivity extends BaseActivity {
    private TitleLayout2 titlelayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdzx);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        titlelayout.setTitle("互动咨询");
        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();
        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
    }
}
