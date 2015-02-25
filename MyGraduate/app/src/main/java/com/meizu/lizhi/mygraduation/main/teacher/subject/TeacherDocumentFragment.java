package com.meizu.lizhi.mygraduation.main.teacher.subject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.meizu.lizhi.mygraduation.data.ResourceData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.SelectFileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TeacherDocumentFragment extends Fragment {

    static final String TAG=TeacherDocumentFragment.class.getName();

    ListView mListView;

    List<ResourceData> mList;

    RequestQueue queue;

    MyListAdapter myListAdapter;

    RelativeLayout mRelativeLayoutFooter;

    long subjectId;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getSubjectResource";

    void downData() {
        mList = new ArrayList<ResourceData>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
        StringRequest getSubjectResourceRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(s);
                            int code = obj.getInt("code");
                            int result = obj.getInt("result");
                            if (code == 38) {
                                switch (result) {
                                    case 0: {
                                        Log.e(TAG,"xxxxx");
                                        JSONArray data=obj.getJSONArray("data");
                                        for (int i = 0; i < data.length(); i++) {
                                            Log.e(TAG, "" + i);
                                            JSONObject value = data.getJSONObject(i);
                                            ResourceData resourceData = new ResourceData();
                                            resourceData.id=value.getLong("id");
                                            resourceData.title=value.getString("title");
                                            resourceData.name=value.getString("name");
                                            resourceData.detail=value.getString("detail");
                                            mList.add(resourceData);
                                            Log.e(TAG, "" + i);
                                        }
                                        myListAdapter = new MyListAdapter(getActivity(), mList);
                                        mListView.setAdapter(myListAdapter);
                                    }
                                    break;
                                    case 1: {
                                        Toast.makeText(getActivity(), "刷新列表出了一点小问题，请您稍后再试试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "网络链接出了点小问题，请您检查检查网络", Toast.LENGTH_SHORT).show();
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
                    value.put("type",1);
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

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_resource, container, false);
        Intent intent=getActivity().getIntent();
        subjectId=intent.getLongExtra("id",0);
        Log.e("xxx",subjectId+"");
        queue= Volley.newRequestQueue(getActivity());
        mListView= (ListView) view.findViewById(R.id.list);
        mRelativeLayoutFooter= (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.teacher_resource_footer, null);
        mRelativeLayoutFooter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),AddResourceActivity.class);
                        intent.putExtra("type",1);
                        intent.putExtra("subject",subjectId);
                        startActivity(intent);
                    }
                }
        );
        mListView.addFooterView(mRelativeLayoutFooter);
        downData();
        return view;
    }
    class MyListAdapter extends BaseAdapter {

        Context mContext;

        List<ResourceData> mList;

        MyListAdapter(Context context, List<ResourceData> list) {
            this.mList = list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = viewHolder.createView(mContext);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.putData((ResourceData) getItem(position));
            return convertView;
        }


        class ViewHolder {
            TextView mTextViewTitle;
            TextView mTextViewDescribe;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.recourse_item, null);
                this.mTextViewTitle = (TextView) view.findViewById(R.id.resourceTitle);
                this.mTextViewDescribe = (TextView) view.findViewById(R.id.resourceDescribe);
                return view;
            }

            void putData(ResourceData resourceData) {
                this.mTextViewTitle.setText(resourceData.title);
                this.mTextViewDescribe.setText(resourceData.detail);
            }
        }
    }

}
