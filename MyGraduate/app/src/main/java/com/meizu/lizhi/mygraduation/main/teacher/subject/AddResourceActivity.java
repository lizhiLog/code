package com.meizu.lizhi.mygraduation.main.teacher.subject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.ResourceData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.CurrentUser;
import com.meizu.lizhi.mygraduation.operation.Operate;
import com.meizu.lizhi.mygraduation.operation.SelectFileActivity;

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
import java.io.IOException;
import java.nio.charset.Charset;

public class AddResourceActivity extends Activity implements View.OnClickListener {


    RelativeLayout mRelativeLayoutSelect;
    EditText mEditTextPath;
    EditText mEditTextName;
    EditText mEditTextDescribe;
    Button mButtonUpload;

    long subjectId;
    int type;
    String title;
    String name;
    String detail;
    String filePath;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/uploadFile";

    void initView() {
        mEditTextPath = (EditText) findViewById(R.id.filePath);
        mButtonUpload = (Button) findViewById(R.id.upload);
        mRelativeLayoutSelect = (RelativeLayout) findViewById(R.id.selectFileLayout);
        mEditTextName = (EditText) findViewById(R.id.name);
        mEditTextDescribe = (EditText) findViewById(R.id.describe);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);
        Intent intent = getIntent();
        subjectId = intent.getLongExtra("subject", 0);
        type = intent.getIntExtra("type", 0);
        Log.e("yyy",type+"");
        initView();
        mButtonUpload.setOnClickListener(this);
        mRelativeLayoutSelect.setOnClickListener(this);
    }


    void uploadFile(){
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "上传中...");
        new AsyncTask<Void, Void, HttpResponse>() {
            @Override
            protected HttpResponse doInBackground(Void... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 50000);  //请求超时
                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 50000);   //读取超时
                    HttpPost post = new HttpPost(actionUrl);
                    MultipartEntity multiPart = new MultipartEntity();
                    multiPart.addPart("json", new StringBody(getJson(), Charset.forName("utf-8")));
                    File file=new File("/sdcard/graduation/uploadFile/"+name);
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
                        if(code==40){
                            switch (result){
                                case 0:{
                                    Toast.makeText(AddResourceActivity.this, "文件上传成功", Toast.LENGTH_SHORT).show();
                                    AddResourceActivity.this.finish();
                                }break;
                                case 1:{
                                    Toast.makeText(AddResourceActivity.this, "服务器出了点小问题，请您稍候再试试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AddResourceActivity.this, "网络链接出了点小问题，请您检查检查网络", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload: {
                if(filePath.equals("")){
                    Toast.makeText(AddResourceActivity.this,"请先选择文件",Toast.LENGTH_SHORT).show();
                    return;
                }else if(mEditTextName.getText().toString().trim().equals("")
                        ||mEditTextDescribe.getText().toString().trim().equals("")){
                    Toast.makeText(AddResourceActivity.this,"文件名词和描述不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    File file = new File(filePath);
                    if (file.exists() == false) {
                        Toast.makeText(AddResourceActivity.this, "您要上传的文件不存在", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        name=Operate.getSystemTime()+filePath.substring(filePath.lastIndexOf("."),filePath.length());
                        file.renameTo(new File(file.getParent()+"/"+name));
                        mButtonUpload.setEnabled(false);
                        title=mEditTextName.getText().toString().trim();
                        detail=mEditTextDescribe.getText().toString().trim();
                        uploadFile();
                    }
                }

            }
            break;
            case R.id.selectFileLayout: {
                Intent intent=new Intent(AddResourceActivity.this, SelectFileActivity.class);
                intent.putExtra("type",1);
                startActivityForResult(intent, 1);
            }
        }
    }

    public String getJson() {
        JSONObject info = new JSONObject();
        try {
            info.put("code", 40);
            JSONObject value = new JSONObject();
            value.put("subject", subjectId);
            value.put("type",type);
            value.put("title", title);
            value.put("name",name);
            value.put("detail", detail);
            value.put("time", Operate.getSystemTime());
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==2){
            filePath=data.getStringExtra("path");
            mEditTextPath.setText(filePath.substring(filePath.lastIndexOf('/')+1,filePath.length()));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
