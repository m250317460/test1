package com.example.danganguan;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by dell on 2018/7/21.
 */

public class TitleLayout2 extends LinearLayout {
    private TextView tv_title;
    TextView righticon;
    public TitleLayout2(Context context) {
        super(context,null);
    }
    public TitleLayout2(final Context context, AttributeSet attrs) {
        super(context, attrs);
        //引入布局
        LayoutInflater.from(context).inflate(R.layout.title_bar2,this);
        ImageView btnBack=(ImageView)findViewById(R.id.tv_exit);
        righticon = findViewById(R.id.right_icon);
        //Button btnEdit=(Button)findViewById(R.id.btnEdit);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)getContext()).finish();
            }
        });
        tv_title=(TextView)findViewById(R.id.tv_title);
    }
    //显示活的的标题
    public void setTitle(String title)
    {
        if(!TextUtils.isEmpty(title))
        {
            tv_title.setText(title);
        }
    }
    public void setRightview(boolean a){
        if (a == true){
            righticon.setVisibility(View.VISIBLE);
        }else
            righticon.setVisibility(View.INVISIBLE);
    }

}
