package com.meizu.lizhi.mygraduation.main.student.account;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.flyme.reflect.StatusBarProxy;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.CurrentUser;
import com.meizu.lizhi.mygraduation.operation.GetUserSpinnerInfo;
import com.meizu.lizhi.mygraduation.operation.Operate;
import com.meizu.lizhi.mygraduation.operation.ToastUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AccountActivity extends Activity implements View.OnClickListener{


    ImageView mImageViewPhoto;
    TextView mTextViewEmail;
    EditText mEditTextName;
    Spinner mSpinnerSex;
    Spinner mSpinnerSchool;
    Spinner mSpinnerAcademy;
    EditText mEditTextDescribe;

    ImageView mImageViewSave;

    RelativeLayout mRelativeLayoutCustom;

    String photo;
    String email;
    String name;
    int sex;
    String school;
    String academy;
    String detail;

    ActionBar mActionBar;
    private int mActionBarOptions;

    RequestQueue queue;

    Bitmap mBitmap;

    Bitmap initBitmap;

    static final String getUserInfoUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getUserInfo";
    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/uploadUserData";

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    mImageViewPhoto.setImageBitmap(initBitmap);
                }
            }
            super.handleMessage(msg);
        }
    };

    void initView(){
        mActionBar=getActionBar();
        mImageViewPhoto= (ImageView) findViewById(R.id.imagePhoto);
        mTextViewEmail= (TextView) findViewById(R.id.email);
        mEditTextName= (EditText) findViewById(R.id.nameEdit);
        mSpinnerSex= (Spinner) findViewById(R.id.sex);
        mSpinnerSchool= (Spinner) findViewById(R.id.school);
        mSpinnerAcademy= (Spinner) findViewById(R.id.academy);
        mEditTextDescribe= (EditText) findViewById(R.id.describeEdit);
        mRelativeLayoutCustom= (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.account_detail_custom_view,null);
        mImageViewSave= (ImageView) mRelativeLayoutCustom.findViewById(R.id.save);
        mActionBar.setCustomView(mRelativeLayoutCustom);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
        setContentView(R.layout.activity_account);
        queue = Volley.newRequestQueue(this);
        initView();
        initUserInfo();
        mImageViewPhoto.setOnClickListener(this);
        mImageViewSave.setOnClickListener(this);
    }

    public void initUserInfo(){
        StringRequest getUserInfoRequest = new StringRequest(Request.Method.POST, getUserInfoUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject obj = new JSONObject(s);
                            int code = obj.getInt("code");
                            int result = obj.getInt("result");
                            if (code == 54) {
                                switch (result) {
                                    case 0: {
                                         JSONObject data=obj.getJSONObject("data");
                                         final String photo=data.getString("photo");
                                         if(photo.equals("empty")){
                                             mImageViewPhoto.setBackgroundResource(R.drawable.icon_photo);
                                         }else{
                                             new Thread(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     loadImage("http://" + StaticIp.IP + ":8080/graduationServlet/photo/user/" + photo);
                                                     Message message=new Message();
                                                     message.what=1;
                                                     mHandler.sendMessage(message);
                                                 }
                                             }).start();
                                         }
                                         mTextViewEmail.setText(data.getString("email"));
                                        mEditTextName.setText(data.getString("name"));
                                        mSpinnerSex.setSelection(data.getInt("sex"));
                                        GetUserSpinnerInfo getUserSpinnerInfo=new GetUserSpinnerInfo();
                                        mSpinnerSchool.setSelection(getUserSpinnerInfo.getSchoolPosition(data.getString("school")));
                                        mSpinnerAcademy.setSelection(getUserSpinnerInfo.getAcademyPosition(data.getString("academy")));
                                        mEditTextDescribe.setText(data.getString("detail").equals("empty")?"":data.getString("detail"));
                                    }
                                    break;
                                    case 1: {
                                        ToastUtils.showToast(AccountActivity.this,"服务器出错,请您稍候再试!");
                                    }
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtils.showToast(AccountActivity.this, "网络链接出了点小问题，请您检查检查网络");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject json=new JSONObject();
                try {
                    json.put("code",54);
                    JSONObject value=new JSONObject();
                    value.put("id",CurrentUser.getCurrentUserId(AccountActivity.this));
                    json.put("data",value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json", json.toString().trim());
                return map;
            }
        };
        queue.add(getUserInfoRequest);
    }

    public void loadImage(String url) {
        URL myFileUrl;
        HttpURLConnection connection;
        try {
            myFileUrl = new URL(url);
            connection = (HttpURLConnection) myFileUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            initBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mActionBar.setDisplayOptions(mActionBarOptions, ActionBar.DISPLAY_SHOW_CUSTOM | mActionBarOptions);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mActionBarOptions = mActionBar.getDisplayOptions();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save:{
                if(checkInfo()==true){
                    saveUserPhoto();
                    uploadUserData();
                }
            }break;
            case R.id.imagePhoto:{
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        }
    }

    public String getJson() {
        JSONObject info = new JSONObject();
        try {
            info.put("code", 52);
            JSONObject value = new JSONObject();
            value.put("user", CurrentUser.getCurrentUserId(this));
            value.put("photo", photo);
            value.put("name", name);
            value.put("sex",sex);
            value.put("school",school);
            value.put("academy",academy);
            value.put("detail", detail);
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }

    public void uploadUserData(){
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "上传中...");
        new AsyncTask<Void, Void, HttpResponse>() {
            @Override
            protected HttpResponse doInBackground(Void... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 500);  //请求超时
                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 500);   //读取超时
                    HttpPost post = new HttpPost(actionUrl);
                    File file = new File("/sdcard/graduation/userPhoto/" + photo);
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
                        if(code==52){
                            switch (result){
                                case 0:{
                                    ToastUtils.showToast(AccountActivity.this, "资料修改成功");
                                }break;
                                case 1:{
                                    ToastUtils.showToast(AccountActivity.this,"服务器出错,请您稍候再试!");
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showToast(AccountActivity.this, "网络链接出了点小问题，请您检查检查网络");
                }
            }
        }.execute();
    }


    boolean checkInfo(){
        if(mBitmap==null){
            ToastUtils.showToast(AccountActivity.this,"必须上传头像！");
            return false;
        }
        email=mTextViewEmail.getText().toString().trim();
        name=mEditTextName.getText().toString().trim();
        if(name.length()<2){
            ToastUtils.showToast(AccountActivity.this,"昵称在2～7个字符之间");
            return false;
        }
        if(mSpinnerSex.getSelectedItem().toString().trim().equals("选择")){
            sex=0;
        }else if(mSpinnerSex.getSelectedItem().toString().trim().equals("男")){
            sex=1;  //男
        }else{
            sex=2;  //女
        }

        school=mSpinnerSchool.getSelectedItem().toString().trim();
        academy=mSpinnerAcademy.getSelectedItem().toString().trim();
        detail=mEditTextDescribe.getText().toString().trim();
        detail=detail.equals("")?"empty":detail;
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(data!=null) {
                    startPhotoZoom(data.getData());
                }
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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
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
        }
    }

    public void saveUserPhoto() {
        photo = Operate.getSystemTime()+ ".jpg";
        File dir = new File("/sdcard/graduation/userPhoto");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File("/sdcard/graduation/userPhoto/", photo);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
