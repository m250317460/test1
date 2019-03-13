package com.example.danganguan;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by dell on 2018/7/21.
 */

public class TitleLayout extends LinearLayout {
    private TextView tv_title;
    public TitleLayout(Context context) {
        super(context,null);
    }

    public TitleLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        //引入布局
        LayoutInflater.from(context).inflate(R.layout.title_bar,this);
        //Button btnBack=(Button)findViewById(R.id.btnBack);
        //Button btnEdit=(Button)findViewById(R.id.btnEdit);
        /*btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)getContext()).finish();
            }
        });

        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"编辑",Toast.LENGTH_SHORT).show();
            }
        });*/
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

}
