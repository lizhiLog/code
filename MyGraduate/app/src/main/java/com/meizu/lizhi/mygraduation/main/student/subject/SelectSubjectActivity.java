package com.meizu.lizhi.mygraduation.main.student.subject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.SubjectData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.CurrentUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectSubjectActivity extends Activity {

    static final String TAG=SelectSubjectActivity.class.getName();

    ListView mListView;

    List<SubjectData> mList;

    MyListAdapter myListAdapter;

    Spinner mSpinnerSchool;
    Spinner mSpinnerAcademy;

    RequestQueue queue;

    String subjectJson;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getAllSubject";

    final String actionUrl1 = "http://" + StaticIp.IP + ":8080/graduationServlet/selectSubject";

    void downData() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "加载中...");
        StringRequest getSubjectRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        subjectJson=s.toString().trim();
                        refreshData(1,"","");
                        mSpinnerSchool.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        int type;
                                        String school=mSpinnerSchool.getSelectedItem().toString().trim();
                                        String academy=mSpinnerAcademy.getSelectedItem().toString().trim();
                                        if(school.equals("全部")&&school.equals("全部")){
                                            type=1;
                                        }else if(school.equals("全部")&&!school.equals("全部")){
                                            type=2;
                                        }else if(!school.equals("全部")&&school.equals("全部")){
                                            type=3;
                                        }else{
                                            type=4;
                                        }
                                        refreshData(type,school,academy);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                }
                        );
                        mSpinnerAcademy.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        int type;
                                        String school=mSpinnerSchool.getSelectedItem().toString().trim();
                                        String academy=mSpinnerAcademy.getSelectedItem().toString().trim();
                                        if(school.equals("全部")&&school.equals("全部")){
                                            type=1;
                                        }else if(school.equals("全部")&&!school.equals("全部")){
                                            type=2;
                                        }else if(!school.equals("全部")&&school.equals("全部")){
                                            type=3;
                                        }else{
                                            type=4;
                                        }
                                        refreshData(type,school,academy);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                }
                        );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(SelectSubjectActivity.this, "网络链接出了点小问题，请您检查检查网络", Toast.LENGTH_SHORT).show();
                    }
                }) {

        };
        queue.add(getSubjectRequest);
    }

    public void refreshData(int type,String school,String academy){
        mList = new ArrayList<SubjectData>();
        try {
            JSONObject obj=new JSONObject(subjectJson);
            int code = obj.getInt("code");
            int result = obj.getInt("result");
            if (code == 42) {
                switch (result) {
                    case 0: {
                        JSONArray data = obj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            SubjectData subjectData = new SubjectData();
                            subjectData.id=value.getLong("id");
                            subjectData.imageUrl= "http://" + StaticIp.IP + ":8080/graduationServlet/photo/subject/"+value.getString("photo");
                            subjectData.name=value.getString("name");
                            subjectData.author=value.getString("author");
                            subjectData.school=value.getString("school");
                            subjectData.academy=value.getString("academy");
                            subjectData.detail=value.getString("detail");
                            if(type==1){
                                mList.add(subjectData);
                            }else if(type==2){
                                if(subjectData.academy.equals(academy)){
                                    mList.add(subjectData);
                                }
                            }else if(type==3){
                                if(subjectData.school.equals(school)){
                                    mList.add(subjectData);
                                }
                            }else{
                                if(subjectData.school.equals(school)&&subjectData.academy.equals(academy)){
                                    mList.add(subjectData);
                                }
                            }
                        }
                        myListAdapter = new MyListAdapter(this, mList);
                        mListView.setAdapter(myListAdapter);
                    }
                    break;
                    case 1: {
                        Toast.makeText(this, "刷新列表出了一点小问题，请您稍后再试试", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_select_subject);
        queue= Volley.newRequestQueue(this);
        mListView = (ListView)findViewById(R.id.list);
        mSpinnerSchool= (Spinner) findViewById(R.id.school);
        mSpinnerAcademy= (Spinner) findViewById(R.id.academy);
        downData();
        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        StringRequest selectSubjectRequest = new StringRequest(Request.Method.POST, actionUrl1,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        subjectJson=s.toString().trim();
                                        try {
                                            JSONObject jsonObject=new JSONObject(s);
                                            int code=jsonObject.getInt("code");
                                            int result=jsonObject.getInt("result");
                                            if(code==44){
                                                switch (result){
                                                    case 0:{
                                                        setResult(2);
                                                        SelectSubjectActivity.this.finish();
                                                    }break;
                                                    case 1:{
                                                        Toast.makeText(SelectSubjectActivity.this, "该课程已经在您的课程列表中，无法重复添加", Toast.LENGTH_SHORT).show();
                                                    }
                                                    break;
                                                    case 2:{
                                                        Toast.makeText(SelectSubjectActivity.this, "添加过程出现问题，请稍候再试一试", Toast.LENGTH_SHORT).show();
                                                        return;
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
                                        Toast.makeText(SelectSubjectActivity.this, "网络链接出了点小问题，请您检查检查网络", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> map=new HashMap<String, String>();
                                JSONObject jsonObject=new JSONObject();
                                try {
                                    jsonObject.put("code",44);
                                    JSONObject value=new JSONObject();
                                    value.put("userId",CurrentUser.getCurentUserId(SelectSubjectActivity.this));
                                    value.put("subject",mList.get(position).id);
                                    jsonObject.put("data",value);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                map.put("json",jsonObject.toString().trim());
                                return map;
                            }

                        };
                        queue.add(selectSubjectRequest);
                    }
                }
        );
    }


    class MyListAdapter extends BaseAdapter {

        Context mContext;

        List<SubjectData> mList;

        MyListAdapter(Context context, List<SubjectData> list) {
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
            viewHolder.putData((SubjectData) getItem(position));
            return convertView;
        }


        class ViewHolder {
            ImageView mImageViewSubject;
            TextView mTextViewName;
            TextView mTextViewAuthor;
            TextView mTextViewSchoolAndAcademy;
            TextView mTextViewDescribe;
            TextView mTextViewToDetail;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.subject_item, null);
                this.mImageViewSubject = (ImageView) view.findViewById(R.id.subjectImage);
                this.mTextViewName = (TextView) view.findViewById(R.id.subjectName);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewSchoolAndAcademy = (TextView) view.findViewById(R.id.schoolAndAcademy);
                this.mTextViewDescribe = (TextView) view.findViewById(R.id.describe);
                this.mTextViewToDetail= (TextView) view.findViewById(R.id.detail);
                return view;
            }

            void putData(SubjectData subjectData) {
                initPhoto(subjectData.imageUrl);
                this.mTextViewName.setText(subjectData.name);
                this.mTextViewAuthor.setText(subjectData.author);
                this.mTextViewSchoolAndAcademy.setText(subjectData.school + " " + subjectData.academy);
                String detail = subjectData.detail;
                this.mTextViewDescribe.setText(detail.length() > 11 ? detail.substring(0, 10) + "..." : detail);
                this.mTextViewToDetail.setVisibility(View.GONE);
            }

            public void initPhoto(String url){
                final LruCache<String, Bitmap> mImageCache = new LruCache<String, Bitmap>(
                        20);
                ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
                    @Override
                    public void putBitmap(String key, Bitmap value) {
                        mImageCache.put(key, value);
                    }

                    @Override
                    public Bitmap getBitmap(String key) {
                        return mImageCache.get(key);
                    }
                };
                ImageLoader mImageLoader = new ImageLoader(queue, imageCache);
                ImageLoader.ImageListener listener = ImageLoader
                        .getImageListener(this.mImageViewSubject, android.R.drawable.ic_menu_rotate,
                                R.drawable.ic_test);
                mImageLoader.get(url, listener);
            }
        }
    }
}
