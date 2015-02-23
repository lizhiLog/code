package com.meizu.lizhi.mygraduation.main.teacher.subject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.internet.StaticIp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;

public class CreateSubjectActivity extends Activity implements View.OnClickListener {

    ImageView mImageViewPhoto;
    EditText mEditTextSubjectName;
    EditText mEditTextSubjectDescribe;
    Button mButtonSubmit;

    Bitmap mBitmap;

    long userId=7;
    String subjectPhotoName;
    String subjectName;
    String subjectDescribe;

    static final String TAG = CreateSubjectActivity.class.getName();

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/createSubject";

    long getSystemTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    void initView() {
        mImageViewPhoto = (ImageView) findViewById(R.id.image);
        mEditTextSubjectName = (EditText) findViewById(R.id.subjectName);
        mEditTextSubjectDescribe = (EditText) findViewById(R.id.describe);
        mButtonSubmit = (Button) findViewById(R.id.submit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_subject);
        initView();
        mImageViewPhoto.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image: {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.submit: {

                if (checkInfo() == false) {
                    return;
                }
                Log.e(TAG, (mBitmap == null) + "");
                saveSubjectPhoto();
                createSubject();
            }
        }
    }

    public boolean checkInfo() {
        subjectName = mEditTextSubjectName.getText().toString().trim();
        subjectDescribe = mEditTextSubjectDescribe.getText().toString().trim();
        if (mBitmap == null || subjectName.equals("") || subjectDescribe.equals("")) {
            Toast.makeText(CreateSubjectActivity.this, "课程图像/课程名称/课程描述 都不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String getJson() {
        JSONObject info = new JSONObject();
        try {
            info.put("code", 32);
            JSONObject value = new JSONObject();
            value.put("author",userId);
            value.put("photo", subjectPhotoName);
            value.put("name", subjectName);
            value.put("describe", subjectDescribe);
            value.put("time",getSystemTime());
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }

    public void createSubject() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "上传中...");
        new AsyncTask<Void, Void, HttpResponse>() {
            @Override
            protected HttpResponse doInBackground(Void... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);  //请求超时
                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);   //读取超时
                    HttpPost post = new HttpPost(actionUrl);
                    File file = new File("/sdcard/graduation/subjectPhoto/" + subjectPhotoName);
                    MultipartEntity multiPart = new MultipartEntity();
                    multiPart.addPart("json", new StringBody(getJson(), Charset.forName("utf-8")));
                    multiPart.addPart("file", new FileBody(file));
                    post.setEntity(multiPart);
                    HttpResponse response = client.execute(post);
                    return response;
                } catch (Exception e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(HttpResponse response) {
                progressDialog.dismiss();
                if (response != null) {
                    HttpEntity entity=response.getEntity();
                    try {
                        String json= EntityUtils.toString(entity);
                        JSONObject jsonObject=new JSONObject(json);
                        int code=jsonObject.getInt("code");
                        int result=jsonObject.getInt("result");
                        if(code==32){
                            switch (result){
                                case 0:{
                                    Toast.makeText(CreateSubjectActivity.this, "课程创建成功", Toast.LENGTH_SHORT).show();
                                }break;
                                case 1:{
                                    Toast.makeText(CreateSubjectActivity.this, "服务器出了点小问题，请您稍候再试试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CreateSubjectActivity.this, "网络链接出了点小问题，请您检查检查网络", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                startPhotoZoom(data.getData());
                break;
            case 2:
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 1000);
        intent.putExtra("outputY", 1000);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    private void setPicToView(Intent picData) {
        Bundle extras = picData.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            mImageViewPhoto.setImageBitmap(mBitmap);
            Log.e(TAG, mBitmap.getWidth() + ":" + mBitmap.getHeight());
        }
    }

    public void saveSubjectPhoto() {
        subjectPhotoName = getSystemTime() + ".jpg";
        Log.e(TAG, "name:" + subjectPhotoName);
        File dir = new File("/sdcard/graduation/subjectPhoto");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File("/sdcard/graduation/subjectPhoto/", subjectPhotoName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.e(TAG, (mBitmap == null) + "");
        Log.e(TAG, (fOut == null) + "fOut");
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
