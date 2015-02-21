package com.meizu.lizhi.mygraduation.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.meizu.lizhi.mygraduation.internet.Status;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends Activity implements View.OnClickListener {

    EditText mEditTextEmail;
    EditText mEditTextPassword;
    CheckBox mCheckBox;
    Spinner mSpinnerType;
    Button mButtonLogin;
    TextView mTextViewRegister;


    String userEmail;
    String userPassword;
    int userType;

    static final String actionUrl="http://" + StaticIp.IP + ":8080/graduationServlet/login";

    void initView() {
        mEditTextEmail = (EditText) findViewById(R.id.email);
        mEditTextPassword = (EditText) findViewById(R.id.password);
        mCheckBox = (CheckBox) findViewById(R.id.check);
        mSpinnerType = (Spinner) findViewById(R.id.type);
        mButtonLogin = (Button) findViewById(R.id.login);
        mTextViewRegister = (TextView) findViewById(R.id.registerText);
    }

    void initEdit() {
        SharedPreferences sharedPreferences = getSharedPreferences("password", MODE_PRIVATE);
        String name = sharedPreferences.getString("userEmail", "");
        String password = sharedPreferences.getString("password", "");
        if (!name.equals("") && !password.equals("")) {
            mEditTextEmail.setText(name);
            mEditTextPassword.setText(password);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEdit();
        mButtonLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: {
                if (checkInfo() == false) {
                    return;
                }
                String json = getJson();
                Boolean check = mCheckBox.isChecked();
                if (check == true) {
                    SharedPreferences sharedPreferences = getSharedPreferences("password", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userEmail", userEmail);
                    editor.putString("password", userPassword);
                    editor.commit();
                }
                doLogin(json);

            }
            break;
            case R.id.registerText: {
                Toast.makeText(LoginActivity.this,"去注册哪个？",Toast.LENGTH_SHORT).show();

            }
            break;
        }
    }

    public void doLogin(final String json){
        RequestQueue queue= Volley.newRequestQueue(this);
        final ProgressDialog progressDialog=ProgressDialog.show(this,null,"登录中...");
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
                            if(code==22){
                                switch (result){
                                    case 0:{
                                        Toast.makeText(LoginActivity.this,"老师登录成功",Toast.LENGTH_SHORT).show();
                                    }break;
                                    case 1:{
                                        Toast.makeText(LoginActivity.this,"学生登录成功",Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                    case 2:{
                                        Toast.makeText(LoginActivity.this,"邮箱或者密码错误",Toast.LENGTH_SHORT).show();
                                    }break;
                                    case 3:{
                                        Toast.makeText(LoginActivity.this,"登录过程中出了一点小问题，请您稍后再试试",Toast.LENGTH_SHORT).show();
                                    }break;
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
                        Toast.makeText(LoginActivity.this,"网络链接出了点小问题，请您检查检查网络",Toast.LENGTH_SHORT).show();
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

    boolean checkInfo() {
        userEmail = mEditTextEmail.getText().toString().trim();
        userPassword = mEditTextPassword.getText().toString().trim();
        userType = (mSpinnerType.getSelectedItem().toString()).equals("老师")?0:1;
        if (userEmail.equals("") || userPassword.equals("")) {
            Toast.makeText(LoginActivity.this, "用户名和密码不可为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String getJson(){
        JSONObject info = new JSONObject();
        try {
            info.put("code", 22);
            JSONObject value = new JSONObject();
            value.put("type",userType);
            value.put("email", userEmail);
            value.put("password", userPassword);
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }

}
