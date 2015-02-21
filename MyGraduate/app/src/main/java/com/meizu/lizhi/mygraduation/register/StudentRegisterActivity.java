package com.meizu.lizhi.mygraduation.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.internet.StaticIp;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentRegisterActivity extends Activity implements View.OnClickListener {

    static final String TAG=TeacherRegisterActivity.class.getName();

    EditText mEditTextEmail;
    EditText mEditTextUserName;
    EditText mEditTextPassword;
    Spinner mSpinnerSchool;
    Spinner mSpinnerAcademy;
    Button mButtonSubmit;

    String name;
    String email;
    String password;
    String school;
    String academy;
    long time;


    final String actionUrl="http://" + StaticIp.IP + ":8080/graduationServlet/register";

    long getSystemTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }


    void initView() {
        mEditTextEmail = (EditText) findViewById(R.id.email);
        mEditTextUserName = (EditText) findViewById(R.id.userName);
        mEditTextPassword = (EditText) findViewById(R.id.userPassword);
        mSpinnerSchool = (Spinner) findViewById(R.id.school);
        mSpinnerAcademy = (Spinner) findViewById(R.id.academy);
        mButtonSubmit = (Button) findViewById(R.id.register);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        initView();
        mButtonSubmit.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:{
                if (checkInfo() == false) {
                    return;
                }
                String json = getJson();
                Log.v(TAG,"json:"+json);
                uploadData(json);
            }
        }
    }

    public void uploadData(final String json){
        RequestQueue queue= Volley.newRequestQueue(this);
        final ProgressDialog progressDialog=ProgressDialog.show(this,null,"注册中...");
        StringRequest registerRequest=new StringRequest(Request.Method.POST,actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj=new JSONObject(s);
                            int code=obj.getInt("code");
                            JSONObject data=obj.getJSONObject("data");
                            int result=data.getInt("result");
                            if(code==20){
                                switch (result){
                                    case 0:{
                                        Toast.makeText(StudentRegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                    }break;
                                    case 1:{
                                        Toast.makeText(StudentRegisterActivity.this,"您要注册的邮箱已被占用，请更换邮箱注册",Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                    case 2:{
                                        Toast.makeText(StudentRegisterActivity.this,"注册过程中出了一点小问题，请您稍后再试试",Toast.LENGTH_SHORT).show();
                                    }
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
                        progressDialog.dismiss();
                        Toast.makeText(StudentRegisterActivity.this,"网络链接出了点小问题，请您检查检查网络",Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                map.put("json",json);
                return map;
            }
        };
        queue.add(registerRequest);
    }

    public boolean checkInfo(){
        email = mEditTextEmail.getText().toString().trim();
        name = mEditTextUserName.getText().toString().trim();
        password = mEditTextPassword.getText().toString().trim();
        school = mSpinnerSchool.getSelectedItem().toString().trim();
        academy = mSpinnerAcademy.getSelectedItem().toString().trim();
        time=getSystemTime();

        if(!isEmail(email)){
            Toast.makeText(StudentRegisterActivity.this, "您注册的邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }else if (name.length() < 2 || name.length() > 7) {
            Toast.makeText(StudentRegisterActivity.this, "您的昵称必须在2～7个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8 || password.length() > 15) {
            Toast.makeText(StudentRegisterActivity.this, "密码必须在8～15个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    public String getJson(){
        JSONObject info = new JSONObject();
        try {
            info.put("code", 20);
            JSONObject value = new JSONObject();
            value.put("type",1); //1 代表学生
            value.put("email", email);
            value.put("name", name);
            value.put("password", password);
            value.put("school", school);
            value.put("academy", academy);
            value.put("time",time);
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }
}
