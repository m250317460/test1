package com.example.danganguan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import constantandutil.CommonModel;
import constantandutil.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static com.example.danganguan.MainActivity.*;
import static constantandutil.Constant.URL_yuyue;

public class yuyueActivity extends BaseActivity implements View.OnClickListener {
    private TimePickerView pvTime;
    private TextView xztime;
    //折叠菜单栏
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<Item>> iData = null;
    private ArrayList<Item> lData = null;
    private Context mContext;
    private ExpandableListView exlist_lol;
    private MyBaseExpandableListAdapter myAdapter = null;
    private Group yuyuetype=new Group("预约类型");
    private Group dangantype=new Group("档案类型");
    private TitleLayout2 titlelayout;
    private TextView righticon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuyue);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        righticon = findViewById(R.id.right_icon);
        titlelayout.setRightview(true);
        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();
        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }


    }
    protected void onStart(){
        super.onStart();
        Button yyqueren=findViewById(R.id.yyqueren);
        yyqueren.setOnClickListener(this);
        TitleLayout2 titleLayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        titleLayout.setTitle("提前预约");
        xztime = findViewById(R.id.xztime);
        righticon.setOnClickListener(this);
        xztime.setOnClickListener(this);
        initTimePicker();           //时间选择器
        mContext = yuyueActivity.this;
        exlist_lol = (ExpandableListView) findViewById(R.id.exlist_lol);

        //数据准备
        gData = new ArrayList<Group>();
        iData = new ArrayList<ArrayList<Item>>();

        gData.add(yuyuetype);
        gData.add(dangantype);

        lData = new ArrayList<Item>();

        //AD组
        lData.add(new Item("取走档案"));
        lData.add(new Item("查询档案"));
        iData.add(lData);
        //AP组
        lData = new ArrayList<Item>();
        lData.add(new Item("成绩单"));
        lData.add(new Item("个人学历"));
        lData.add(new Item("新生录取名册"));
        lData.add(new Item("团关系"));
        lData.add(new Item("党关系"));
        iData.add(lData);

        myAdapter = new MyBaseExpandableListAdapter(gData,iData,mContext);
        exlist_lol.setAdapter(myAdapter);


        //为列表设置点击事件
        exlist_lol.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //把相应那一栏的名字替换
                if (groupPosition==0){
                    yuyuetype.setgName(iData.get(groupPosition).get(childPosition).getiName());
                    gData.set(groupPosition,yuyuetype);
                }else if (groupPosition==1){
                    dangantype.setgName(iData.get(groupPosition).get(childPosition).getiName());
                    gData.set(groupPosition,dangantype);
                }
                Toast.makeText(mContext, "你点击了：" + iData.get(groupPosition).get(childPosition).getiName(), Toast.LENGTH_SHORT).show();
                //选择完之后关闭菜单
                for (int i=0;i<3;i++){
                    exlist_lol.collapseGroup(i);
                }
                return true;
            }
        });
    }

    private void initTimePicker() {//Dialog 模式下，在底部弹出

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                xztime.setText(getTime(date));
                Toast.makeText(yuyueActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
                Log.i("pvTime", "onTimeSelect");

            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                .isDialog(true)
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.xztime:
                pvTime.show();
                break;
            case R.id.yyqueren:
                //发送消息给服务器，两种方式，String 或者 json，这里先不谈，数据内容分别是
                //获取两个组名，获取一个textview的text
                //尝试post方法传送过去，两个可以用一个表，然后在后面的类型里面标注服务的区别，然后把这个表的内容
                //解析成string模式定期发给老师的账号.
                //获取两个组名和一个textview
                String g1=gData.get(0).getgName(); String g2=gData.get(1).getgName();
                String time=xztime.getText().toString();
                if (!(g1=="预约类型"||g2=="档案类型"||time=="选择时间")){
                    yuyuequeren(g1,g2,time);
                }else {
                    Toast.makeText(yuyueActivity.this,"内容不完整",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.right_icon:
                startActivity(new Intent(yuyueActivity.this,jijianActivity.class));
        }

    }

    private void yuyuequeren(final String g1, final String g2, final String time) {
        //发送网络请求，附带三个参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //用于获取response
                    OkHttpClient client=new OkHttpClient();
                    //创建一个request,包含账号和预约信息
                    Request request=new Request.Builder()
                            .url(URL_yuyue+"?number="+number+"&g1="+g1+"&g2="+g2+"&time="+time)
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
                                        Toast.makeText(yuyueActivity.this,"连接超时，请稍后重试",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            if (e instanceof ConnectException) {
                                ////判断连接异常，
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(yuyueActivity.this,"连接错误，请检查您的网络",Toast.LENGTH_SHORT).show();
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(yuyueActivity.this,model.message+",已有"+(Integer.parseInt(model.count)-1)+"个同学预约",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //正确，返回一个吐司。取消一键返回
                            }else if(model.code==400){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(yuyueActivity.this,model.message+"，请注意时间有效性",Toast.LENGTH_SHORT).show();
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
