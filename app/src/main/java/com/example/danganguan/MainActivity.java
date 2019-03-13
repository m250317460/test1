package com.example.danganguan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.signature.StringSignature;
import java.util.ArrayList;
import java.util.List;
/*
本项目中把所有的布局文件，图片文件，以及文本文件放在了主视图中，在没有设置监听器的情况下就可以显示出来大概的布局样子
之后设置监听器，点击之后使用transcation.show()进行显示事务
项目步骤
1.主活动设置好整体框架
2.为每个分活动设置fragmentlayout文件
3.创建对应的fragment类
4.在主活动中设置监听按钮点击事件并且加载对应页面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private beginFragment beginFragment;            //首页碎片
    private operateFragment operateFragment;        //操作碎片
    private mineFragment mineFragment;              //我的碎片
    private List<View> bottomTabs;                  //底部标签列表
    private View beginLayout;                       //首页布局视图
    private View operateLayout;                     //操作布局视图
    private View mineLayout;                        //我的布局属兔
    private ImageView beginImage;                   //开始图片
    private ImageView operateImage;                 //操作图片
    private ImageView mineImage;                    //我的图片
    private TextView beginText;                     //首页文本
    private TextView operateText;                   //操作文本
    private TextView mineText;                      //我的文本
    private FragmentManager fragmentManager;        //碎片管理器
    public static boolean isLogined;
    private TitleLayout titlelayout;
    public static String number;
    public static String path;
    public static String password;
    public static boolean changed=false;
    public static StringSignature signature=new StringSignature("1");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = this.getSharedPreferences("loginfo",MODE_PRIVATE);
        isLogined = sharedPreferences.getBoolean("Logined",false);
        if (!isLogined) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_main);
            //获取用户登陆信息
            number=sharedPreferences.getString("number","");
            path=sharedPreferences.getString("image","");
            password=sharedPreferences.getString("password","");
            //用于隐藏标题栏
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            titlelayout=(TitleLayout)findViewById(R.id.titlelayout1);
            View paddingView = findViewById(R.id.paddingView);
            ViewGroup.LayoutParams params = paddingView.getLayoutParams();
            params.height = getStatusBarHeight();
            //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
            }
            initViews();
            fragmentManager = getSupportFragmentManager();
            setSelectTab(0);
        }
    }
    private void initViews() {
        //布局视图通过寻找主布局中的各个relativelayout
        beginLayout = findViewById(R.id.begin);
        operateLayout = findViewById(R.id.operate);
        mineLayout = findViewById(R.id.mine_layout);
        //Item的图标，通过寻找主布局中各个relativelayout中的imageView锁定
        beginImage = (ImageView) findViewById(R.id.begin_danganguan_image);
        operateImage = (ImageView) findViewById(R.id.operate_danganguan_image);
        mineImage = (ImageView) findViewById(R.id.mine_danganguan_image);
        //item的文字，通过寻找主布局中的textview锁定
        beginText = (TextView) findViewById(R.id.begin_danganguan_text);
        operateText = (TextView) findViewById(R.id.operate_text);
        mineText = (TextView) findViewById(R.id.mine_danganguan_text);
        //各个视图的监听器
        beginLayout.setOnClickListener(this);
        operateLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);


        //底部标签设置大小之后加入各个视图（在定义的时候已经确定泛型为view）
        bottomTabs = new ArrayList<>(5);
        bottomTabs.add(beginLayout);
        bottomTabs.add(operateLayout);
        bottomTabs.add(mineLayout);
    }
    private void setSelectTab(int index) {
        //初始化选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        //这里实现设置监听器的切换

        switch (index) {
            case 0:
                beginImage.setImageResource(R.drawable.begin22);
                beginText.setTextColor(Color.parseColor("#00c98d"));
                titlelayout.setTitle("首页");

                if (beginFragment == null) {
                    beginFragment = new beginFragment();
                    transaction.add(R.id.content, beginFragment);
                } else {
                    transaction.show(beginFragment);
                }
                break;
            case 1:
                operateImage.setImageResource(R.drawable.operate2);
                operateText.setTextColor(Color.parseColor("#00c98d"));
                titlelayout.setTitle("功能");
                if (operateFragment == null) {
                    operateFragment = new operateFragment();
                    transaction.add(R.id.content, operateFragment);
                } else {
                    transaction.show(operateFragment);
                }
                break;
            case 2:
                mineImage.setImageResource(R.drawable.mine);
                mineText.setTextColor(Color.parseColor("#00c98d"));
                titlelayout.setTitle("我的");
                if (mineFragment == null) {
                    mineFragment = new mineFragment();
                    transaction.add(R.id.content, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.begin){
            setSelectTab(0);
        }else if (v.getId()==R.id.operate){
            setSelectTab(1);
        }else if (v.getId()==R.id.mine_layout){
            setSelectTab(2);
        }

        /*switch (v.getId()) {
            case R.id.begin:
                setSelectTab(0);
                break;
            case R.id.operate:
                setSelectTab(1);
                break;
            case R.id.mine_layout:
                setSelectTab(2);
                break;


            default:
                break;
        }*/
    }
    //该方法表示未选中的时候主布局的状态，文字为灰色，图片为未选中状态
    private void clearSelection() {
        beginImage.setImageResource(R.drawable.begin2);
        beginText.setTextColor(Color.parseColor("#82858b"));
        operateImage.setImageResource(R.drawable.operate);
        operateText.setTextColor(Color.parseColor("#82858b"));
        mineImage.setImageResource(R.drawable.mine2);
        mineText.setTextColor(Color.parseColor("#82858b"));
    }
    private void hideFragments(FragmentTransaction transaction) {
        if (beginFragment != null) {
            transaction.hide(beginFragment);
        }
        if (operateFragment != null) {
            transaction.hide(operateFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }
}