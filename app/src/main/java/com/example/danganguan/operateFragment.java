package com.example.danganguan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by dell on 2018/7/18.
 */
public class operateFragment extends Fragment implements View.OnClickListener{
    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View writeLayout = inflater.inflate(R.layout.operate_layout,
                container, false);
        //ImageView jijianimage=(ImageView)writeLayout.findViewById(R.id.jijianImage);
        TextView jijiantext=(TextView)writeLayout.findViewById(R.id.jijianText);
        //ImageView yuyueimage=(ImageView)writeLayout.findViewById(R.id.yuyueImage);
        TextView yuyuetext=(TextView)writeLayout.findViewById(R.id.yuyueText);
        TextPaint tp=jijiantext.getPaint();
        tp.setFakeBoldText(true);
        tp=yuyuetext.getPaint();
        tp.setFakeBoldText(true);
        RelativeLayout xxggLayout = writeLayout.findViewById(R.id.xxggLayout);
        xxggLayout.setBackgroundResource(R.drawable.xxgg);
        writeLayout.findViewById(R.id.xxggLayout).setOnClickListener(this);
        writeLayout.findViewById(R.id.dayyLayout).setOnClickListener(this);
        writeLayout.findViewById(R.id.gdgsLayout).setOnClickListener(this);
        writeLayout.findViewById(R.id.hdzxLayout).setOnClickListener(this);
        //yuyueimage.setOnClickListener(this);
        //yuyuetext.setOnClickListener(this);
        //jijianimage.setOnClickListener(this);
        //jijiantext.setOnClickListener(this);
        return writeLayout;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gdgsLayout:
                startActivity(new Intent(operateFragment.this.getActivity(),gdgsActivity.class));
                break;
            case R.id.dayyLayout:
                Intent intent=new Intent(operateFragment.this.getActivity(),yuyueActivity.class);
                intent.putExtra("number",MainActivity.number);
                startActivity(intent);
                break;
            case R.id.xxggLayout:
                startActivity(new Intent(operateFragment.this.getActivity(),xxggActivity.class));
                break;
            case R.id.hdzxLayout:
                startActivity(new Intent(operateFragment.this.getActivity(),hdzxActivity.class));
                break;

        }
    }
}

