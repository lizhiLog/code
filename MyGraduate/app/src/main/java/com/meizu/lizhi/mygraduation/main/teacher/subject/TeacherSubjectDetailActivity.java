package com.meizu.lizhi.mygraduation.main.teacher.subject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.meizu.flyme.reflect.StatusBarProxy;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.PostCommentData;
import com.meizu.lizhi.mygraduation.data.SubjectCommentData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.Operate;
import com.meizu.lizhi.mygraduation.register.StudentRegisterActivity;
import com.meizu.lizhi.mygraduation.register.TeacherRegisterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherSubjectDetailActivity extends Activity {

    ListView mListView;

    List<SubjectCommentData> mList;

    MyListAdapter myListAdapter;

    RelativeLayout mRelativeLayoutHeader = null;

    RelativeLayout mRelativeLayoutNetworkWrong;

    ImageView mImageViewPhoto;
    TextView mTextViewName;
    ImageView mImageViewDelete;
    TextView mTextViewAuthor;
    TextView mTextViewSchoolAndAcademy;
    TextView mTextViewDetail;
    TextView mTextViewStudentNum;
    TextView mTextViewCommentNum;

    long subjectId;

    SwipeRefreshLayout mRefreshLayout;

    RequestQueue queue;

    String json="";

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getSubjectComment";
    final String deleteSubjectUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/deleteSubject";


    void initView() {
        mListView = (ListView) findViewById(R.id.list);
        mRelativeLayoutHeader = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.teacher_subject_detail_header, null);
        mImageViewPhoto = (ImageView) mRelativeLayoutHeader.findViewById(R.id.image);
        mImageViewDelete = (ImageView) mRelativeLayoutHeader.findViewById(R.id.delete);
        mTextViewName = (TextView) mRelativeLayoutHeader.findViewById(R.id.name);
        mTextViewAuthor = (TextView) mRelativeLayoutHeader.findViewById(R.id.author);
        mTextViewSchoolAndAcademy = (TextView) mRelativeLayoutHeader.findViewById(R.id.schoolAndAcademy);
        mTextViewDetail = (TextView) mRelativeLayoutHeader.findViewById(R.id.detail);
        mTextViewStudentNum = (TextView) mRelativeLayoutHeader.findViewById(R.id.studentNum);
        mTextViewCommentNum = (TextView) mRelativeLayoutHeader.findViewById(R.id.commentNum);
        mListView.addHeaderView(mRelativeLayoutHeader);
    }

    void downloadData() {
        mList = new ArrayList<SubjectCommentData>();
        mRefreshLayout.setRefreshing(true);
        StringRequest getSubjectCommentRequest = new StringRequest(Request.Method.POST, actionUrl,
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
                        mRelativeLayoutNetworkWrong.setVisibility(View.VISIBLE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject info = new JSONObject();
                try {
                    info.put("code", 36);
                    JSONObject value = new JSONObject();
                    value.put("id", subjectId);
                    info.put("data", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json", info.toString().trim());
                return map;
            }

        };
        queue.add(getSubjectCommentRequest);
    }

    public void doAdapter(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            int code = obj.getInt("code");
            int result = obj.getInt("result");
            if (code == 36) {
                switch (result) {
                    case 0: {
                        JSONObject data1 = obj.getJSONObject("data1");
                        putPhoto("http://" + StaticIp.IP + ":8080/graduationServlet/photo/subject/"+data1.getString("photo"));
                        mTextViewName.setText(data1.getString("name"));
                        mTextViewAuthor.setText(data1.getString("author"));
                        mTextViewSchoolAndAcademy.setText(data1.getString("school") + " " + data1.getString("academy"));
                        mTextViewDetail.setText(data1.getString("detail"));
                        mTextViewCommentNum.setText("(" + data1.getInt("commentCount") + ")");
                        mTextViewStudentNum.setText("学生人数(" + data1.getInt("studentCount") + ")");

                        JSONArray data2 = obj.getJSONArray("data2");
                        for (int i = 0; i < data2.length(); i++) {
                            JSONObject value = data2.getJSONObject(i);
                            SubjectCommentData subjectCommentData = new SubjectCommentData();
                            subjectCommentData.id = value.getLong("id");
                            subjectCommentData.photoUrl = value.getString("photo");
                            subjectCommentData.author = value.getString("name");
                            subjectCommentData.content = value.getString("content");
                            subjectCommentData.time = value.getLong("time");
                            mList.add(subjectCommentData);
                        }
                        myListAdapter = new MyListAdapter(TeacherSubjectDetailActivity.this, mList);
                        mListView.setAdapter(myListAdapter);
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

    public void putPhoto(String url) {
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
                .getImageListener(mImageViewPhoto, android.R.drawable.ic_menu_rotate,
                        R.drawable.ic_test);
        mImageLoader.get(url, listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_teacher_subject_detail);
        mRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipe);
        mRelativeLayoutNetworkWrong = (RelativeLayout) findViewById(R.id.networkWrongLayout);
        mRelativeLayoutNetworkWrong.setVisibility(View.GONE);
        initView();
        Intent intent = getIntent();
        subjectId = intent.getLongExtra("id", 0);
        if (json.equals("")) {
            downloadData();
        } else {
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
        mImageViewDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new AlertDialog.Builder(TeacherSubjectDetailActivity.this)
                                .setIcon(android.R.drawable.btn_star)//设置对话框图标
                                .setMessage("删除课程，学生的选课记录以及课程评论和资源将全部删除，你确定要删除吗？")
                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        deleteSubject(subjectId);
                                    }
                                })
                                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();

                        dialog.show();
                    }
                }
        );
    }

    public void deleteSubject(final long id) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "删除中...");
        StringRequest getSubjectCommentRequest = new StringRequest(Request.Method.POST, deleteSubjectUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        JSONObject obj;
                        try {
                            obj = new JSONObject(s);
                            int code = obj.getInt("code");
                            int result = obj.getInt("result");
                            if (code == 56) {
                                switch (result) {
                                    case 0: {
                                        setResult(2);
                                        TeacherSubjectDetailActivity.this.finish();
                                    }break;
                                    case 1:{
                                        Toast.makeText(TeacherSubjectDetailActivity.this,"删除过程中出现了一点问题，请稍候再试",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TeacherSubjectDetailActivity.this,"网络问题出现问题，请稍候再试",Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject info = new JSONObject();
                try {
                    info.put("code", 56);
                    JSONObject value = new JSONObject();
                    value.put("id", id);
                    info.put("data", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json", info.toString().trim());
                return map;
            }
        };
        queue.add(getSubjectCommentRequest);
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
            ImageView mImageViewAuthorPhoto;
            TextView mTextViewAuthor;
            TextView mTextViewContent;
            TextView mImageViewTime;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.subject_comment_item, null);
                this.mImageViewAuthorPhoto = (ImageView) view.findViewById(R.id.imagePhoto);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewContent = (TextView) view.findViewById(R.id.content);
                this.mImageViewTime = (TextView) view.findViewById(R.id.time);
                return view;
            }

            void putData(SubjectCommentData subjectCommentData) {
                initPhoto("http://" + StaticIp.IP + ":8080/graduationServlet/photo/user/"+subjectCommentData.photoUrl);
                this.mTextViewAuthor.setText(subjectCommentData.author);
                this.mTextViewContent.setText(subjectCommentData.content);
                this.mImageViewTime.setText(Operate.getFormatTime(subjectCommentData.time));
            }

            public void initPhoto(String url) {
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
                        .getImageListener(this.mImageViewAuthorPhoto, android.R.drawable.ic_menu_rotate,
                                R.drawable.ic_test);
                mImageLoader.get(url, listener);
            }
        }
    }

}
