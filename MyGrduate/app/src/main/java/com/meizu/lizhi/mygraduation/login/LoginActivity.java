package com.meizu.lizhi.mygraduation.login;

import android.app.Activity;
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
import java.util.List;


public class LoginActivity extends Activity implements View.OnClickListener {

    EditText mEditTextEmail;
    EditText mEditTextPassword;
    CheckBox mCheckBox;
    Spinner mSpinnerUser;
    Button mButtonLogin;
    TextView mTextViewRegister;


    String userEmail;
    String userPassword;
    String userKinds;


    void initView() {
        mEditTextEmail = (EditText) findViewById(R.id.email);
        mEditTextPassword = (EditText) findViewById(R.id.password);
        mCheckBox = (CheckBox) findViewById(R.id.check);
        mSpinnerUser = (Spinner) findViewById(R.id.user_spinner);
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Status.LOGIN_SUCCESS: {

                }
                break;
                case Status.LOGIN_FAILURE: {
                    Toast.makeText(LoginActivity.this, "邮箱或密码错误！", Toast.LENGTH_SHORT).show();

                }
                break;
                case Status.CONN_FAILURE: {
                    Toast.makeText(LoginActivity.this, "链接失败！", Toast.LENGTH_SHORT).show();

                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEdit();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: {
                if (checkInfo() == false) {
                    return;
                }
                String json = putJson();
                Boolean remember = mCheckBox.isChecked();
                if (remember == true) {
                    SharedPreferences sharedPreferences = getSharedPreferences("password", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userEmail", userEmail);
                    editor.putString("password", userPassword);
                    editor.commit();
                }
                new LoginThread(json).start();

            }
            break;
            case R.id.registerText: {

            }
            break;
        }
    }

    boolean checkInfo() {
        userEmail = mEditTextEmail.getText().toString().trim();
        userPassword = mEditTextPassword.getText().toString().trim();
        userKinds = mSpinnerUser.getSelectedItem().toString();
        if (userEmail.equals("") || userPassword.equals("")) {
            Toast.makeText(LoginActivity.this, "用户名和密码不可为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    class LoginThread extends Thread {

        String json;

        LoginThread(String json) {
            this.json = json;
        }

        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://" + StaticIp.IP + ":8080/firstApp/loginServlet");
            List params = new ArrayList();
            params.add(new BasicNameValuePair("json", json));

            try {
                post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(post);
                Message message = new Message();
                if (response.getStatusLine().getStatusCode() == 200) {
                    byte[] data;
                    data = EntityUtils.toByteArray(response.getEntity());
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                    DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                    int result = dataInputStream.readInt();
                    message.what = result;
                } else {
                    message.what = Status.CONN_FAILURE;
                }
                mHandler.sendMessage(message);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String putJson() {
        JSONObject info = new JSONObject();
        try {
            info.put("code", 21);
            JSONObject value = new JSONObject();
            value.put("email", userEmail);
            value.put("password", userPassword);
            value.put("userKinds", userKinds);
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }
}
