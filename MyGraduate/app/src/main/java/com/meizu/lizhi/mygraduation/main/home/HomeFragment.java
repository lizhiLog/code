package com.meizu.lizhi.mygraduation.main.home;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.PostData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.Operate;
import com.meizu.lizhi.mygraduation.operation.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{


    ActionBar mActionBar;

    ListView mListView;

    List<PostData> mList;

    PostAdapter mPostAdapter;

    private int mActionBarOptions;

    ImageView mImageViewWrite;

    RelativeLayout mRelativeLayoutCustomBar;

    RelativeLayout mRelativeLayoutNoData;

    SwipeRefreshLayout mRefreshLayout;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getPost";

    String json="";

    void downloadData() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        mRefreshLayout.setRefreshing(true);
        StringRequest getPostRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        mRefreshLayout.setRefreshing(false);
                        json=s;
                        doAdapter(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.setRefreshing(false);
                        ToastUtils.showToast(getActivity(),"网络链接出现问题,请检查您的网络!");
                    }
                }) {

        };
        queue.add(getPostRequest);
    }

    public void doAdapter(String json){
        mList = new ArrayList<PostData>();
        mList.clear();
        mRelativeLayoutNoData.setVisibility(View.GONE);
        try {
            JSONObject obj = new JSONObject(json);
            int code = obj.getInt("code");
            int result = obj.getInt("result");
            if (code == 26) {
                switch (result) {
                    case 0: {
                        JSONArray data = obj.getJSONArray("data");
                        if(data.length()<1){
                            mRelativeLayoutNoData.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            PostData postData = new PostData();
                            postData.id = value.getLong("id");
                            postData.author = value.getString("name");
                            postData.content = value.getString("content");
                            String photo=value.getString("photo");
                            if(!photo.equals("empty")) {
                                postData.photoUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/photo/user/" +photo;
                            }else{
                                postData.photoUrl=photo;
                            }
                            postData.time = value.getLong("time");
                            mList.add(postData);
                        }
                        mPostAdapter = new PostAdapter(getActivity().getApplicationContext(), mList);
                        mListView.setAdapter(mPostAdapter);
                    }
                    break;
                    case 1: {
                        ToastUtils.showToast(getActivity(),"服务器出错,请您稍候再试!");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        mRelativeLayoutCustomBar= (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.home_custom_title_view, null);
        mRelativeLayoutNoData= (RelativeLayout) view.findViewById(R.id.noData);
        mImageViewWrite= (ImageView) mRelativeLayoutCustomBar.findViewById(R.id.writePost);
        mImageViewWrite.setOnClickListener(this);
        mActionBar=getActivity().getActionBar();
        mActionBar.setCustomView(mRelativeLayoutCustomBar);
        mActionBar.setTitle("社区");
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mListView = (ListView) view.findViewById(R.id.homeList);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        if(json.equals("")){
            downloadData();
        }else{
            doAdapter(json);
        }
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
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.writePost:{
                Intent intent=new Intent(getActivity(),WritePostActivity.class);
                startActivityForResult(intent,2);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2){
            if(resultCode==2){
                downloadData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             Intent intent=new Intent(getActivity(),PostDetailActivity.class);
             intent.putExtra("id",mList.get(position).id);
             startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionBarOptions = mActionBar.getDisplayOptions();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActionBar.setDisplayOptions(mActionBarOptions, ActionBar.DISPLAY_SHOW_CUSTOM | mActionBarOptions);

    }

}
