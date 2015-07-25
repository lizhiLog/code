package com.meizu.lizhi.mygraduation.main.teacher.subject;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.ResourceData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherExcelFragment extends Fragment {


    ListView mListView;

    List<ResourceData> mList;

    RequestQueue queue;

    TeacherResourceAdapter mResourceAdapter;

    RelativeLayout mRelativeLayoutFooter;

    RelativeLayout mRelativeLayoutNetworkWrong;

    long subjectId;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getSubjectResource";

    String json="";

    SwipeRefreshLayout mRefreshLayout;

    void downloadData() {
        mList = new ArrayList<ResourceData>();
        mRefreshLayout.setRefreshing(true);
        StringRequest getSubjectResourceRequest = new StringRequest(Request.Method.POST, actionUrl,
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
                        mRelativeLayoutNetworkWrong.setVisibility(View.VISIBLE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                JSONObject info = new JSONObject();
                try {
                    info.put("code", 38);
                    JSONObject value = new JSONObject();
                    value.put("id",subjectId);
                    value.put("type",4);
                    info.put("data", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json",info.toString().trim());
                return map;
            }

        };
        queue.add(getSubjectResourceRequest);
    }

    public void doAdapter(String json){
        mList.clear();
        try {
            JSONObject obj = new JSONObject(json);
            int code = obj.getInt("code");
            int result = obj.getInt("result");
            if (code == 38) {
                switch (result) {
                    case 0: {
                        JSONArray data=obj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            ResourceData resourceData = new ResourceData();
                            resourceData.id=value.getLong("id");
                            resourceData.title=value.getString("title");
                            resourceData.name=value.getString("name");
                            resourceData.detail=value.getString("detail");
                            mList.add(resourceData);
                        }
                        mResourceAdapter = new TeacherResourceAdapter(getActivity(), mList,mHandler);
                        mListView.setAdapter(mResourceAdapter);
                    }
                    break;
                    case 1: {
                        mRelativeLayoutNetworkWrong.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    downloadData();
                }
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2){
            if(resultCode==2){
                downloadData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_resource, container, false);
        mRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        Intent intent=getActivity().getIntent();
        subjectId=intent.getLongExtra("id",0);
        queue= Volley.newRequestQueue(getActivity());
        mListView= (ListView) view.findViewById(R.id.list);
        mRelativeLayoutNetworkWrong= (RelativeLayout) view.findViewById(R.id.networkWrongLayout);
        mRelativeLayoutNetworkWrong.setVisibility(View.GONE);
        mRelativeLayoutFooter= (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.teacher_resource_footer, null);
        mRelativeLayoutFooter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),AddResourceActivity.class);
                        intent.putExtra("type",4);
                        intent.putExtra("subject",subjectId);
                        startActivityForResult(intent,2);
                    }
                }
        );
        mListView.addFooterView(mRelativeLayoutFooter);
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
        if(json.equals("")){
            downloadData();
        }else{
            doAdapter(json);
        }
        return view;
    }
}
