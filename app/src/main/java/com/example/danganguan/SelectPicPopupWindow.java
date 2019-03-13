package com.example.danganguan;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import constantandutil.CommonModel;
import constantandutil.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static constantandutil.Base64.bitmapToBase64;
import static constantandutil.Constant.URL_gaini;
import static constantandutil.Constant.URL_gaitou;
import static constantandutil.Constant.pathbase;

public class SelectPicPopupWindow extends Activity implements OnClickListener {
    //用来表示子活动
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Uri imageUri;
    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout layout;
    private SharedPreferences.Editor editor;
    private static String base;
    public static int sign_num=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
        btn_pick_photo = (Button) findViewById(R.id.btn_pick_photo);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        picture=findViewById(R.id.yonghuImage);
        layout = (LinearLayout) findViewById(R.id.pop_layout);

        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity   
        layout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub   
                Toast.makeText(SelectPicPopupWindow.this, "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //添加按钮监听   
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
    }
    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity   需要啥我帮忙点的就QQ给我说
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                takephoto();
                break;
            case R.id.btn_pick_photo:
                //运行时权限处理，如果同意则进行openAlbm
                if (ContextCompat.checkSelfPermission(SelectPicPopupWindow.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SelectPicPopupWindow.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbm();
                }
                break;
            case R.id.btn_cancel:
                break;
            default:
                break;
        }
        finish();
    }

    private void takephoto() {
        //创建file对象用于存储拍照后的对象
        //第一个参数表示应用关联缓存目录，第二个参数表示图片名字
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //接着判断设备的系统版本是否小于7.0，如果小于就调用else方法，把file对象转换成uri对象，标识着这张图片
        //的真是路径，否则调用另一个方法把file转换成一个封装好的uri对象，
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(SelectPicPopupWindow.this, "com.example.danganguan.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //指定照片的输出地址,填入uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //第二个参数如果》=0则当activity结束后把requestcode返回给onar中，以便标识返回的目标activity
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void openAlbm() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbm();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //第一个与start中的第二个参数相对应，
        //第二个由子活动的setresult方法返回，用于表示是哪个子活动返回的
        //第三个用来获取父活动传来的数据
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        //转化为bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4以及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以及一下版本
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    //一下3个方法是处理图片得到他的uripath
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);

            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，则直接获取图片路径
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真是的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(final String imagePath) {
        if (imagePath!=null){
            editor = getSharedPreferences("loginfo", MODE_PRIVATE).edit();
            editor.putString("imagepath",imagePath);
            editor.apply();
            //在改头页面显示
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            base=bitmapToBase64(bitmap);
            //把base64码发送
            //给服务器
            sendbitmapurl(base);
            picture.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }


    }

    private void sendbitmapurl(final String base64) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //用于获取response
                    OkHttpClient client=new OkHttpClient();
                    //该操作完成搭建request
                    //发送base64数组，用于保存图片
                    RequestBody requestBody=new FormBody.Builder()
                            .add("base64",base64)
                            .build();
                    Request request=new Request.Builder()
                            .url(URL_gaitou+"?number="+MainActivity.number)
                            .post(requestBody)
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
                                        Toast.makeText(SelectPicPopupWindow.this,"连接超时，请稍后重试",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            if (e instanceof ConnectException) {
                                ////判断连接异常，
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SelectPicPopupWindow.this,"连接错误，请检查您的网络",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //可以将其转为json然后获取其中的值
                            final String reponseData=response.body().string();
                            final CommonModel model= GsonUtil.fromJson(reponseData,CommonModel.class);
                            if (model.code==200){
                                //设置共享空间中的path
                                //返回在服务器的位置
                                //更改签名
                                getSharedPreferences("loginfo",MODE_PRIVATE).edit()
                                        .putString("image",pathbase+model.data.path);
                                sign_num++;
                                MainActivity.signature=new StringSignature(String.valueOf(sign_num));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SelectPicPopupWindow.this,model.message,Toast.LENGTH_SHORT).show();
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