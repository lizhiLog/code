package com.meizu.lizhi.mygraduation.main.teacher.subject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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
import com.meizu.lizhi.mygraduation.data.PostCommentData;
import com.meizu.lizhi.mygraduation.data.SubjectCommentData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.Operate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherSubjectDetailActivity extends Activity {

    static final String TAG=TeacherSubjectDetailActivity.class.getName();

    ListView mListView;

    List<SubjectCommentData> mList;

    MyListAdapter myListAdapter;

    RelativeLayout mRelativeLayoutHeader = null;

    ImageView mImageViewPhoto;
    TextView mTextViewName;
    TextView mTextViewAuthor;
    TextView mTextViewSchoolAndAcademy;
    TextView mTextViewDetail;
    TextView mTextViewStudentNum;
    TextView mTextViewCommentNum;

    long subjectId;

    RequestQueue queue;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getSubjectComment";

    void initView(){
        mListView= (ListView) findViewById(R.id.list);
        mRelativeLayoutHeader = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.teacher_subject_detail_header, null);
        mImageViewPhoto= (ImageView) mRelativeLayoutHeader.findViewById(R.id.image);
        mTextViewName= (TextView) mRelativeLayoutHeader.findViewById(R.id.name);
        mTextViewAuthor= (TextView) mRelativeLayoutHeader.findViewById(R.id.author);
        mTextViewSchoolAndAcademy= (TextView) mRelativeLayoutHeader.findViewById(R.id.schoolAndAcademy);
        mTextViewDetail= (TextView) mRelativeLayoutHeader.findViewById(R.id.detail);
        mTextViewStudentNum= (TextView) mRelativeLayoutHeader.findViewById(R.id.studentNum);
        mTextViewCommentNum= (TextView) mRelativeLayoutHeader.findViewById(R.id.commentNum);
        mListView.addHeaderView(mRelativeLayoutHeader);
        downData();
    }

    void downData() {
        mList = new ArrayList<SubjectCommentData>();
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "加载中...");
        StringRequest getSubjectCommentRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.e("", s);
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(s);
                            int code = obj.getInt("code");
                            int result = obj.getInt("result");
                            if (code == 36) {
                                switch (result) {
                                    case 0: {
                                        Log.e(TAG,"xxxxx");
                                        JSONObject data1=obj.getJSONObject("data1");
                                        mImageViewPhoto.setBackgroundResource(R.drawable.ic_test);
                                        mTextViewName.setText(data1.getString("name"));
                                        mTextViewAuthor.setText(data1.getString("author"));
                                        mTextViewSchoolAndAcademy.setText(data1.getString("school")+" "+data1.getString("academy"));
                                        mTextViewDetail.setText(data1.getString("detail"));
                                        mTextViewCommentNum.setText("("+data1.getInt("commentCount")+")");
                                        mTextViewStudentNum.setText("学生人数("+data1.getInt("studentCount")+")");

                                        JSONArray data2 = obj.getJSONArray("data2");
                                        Log.e(TAG, "data" + data2.length());
                                        for (int i = 0; i < data2.length(); i++) {
                                            Log.e(TAG, "" + i);
                                            JSONObject value = data2.getJSONObject(i);
                                            SubjectCommentData subjectCommentData = new SubjectCommentData();
                                            subjectCommentData.id=value.getLong("id");
                                            subjectCommentData.photoUrl=value.getString("photo");
                                            subjectCommentData.author=value.getString("name");
                                            subjectCommentData.content=value.getString("content");
                                            subjectCommentData.time=value.getLong("time");
                                            mList.add(subjectCommentData);
                                            Log.e(TAG, "" + i);
                                        }
                                        myListAdapter = new MyListAdapter(TeacherSubjectDetailActivity.this, mList);
                                        mListView.setAdapter(myListAdapter);
                                    }
                                    break;
                                    case 1: {
                                        Toast.makeText(TeacherSubjectDetailActivity.this, "刷新列表出了一点小问题，请您稍后再试试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TeacherSubjectDetailActivity.this, "网络链接出了点小问题，请您检查检查网络", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                JSONObject info = new JSONObject();
                try {
                    info.put("code", 36);
                    JSONObject value = new JSONObject();
                    value.put("id",subjectId);
                    info.put("data", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json",info.toString().trim());
                return map;
            }

        };
        queue.add(getSubjectCommentRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_subject_detail);
        queue= Volley.newRequestQueue(this);
        initView();
        Intent intent=getIntent();
        subjectId=intent.getLongExtra("id",0);
        downData();
    }

    class MyListAdapter extends BaseAdapter {

        Context mContext;

        List<SubjectCommentData> mList;

        MyListAdapter(Context context, List<SubjectCommentData> list) {
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
            viewHolder.putData((SubjectCommentData) getItem(position));
            return convertView;
        }


        class ViewHolder {
            ImageView mImageViewPhoto;
            TextView mTextViewAuthor;
            TextView mTextViewContent;
            TextView mImageViewTime;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.subject_comment_item, null);
                this.mImageViewPhoto = (ImageView) view.findViewById(R.id.imagePhoto);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewContent = (TextView) view.findViewById(R.id.content);
                this.mImageViewTime= (TextView) view.findViewById(R.id.time);
                return view;
            }

            void putData(SubjectCommentData subjectCommentData) {
                initPhoto(subjectCommentData.photoUrl);
                this.mTextViewAuthor.setText(subjectCommentData.author);
                this.mTextViewContent.setText(subjectCommentData.content);
                this.mImageViewTime.setText(Operate.getFormatTime(subjectCommentData.time));
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
                        .getImageListener(this.mImageViewPhoto, android.R.drawable.ic_menu_rotate,
                                R.drawable.ic_test);
                mImageLoader.get(url, listener);
            }
        }
    }

}
