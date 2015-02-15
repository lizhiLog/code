package com.meizu.lizhi.mygraduation.register;

import android.app.Activity;
import android.app.AlertDialog;
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
import java.util.List;

public class StudentRegisterActivity extends Activity implements View.OnClickListener {

    ImageView mButtonImagePhoto;
    EditText mEditTextUserName;
    EditText mEditTextEmail;
    EditText mEditTextPassword;
    EditText mEditTextPasswordSubmit;
    Spinner mSpinnerSchool;
    Spinner mSpinnerAcademy;
    Button mButtonSubmit;

    Bitmap mBitmap = null;

    long systemTime = 0;

    String photoName;
    String userName;
    String email;
    String password;
    String passwordSubmit;
    String school;
    String academy;

    Object LOCK = new Object();

    static final int SUCCESS = 1;

    long getSystemTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }


    void initView() {
        mButtonImagePhoto = (ImageView) findViewById(R.id.imagePhoto);
        mEditTextUserName = (EditText) findViewById(R.id.userName);
        mEditTextEmail = (EditText) findViewById(R.id.email);
        mEditTextPassword = (EditText) findViewById(R.id.password);
        mEditTextPasswordSubmit = (EditText) findViewById(R.id.passwordSubmit);
        mSpinnerSchool = (Spinner) findViewById(R.id.school);
        mSpinnerAcademy = (Spinner) findViewById(R.id.academy);
        mButtonSubmit = (Button) findViewById(R.id.register);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS: {
                    Toast.makeText(StudentRegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        initView();
        mButtonImagePhoto.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagePhoto: {
                //选择头像
                systemTime = getSystemTime();
                ShowPickDialog();
            }
            break;
            case R.id.register: {
                if (checkInfo() == false) {
                    return;
                }
                String json = putJson();
                Log.e("", json);
                Toast.makeText(StudentRegisterActivity.this, json, Toast.LENGTH_SHORT).show();
                new UploadPhotoThread(mBitmap).start();
                new RegisterThread(json).start();

            }
        }
    }

    class UploadPhotoThread extends Thread {

        Bitmap mBitmap;

        UploadPhotoThread(Bitmap bitmap) {
            this.mBitmap = bitmap;
        }

        @Override
        public void run() {
            //上传图片

            //上传成功

            synchronized (LOCK) {
                LOCK.notify();
            }

        }
    }


    class RegisterThread extends Thread {

        String json;

        RegisterThread(String json) {
            this.json = json;
        }

        @Override
        public void run() {
            synchronized (LOCK) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://" + StaticIp.IP + ":8080/firstApp/studentRegisterServlet");
            List params = new ArrayList();
            params.add(new BasicNameValuePair("json", json));

            try {
                post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(post);
                Message message = new Message();
                if (response.getStatusLine().getStatusCode() == 200) {
                    message.what = SUCCESS;
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


    boolean checkInfo() {
        if (mBitmap == null) {
            photoName = "";
        }
        userName = mEditTextUserName.getText().toString().trim();
        email = mEditTextEmail.getText().toString().trim();
        password = mEditTextPassword.getText().toString().trim();
        passwordSubmit = mEditTextPasswordSubmit.getText().toString().trim();
        school = mSpinnerSchool.getSelectedItem().toString().trim();
        academy = mSpinnerAcademy.getSelectedItem().toString().trim();

        if (userName.length() < 2 || userName.length() > 4) {
            Toast.makeText(StudentRegisterActivity.this, "用户名在2～4个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        } else if (checkEmail(email) == false) {
            Toast.makeText(StudentRegisterActivity.this, "邮箱不符合条件", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(passwordSubmit) || password.length() < 5 || password.length() > 16) {
            Toast.makeText(StudentRegisterActivity.this, "密码在5～16个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    boolean checkEmail(String email) {
        if ((!email.endsWith("@qq.com") && !email.endsWith("@162.com")) || email.length() < 6 || email.length() > 15) {
            return false;
        }
        return true;
    }


    String putJson() {
        JSONObject info = new JSONObject();
        try {
            info.put("code", 20);
            JSONObject value = new JSONObject();
            value.put("photo", photoName);
            value.put("userName", userName);
            value.put("email", email);
            value.put("password", password);
            value.put("school", school);
            value.put("academy", academy);
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }

    private void ShowPickDialog() {
        new AlertDialog.Builder(this)
                .setTitle("头像选择")
                .setNegativeButton("去相册", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, 1);

                    }
                })
                .setPositiveButton("去拍照", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), systemTime + ".PNG")));
                        startActivityForResult(intent, 2);
                    }
                }).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                startPhotoZoom(data.getData());
                break;

            case 2:
                File temp = new File(Environment.getExternalStorageDirectory() + "" + systemTime + ".PNG");
                startPhotoZoom(Uri.fromFile(temp));
                break;

            case 3:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
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
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    private void setPicToView(Intent picData) {
        Bundle extras = picData.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            photoName = systemTime + ".PNG";
            mButtonImagePhoto.setImageBitmap(mBitmap);
        }
    }

}
