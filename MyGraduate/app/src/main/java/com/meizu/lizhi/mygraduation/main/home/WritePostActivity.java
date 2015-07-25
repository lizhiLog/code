package com.meizu.lizhi.mygraduation.main.home;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
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
import com.meizu.lizhi.mygraduation.operation.CurrentUser;
import com.meizu.lizhi.mygraduation.operation.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class WritePostActivity extends Activity implements View.OnClickListener {

    ActionBar mActionBar;

    EditText mEditTextPost;

    TextView mTextViewCancel;

    TextView mTextViewSend;

    TextView mTextViewAuthor;

    TextView mTextViewFontLength;

    private View mCustomView;

    private int mActionBarOptions;


    String content;
    long time;


    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/sendPost";

    long getSystemTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }


    void initView() {
        mEditTextPost = (EditText) findViewById(R.id.postEdit);
        mTextViewFontLength = (TextView) findViewById(R.id.fontLength);
        mActionBar = getActionBar();
        mCustomView = LayoutInflater.from(this).inflate(R.layout.write_post_custom_title_view, null);
        mTextViewCancel = (TextView) mCustomView.findViewById(R.id.cancel);
        mTextViewAuthor = (TextView) mCustomView.findViewById(R.id.author);
        mTextViewSend = (TextView) mCustomView.findViewById(R.id.send);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
        setContentView(R.layout.activity_write_post);
        initView();
        mTextViewAuthor.setText(CurrentUser.getCurrentUserName(this));
        mTextViewCancel.setOnClickListener(this);
        mTextViewSend.setOnClickListener(this);
        mEditTextPost.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        content = mEditTextPost.getText().toString();
                        mTextViewFontLength.setText((200 - content.length()) + "字");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel: {
                this.finish();
            }
            break;
            case R.id.send: {
                String content = mEditTextPost.getText().toString().trim();
                if (content.length() == 0) {
                    ToastUtils.showToast(WritePostActivity.this,"亲，请输入文字后再发表");
                    return;
                }
                String json = getJson();
                sendPost(json);
            }
            break;
        }
    }

    public void sendPost(final String json) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest registerRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject obj = new JSONObject(s);
                            int code = obj.getInt("code");
                            int result = obj.getInt("result");
                            if (code == 24) {
                                switch (result) {
                                    case 0: {
                                        setResult(2);
                                        WritePostActivity.this.finish();
                                    }
                                    break;
                                    case 1: {
                                        ToastUtils.showToast(WritePostActivity.this,"发贴过程中出了一点小问题");
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
                        ToastUtils.showToast(WritePostActivity.this,"网络链接出了点小问题，请您检查检查网络");
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



    public String getJson() {
        JSONObject info = new JSONObject();
        time = getSystemTime();
        try {
            info.put("code", 24);
            JSONObject value = new JSONObject();
            value.put("author", CurrentUser.getCurrentUserId(this));
            value.put("content", content);
            value.put("time", time);
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }
}
