package com.meizu.lizhi.mygraduation.main.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.flyme.reflect.StatusBarProxy;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.PostData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.CurrentUser;
import com.meizu.lizhi.mygraduation.operation.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySendPostActivity extends Activity {

    ListView mListView;

    List<PostData> mList;

    PostAdapter mPostAdapter;
    SwipeRefreshLayout mRefreshLayout;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getMyWritePost";

    String json = "";

    void downloadData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        mRefreshLayout.setRefreshing(true);
        StringRequest getPostRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        mRefreshLayout.setRefreshing(false);
                        json = s;
                        doAdapter(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.setRefreshing(false);
                        ToastUtils.showToast(MySendPostActivity.this, "网络链接出现问题,请检查您的网络!");

                    }
                }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> map=new HashMap<String, String>();
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("code",64);
                    JSONObject value=new JSONObject();
                    value.put("id", CurrentUser.getCurrentUserId(MySendPostActivity.this));
                    jsonObject.put("data",value);
                    map.put("json",jsonObject.toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return map;
            }
        };
        queue.add(getPostRequest);
    }

    public void doAdapter(String json) {
        mList = new ArrayList<PostData>();
        mList.clear();
        try {
            JSONObject obj = new JSONObject(json);
            int code = obj.getInt("code");
            int result = obj.getInt("result");
            if (code == 64) {
                switch (result) {
                    case 0: {
                        JSONArray data = obj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            PostData postData = new PostData();
                            postData.id = value.getLong("id");
                            String photo=value.getString("photo");
                            if(!photo.equals("empty")) {
                                postData.photoUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/photo/user/" +photo;
                            }else{
                                postData.photoUrl=photo;
                            }
                            postData.author = value.getString("name");
                            postData.content = value.getString("content");
                            postData.time = value.getLong("time");
                            mList.add(postData);
                        }
                        mPostAdapter = new PostAdapter(this, mList);
                        mListView.setAdapter(mPostAdapter);
                    }
                    break;
                    case 1: {
                        ToastUtils.showToast(MySendPostActivity.this,"服务器出错,请您稍候再试!");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
        setContentView(R.layout.activity_my_comment_post);
        mRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipe);
        mListView= (ListView) findViewById(R.id.list);
        if (json.equals("")) {
            downloadData();
        } else {
            doAdapter(json);
        }
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,android.R.color.holo_green_light,android.R.color.holo_green_dark);
        mRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        downloadData();
                    }
                }
        );
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent(MySendPostActivity.this,MySendPostDetailActivity.class);
            intent.putExtra("id",mList.get(position).id);
            startActivityForResult(intent,2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2){
          if(resultCode==2){
              downloadData();
          }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
