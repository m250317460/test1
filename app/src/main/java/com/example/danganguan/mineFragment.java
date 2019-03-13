package com.example.danganguan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dell on 2018/7/18.
 */

public class mineFragment extends Fragment implements View.OnClickListener{
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    TextView name;
    TextView number;
    View writeLayout;
    BorderRelativeLayout wl,tc,gaimi;
    //private TitleLayout titleLayout;
    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         writeLayout = inflater.inflate(R.layout.mine_layout,
                container, false);
        sharedPreferences=mineFragment.this.getActivity().getSharedPreferences("loginfo",MODE_PRIVATE);
        writeLayout.findViewById(R.id.yonghuImage).setOnClickListener(this);

        name=(TextView) writeLayout.findViewById(R.id.yonghuText);
        number = writeLayout.findViewById(R.id.yonghuPho);
        writeLayout.findViewById(R.id.wuliu).setOnClickListener(this);
        writeLayout.findViewById(R.id.gaitou).setOnClickListener(this);
        writeLayout.findViewById(R.id.gaini).setOnClickListener(this);
        writeLayout.findViewById(R.id.gaimi).setOnClickListener(this);
        writeLayout.findViewById(R.id.myyy).setOnClickListener(this);
        gaimi=writeLayout.findViewById(R.id.gaimi);
        gaimi.setNeedBottomBorder(false);
        wl=writeLayout.findViewById(R.id.wuliu);
        wl.setNeedBottomBorder(false);
        tc=writeLayout.findViewById(R.id.tc);
        tc.setNeedBottomBorder(false);
        writeLayout.findViewById(R.id.tc).setOnClickListener(this);
        return writeLayout;
    }
    public void onResume(){
        super.onResume();
            //使用Glide框架设置图片,path 为url
            ImageView yonghuImage=writeLayout.findViewById(R.id.yonghuImage);
            //判断是否是默认路径
            if (sharedPreferences.getString("image","")!="http://101.201.236.162:8080/Danganguan/imgs/null"){
            Glide.with(this).load(sharedPreferences.getString("image",""))
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    //.skipMemoryCache(true)
                    .centerCrop()
                    //.error(R.drawable.icon_head)
                   // .placeholder(R.drawable.icon_head)
                    .dontAnimate()
                    .placeholder(R.drawable.wmine)
                    .error(R.drawable.operate)
                    .signature(MainActivity.signature)
                    .transform(new CircleCrop(this.getContext()))
                    .into(yonghuImage);
            }
        name.setText( sharedPreferences.getString("name",""));
        number.setText(sharedPreferences.getString("number",""));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gaitou:
                //执行更改信息活动
                Intent intent=new Intent(mineFragment.this.getContext(),GaitouActivity.class);
                intent.putExtra("number",MainActivity.number);
                startActivity(intent);
                break;
            case R.id.gaini:
                startActivity(new Intent(mineFragment.this.getContext(),GainiActivity.class)
                        .putExtra("number",MainActivity.number));
                break;
            case R.id.gaimi:
                startActivity(new Intent(mineFragment.this.getContext(),GaimiActivity.class)
                        .putExtra("number",MainActivity.number));
                break;
            case R.id.tc:
                editor=mineFragment.this.getActivity().getSharedPreferences("loginfo",MODE_PRIVATE).edit();
                editor.putBoolean("Logined",false);
                editor.apply();
                startActivity(new Intent(mineFragment.this.getContext(),LoginActivity.class));
                this.getActivity().finish();
                break;
            case R.id.myyy:
                startActivity(new Intent(mineFragment.this.getContext(),MyyyActivity.class));
                break;
            case R.id.wuliu:
                startActivity(new Intent(mineFragment.this.getContext(),wuliuActivity.class));
                break;
                default:
                    break;
        }

    }
}
