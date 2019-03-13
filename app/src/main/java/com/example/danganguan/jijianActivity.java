package com.example.danganguan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import bean.JsonBean;
import constantandutil.CommonModel;
import constantandutil.GetJsonDataUtil;
import constantandutil.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.danganguan.MainActivity.number;
import static constantandutil.Constant.URL_jijian;

public class jijianActivity extends BaseActivity implements View.OnClickListener{
    TitleLayout2 titleLayout;    //设置标题
    /*折叠菜单栏*/
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<Item>> iData = null;
    private ArrayList<Item> lData = null;
    private Context mContext;
    private ExpandableListView exlist_lol;
    private MyBaseExpandableListAdapter myAdapter = null;
    /*
    地址选择栏目
    * */
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;
    //普通选项栏
    private EditText name;
    private EditText phonum;
    private TextView area;
    private EditText xxarea;
    //文件类型栏
    static Group jjtype=new Group("选择文件类型");
    private TitleLayout2 titlelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jijian);
        //用于隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        titlelayout=(TitleLayout2)findViewById(R.id.titlelayout1);
        View paddingView = findViewById(R.id.paddingView1);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();
        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        name=findViewById(R.id.etname);
        phonum=findViewById(R.id.etphonum);
        xxarea=findViewById(R.id.xxarea);
        area=findViewById(R.id.etarea);
        area .setOnClickListener(this);
        findViewById(R.id.jjqueren).setOnClickListener(this);

        //设置地区选择
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        initView();
        //设置标题
        titleLayout= (TitleLayout2) findViewById(R.id.titlelayout1);
        titleLayout.setTitle("建立收货地址");
        /*设置选项*/
        mContext = jijianActivity.this;
        exlist_lol = (ExpandableListView) findViewById(R.id.exlist_lol);

        //数据准备
        gData = new ArrayList<Group>();
        iData = new ArrayList<ArrayList<Item>>();
        gData.add(jjtype );

        lData = new ArrayList<Item>();

        //AD组
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
                //需要把组名变一下
                jjtype.setgName(iData.get(groupPosition).get(childPosition).getiName());
                //Toast.makeText(mContext, "你点击了：" + iData.get(groupPosition).get(childPosition).getiName(), Toast.LENGTH_SHORT).show();
                exlist_lol.collapseGroup(0);
                return true;
            }
        });

    }
    //用于判断是否解析完成json数据
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        //Toast.makeText(jijianActivity.this, "Begin Parse Data", Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    //Toast.makeText(jijianActivity.this, "Parse Succeed", Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(jijianActivity.this, "Parse Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    //名字是初始化视图，但是我没有发现那个属兔需要初始化
    private void initView() {
        findViewById(R.id.area).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        /*if (isLoaded) {
            showPickerView();
        } else {
            Toast.makeText(jijianActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
        }*/
        switch (v.getId()){
            case R.id.etarea:
            case R.id.area:
                showPickerView();
                break;
            case R.id.jjqueren:
                //发送消息给服务器，两种方式，String 或者 json，这里先不谈，数据内容分别是
                //四个textview以及一个组名，现在尝试post方法，把数据放到数据库中，response返回是否成功
                if (name.getText().toString()==""||phonum.getText().toString()==""||area.getText().toString()==""
                        ||xxarea.getText().toString()==""||jjtype.getgName()=="选择文件类型"){
                    Toast.makeText(jijianActivity.this,"内容不完整",Toast.LENGTH_SHORT).show();
                }else {
                    jjqueren(name.getText().toString(),phonum.getText().toString(),area.getText().toString()
                            ,xxarea.getText().toString(),jjtype.getgName());
                }


                break;
        }
    }

    private void jjqueren(final String name, final String phonum, final String area, final String xxarea,final String jjtype) {
        new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                   //创建client用于接收response
                   OkHttpClient client=new OkHttpClient();
                   //用于创建request
                   Request request=new Request.Builder()
                           .url(URL_jijian+"?number="+number+"&name="+name
                                   +"&phonum="+phonum+"&area="+area+"&xxarea="+xxarea
                           +"&jjtype="+jjtype)
                           .build();
                   client.newCall(request).enqueue(new Callback() {
                       @Override
                       public void onFailure(Call call, IOException e) {
                           if (e instanceof SocketTimeoutException) {
                               //判断超时异常
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Toast.makeText(jijianActivity.this,"连接超时，请稍后重试",Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }
                           if (e instanceof ConnectException) {
                               ////判断连接异常，
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Toast.makeText(jijianActivity.this,"连接错误，请检查您的网络",Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }
                       }

                       @Override
                       public void onResponse(Call call, Response response) throws IOException {
                           final String reponseData=response.body().string();
                           final CommonModel model= GsonUtil.fromJson(reponseData,CommonModel.class);
                           if (model.code==200){
                               //正确，返回一个吐司。取消一键返回
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Toast.makeText(jijianActivity.this,model.message,Toast.LENGTH_SHORT).show();
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


    private void showPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                area.setText(tx);
                Toast.makeText(jijianActivity.this, tx, Toast.LENGTH_SHORT).show();
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }
    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
