package News;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.danganguan.BaseActivity;
import com.example.danganguan.R;
import com.example.danganguan.TitleLayout2;

/**
 * Created by dell on 2018/7/29.
 */

public class NewsContentFragment extends Fragment {
    private View view;
    private TitleLayout2 titlelayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //新闻内容的布局
        view=inflater.inflate(R.layout.news_content_frag,container,false);
        return view;
    }
    public void refresh(String newsTitle,String newsContent){
        View visibilityLayout=view.findViewById(R.id.visibility_layout);
        visibilityLayout.setVisibility(View.VISIBLE);
        TextView newsTitleText=(TextView)view.findViewById(R.id.news_title);
        TextView newsContentText=(TextView)view.findViewById(R.id.news_content);
        newsTitleText.setText(newsTitle);   //刷新新闻标题
        newsContentText.setText(newsContent);   //刷新新闻内容
        newsContentText.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}
