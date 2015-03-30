package com.meizu.lizhi.mygraduation.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.meizu.lizhi.mygraduation.operation.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModifyPasswordActivity extends Activity {


    TextView mTextViewEmail;
    TextView mTextViewOldPassword;
    TextView mTextViewNewPassword;
    TextView mTextViewNewPasswordSubmit;
    Button mButtonSubmit;

    final String actionUrl="http://" + StaticIp.IP + ":8080/graduationServlet/modifyPassword";


    void initView(){
        mTextViewEmail= (TextView) findViewById(R.id.email);
        mTextViewOldPassword= (TextView) findViewById(R.id.oldPassword);
        mTextViewNewPassword= (TextView) findViewById(R.id.newPassword);
        mTextViewNewPasswordSubmit= (TextView) findViewById(R.id.newPasswordSubmit);
        mButtonSubmit= (Button) findViewById(R.id.submit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
        setContentView(R.layout.activity_modify_password);
        initView();
        mButtonSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkInfo()==true){
                            modifyPassword(mTextViewEmail.getText().toString().trim()
                                    , mTextViewOldPassword.getText().toString().trim()
                                    , mTextViewNewPassword.getText().toString().trim());
                        }
                    }
                }
        );
    }

    boolean checkInfo(){
        if(mTextViewEmail.getText().equals("")){
            ToastUtils.showToast(ModifyPasswordActivity.this,"邮箱不可为空!");
            return false;
        }else if(mTextViewOldPassword.getText().equals("")){
            ToastUtils.showToast(ModifyPasswordActivity.this,"原密码不可为空!");
            return false;
        }else if(mTextViewNewPassword.getText().toString().trim().length()<8){
            ToastUtils.showToast(ModifyPasswordActivity.this,"新密码长度不得小于8位!");
            return false;
        }else if(!mTextViewNewPasswordSubmit.getText().toString().trim().equals(mTextViewNewPassword.getText().toString().trim())){
            ToastUtils.showToast(ModifyPasswordActivity.this,"请确定两次输入的新密码相同!");
            return false;
        }else{
            return true;
        }
    }

    public void modifyPassword(final String email, final String oldPassword, final String newPassword){
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
                            int result=obj.getInt("result");
                            if(code==50){
                                switch (result){
                                    case 0:{
                                        ToastUtils.showToast(ModifyPasswordActivity.this,"修改成功");
                                    }break;
                                    case 1:{
                                        ToastUtils.showToast(ModifyPasswordActivity.this,"邮箱或原始密码错误");
                                    }
                                    break;
                                    case 2:{
                                        ToastUtils.showToast(ModifyPasswordActivity.this,"修改密码过程中出了一点小问题，请您稍后再试试");
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
                        ToastUtils.showToast(ModifyPasswordActivity.this,"网络链接出了点小问题，请您检查检查网络");
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                JSONObject json=new JSONObject();
                try {
                    json.put("code",50);
                    JSONObject value=new JSONObject();
                    value.put("email",email);
                    value.put("oldPassword",oldPassword);
                    value.put("newPassword",newPassword);
                    json.put("data",value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json",json.toString().trim());
                return map;
            }
        };
        queue.add(registerRequest);
    }
}
