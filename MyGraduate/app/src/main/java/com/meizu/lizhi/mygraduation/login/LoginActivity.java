package com.meizu.lizhi.mygraduation.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.flyme.reflect.StatusBarProxy;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.main.student.StudentMainActivity;
import com.meizu.lizhi.mygraduation.main.teacher.TeacherMainActivity;
import com.meizu.lizhi.mygraduation.operation.CurrentUser;
import com.meizu.lizhi.mygraduation.operation.Operate;
import com.meizu.lizhi.mygraduation.operation.ToastUtils;
import com.meizu.lizhi.mygraduation.register.StudentRegisterActivity;
import com.meizu.lizhi.mygraduation.register.TeacherRegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity implements View.OnClickListener {

    EditText mEditTextEmail;
    EditText mEditTextPassword;
    CheckBox mCheckBox;
    Button mButtonLogin;
    TextView mTextViewRegister;
    TextView mTextViewModifyPassword;


    String userEmail;
    String userPassword;

    static final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/login";

    void initView() {
        mEditTextEmail = (EditText) findViewById(R.id.email);
        mEditTextPassword = (EditText) findViewById(R.id.password);
        mCheckBox = (CheckBox) findViewById(R.id.check);
        mTextViewModifyPassword = (TextView) findViewById(R.id.modifyPassword);
        mButtonLogin = (Button) findViewById(R.id.login);
        mTextViewRegister = (TextView) findViewById(R.id.registerText);
    }

    void initEdit() {
        SharedPreferences sharedPreferences = getSharedPreferences("passwordInfo", MODE_PRIVATE);
        String name = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        if (!name.equals("") && !password.equals("")) {
            mEditTextEmail.setText(name);
            mEditTextPassword.setText(password);
        }
    }


    void checkHasLogin(){
        switch (CurrentUser.getCurrentUserType(this)){
            case 0:{
                Intent intent=new Intent(LoginActivity.this,TeacherMainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }break;
            case 1:{
                Intent intent=new Intent(LoginActivity.this,StudentMainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarProxy.setStatusBarDarkIcon(getWindow(),true);
        setContentView(R.layout.activity_login);
        checkHasLogin();
        initView();
        initEdit();
        mButtonLogin.setOnClickListener(this);
        mTextViewModifyPassword.setOnClickListener(this);
        mTextViewRegister.setOnClickListener(this);

    }

    public void showDialog(){
        Dialog dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.btn_star)//设置对话框图标
                .setTitle("选择注册类型")
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("学生", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent=new Intent(LoginActivity.this, StudentRegisterActivity.class);
                        startActivity(intent);

                    }
                })
                .setNeutralButton("老师", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent=new Intent(LoginActivity.this, TeacherRegisterActivity.class);
                        startActivity(intent);
                    }
                }).create();

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: {
                if (checkInfo() == false) {
                    return;
                }
                doLogin(getJson());
            }
            break;
            case R.id.registerText: {
                showDialog();
            }
            break;
            case R.id.modifyPassword: {
                Intent intent=new Intent(LoginActivity.this,ModifyPasswordActivity.class);
                startActivity(intent);
            }
        }
    }

    public void doLogin(final String json) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "登录中...");
        StringRequest registerRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            s = new String(s.toString().getBytes(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(s);
                            int code = obj.getInt("code");
                            int result = obj.getInt("result");
                            if (code == 22) {
                                switch (result) {
                                    case 0: {
                                        JSONObject data = obj.getJSONObject("data");
                                        Boolean check = mCheckBox.isChecked();
                                        long id = data.getLong("id");
                                        String photo=data.getString("photo");
                                        String email = data.getString("email");
                                        String name = data.getString("name");
                                        String password=data.getString("password");
                                        int type = data.getInt("type");
                                        saveCurrentUserInfo(id,photo,email, name, type);   //标志当前用户
                                        if (check == true) {
                                            savePasswordInfo(email,password);
                                        }
                                        if (type == 0) {
                                            Intent intent=new Intent(LoginActivity.this, TeacherMainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent=new Intent(LoginActivity.this, StudentMainActivity.class);
                                            startActivity(intent);
                                        }
                                        LoginActivity.this.finish();
                                    }
                                    break;
                                    case 1: {
                                        ToastUtils.showToast(LoginActivity.this,"邮箱或者密码错误");
                                    }
                                    break;
                                    case 2: {
                                        ToastUtils.showToast(LoginActivity.this,"登录过程中出了一点小问题，请您稍后再试试");
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
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "网络链接出了点小问题，请您检查检查网络", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("json", json);
                return map;
            }
        };
        queue.add(registerRequest);
    }

    public void saveCurrentUserInfo(long userId,String photo,String email, String name, int type) {
        SharedPreferences sharedPreferences = getSharedPreferences("currentUserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("id", userId);
        editor.putString("photo",photo);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putInt("type", type);
        editor.commit();
    }

    public void savePasswordInfo(String email,String password){
        SharedPreferences sharedPreferences = getSharedPreferences("passwordInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }

    boolean checkInfo() {
        userEmail = mEditTextEmail.getText().toString().trim();
        userPassword = mEditTextPassword.getText().toString().trim();
        if (userEmail.equals("") || userPassword.equals("")) {
            Toast.makeText(LoginActivity.this, "用户名和密码不可为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String getJson() {
        JSONObject info = new JSONObject();
        try {
            info.put("code", 22);
            JSONObject value = new JSONObject();
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
