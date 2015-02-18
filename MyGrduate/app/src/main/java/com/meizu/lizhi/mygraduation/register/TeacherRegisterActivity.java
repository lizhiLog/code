package com.meizu.lizhi.mygraduation.register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TeacherRegisterActivity extends Activity implements View.OnClickListener {

    static final String TAG=TeacherRegisterActivity.class.getName();

    EditText mEditTextEmail;
    EditText mEditTextUserName;
    EditText mEditTextUserNo;
    EditText mEditTextPassword;
    Spinner mSpinnerSchool;
    Spinner mSpinnerAcademy;
    Button mButtonSubmit;

    String userName;
    String email;
    String userNo;
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
        mEditTextUserNo= (EditText) findViewById(R.id.userNo);
        mEditTextPassword = (EditText) findViewById(R.id.userPassword);
        mSpinnerSchool = (Spinner) findViewById(R.id.school);
        mSpinnerAcademy = (Spinner) findViewById(R.id.academy);
        mButtonSubmit = (Button) findViewById(R.id.register);
    }

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);
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
       final ProgressDialog progressDialog=ProgressDialog.show(this,"title","...registering...");
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
                                        Toast.makeText(TeacherRegisterActivity.this,"",Toast.LENGTH_SHORT).show();
                                    }break;
                                    case 1:{
                                        Toast.makeText(TeacherRegisterActivity.this,"您要注册的邮箱已被占用，请更换邮箱注册",Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                    case 2:{
                                        Toast.makeText(TeacherRegisterActivity.this,"注册过程中出了一点小问题，请您稍后再试试",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TeacherRegisterActivity.this,"网络链接出了点小问题，请您检查检查网络",Toast.LENGTH_SHORT).show();
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
        userName = mEditTextUserName.getText().toString().trim();
        userNo=mEditTextUserNo.getText().toString().trim();
        password = mEditTextPassword.getText().toString().trim();
        school = mSpinnerSchool.getSelectedItem().toString().trim();
        academy = mSpinnerAcademy.getSelectedItem().toString().trim();
        time=getSystemTime();

        if((!email.endsWith("@qq.com") && !email.endsWith("@126.com")&&email.endsWith("@163.com"))||
                email.length()<12||email.length()>26){
            Toast.makeText(TeacherRegisterActivity.this, "您注册的邮箱必须是12～26个字符的QQ/126/163邮箱", Toast.LENGTH_SHORT).show();
            return false;
        }else if (userName.length() < 2 || userName.length() > 7) {
            Toast.makeText(TeacherRegisterActivity.this, "您的昵称必须在2～7个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userNo.length()<8||userNo.length()>15) {
            Toast.makeText(TeacherRegisterActivity.this, "教师资格编号必须在8～15个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8 || password.length() > 15) {
            Toast.makeText(TeacherRegisterActivity.this, "密码必须在8～15个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String getJson(){
        JSONObject info = new JSONObject();
        try {
            info.put("code", 20);
            JSONObject value = new JSONObject();
            value.put("email", email);
            value.put("userName", userName);
            value.put("userNo",userNo);
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
