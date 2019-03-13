package News;

import android.graphics.Bitmap;

/**
 * Created by dell on 2018/7/29.
 * 新闻实体类，表示新闻对象
 * news content frag是新闻内容的布局
 */

public class News {
    private String title;
    private String content;
    private String time;
    private String id;
    private String img;
    private Bitmap img1;

    public String getImg() {
        return img;
    }

    public Bitmap getImg1() {
        return img1;
    }

    public void setImg1(Bitmap img1) {
        this.img1 = img1;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public String getId(){return id;}
    public String getTime(){return time;}
    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setTime(String time){this.time=time;}
    public void setId(String id){this.id=id;}
}
