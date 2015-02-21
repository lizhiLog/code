package com.meizu.lizhi.mygraduation.main.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.meizu.lizhi.mygraduation.data.PostCommentData;
import com.meizu.lizhi.mygraduation.data.PostData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends Activity implements View.OnClickListener {

    private static final String TAG = CommentActivity.class.getName();

    ListView mListView;

    List<PostCommentData> mList;

    MyListAdapter myListAdapter;

    RelativeLayout mRelativeLayoutHeader = null;

    ImageView mImageViewPhoto;
    TextView mTextViewPostAuthor;
    TextView mTextViewTime;
    TextView mTextViewContent;
    TextView mTextViewCommentNum;
    ImageView mImageViewToComment;
    EditText mEditTextComment;
    Button mButtonSend;
    RelativeLayout mRelativeLayoutSend;

    String photoUrl;
    String postAuthor;
    long postTime;
    String content;
    int commentNum;

    String comment;
    long userId=5;
    long time;

    long postId;

    RequestQueue queue;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getComment";
    final String sendActionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/sendComment";

    long getSystemTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    void initView(){
        mListView= (ListView) findViewById(R.id.list);
        mRelativeLayoutHeader = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.comment_detail_header, null);
        mImageViewPhoto= (ImageView) mRelativeLayoutHeader.findViewById(R.id.imagePhoto);
        mTextViewPostAuthor= (TextView) mRelativeLayoutHeader.findViewById(R.id.postAuthor);
        mTextViewTime= (TextView) mRelativeLayoutHeader.findViewById(R.id.date);
        mTextViewContent= (TextView) mRelativeLayoutHeader.findViewById(R.id.content);
        mTextViewCommentNum= (TextView) mRelativeLayoutHeader.findViewById(R.id.commentNum);
        mImageViewToComment= (ImageView) mRelativeLayoutHeader.findViewById(R.id.comment);
        mEditTextComment= (EditText) mRelativeLayoutHeader.findViewById(R.id.commentEdit);
        mButtonSend= (Button) mRelativeLayoutHeader.findViewById(R.id.send);
        mRelativeLayoutSend= (RelativeLayout) mRelativeLayoutHeader.findViewById(R.id.sendCommentLayout);
        mListView.addHeaderView(mRelativeLayoutHeader);
    }

    void downData() {
        mList = new ArrayList<PostCommentData>();
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "加载中...");
        StringRequest getCommentRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        Log.e(TAG, "s---->" + s);
                        try {

                            JSONObject obj = new JSONObject(s);
                            int code = obj.getInt("code");
                            int result = obj.getInt("result");
                            Log.e(TAG, "obj:" + obj.toString().trim());
                            if (code == 28) {
                                switch (result) {
                                    case 0: {
                                        JSONObject data1=obj.getJSONObject("data1");
                                        photoUrl=data1.getString("photo");
                                        postAuthor=data1.getString("name");
                                        content=data1.getString("content");
                                        postTime=data1.getLong("time");
                                        commentNum=data1.getInt("count");

                                        mRelativeLayoutSend.setVisibility(View.GONE);
                                        mImageViewPhoto.setBackgroundResource(R.drawable.ic_test);
                                        mTextViewPostAuthor.setText(postAuthor);
                                        mTextViewTime.setText("2015-02-12 12:12");
                                        mTextViewContent.setText(content);
                                        mTextViewCommentNum.setText("("+commentNum+")");

                                        JSONArray data2 = obj.getJSONArray("data2");
                                        Log.e(TAG, "data" + data2.length());
                                        for (int i = 0; i < data2.length(); i++) {
                                            Log.e(TAG, "" + i);
                                            JSONObject value = data2.getJSONObject(i);
                                            PostCommentData postCommentData = new PostCommentData();
                                            postCommentData.id=value.getLong("id");
                                            postCommentData.photoUrl=value.getString("photo");
                                            postCommentData.author=value.getString("name");
                                            postCommentData.content=value.getString("content");
                                            postCommentData.time=value.getLong("time");
                                            mList.add(postCommentData);
                                            Log.e(TAG, "" + i);
                                        }
                                        Log.e(TAG, "mListSize:" + mList.size());
                                        myListAdapter = new MyListAdapter(CommentActivity.this, mList);
                                        mListView.setAdapter(myListAdapter);
                                    }
                                    break;
                                    case 1: {
                                        Toast.makeText(CommentActivity.this, "刷新列表出了一点小问题，请您稍后再试试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CommentActivity.this, "网络链接出了点小问题，请您检查检查网络", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                JSONObject info = new JSONObject();
                try {
                    info.put("code", 28);
                    JSONObject value = new JSONObject();
                    value.put("id",postId);
                    info.put("data", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json",info.toString().trim());
                return map;
            }

        };
        queue.add(getCommentRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent=getIntent();
        postId=intent.getLongExtra("id",0);
        queue = Volley.newRequestQueue(this);
        initView();
        mImageViewToComment.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);
        downData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.comment:{
                if(mRelativeLayoutSend.getVisibility()==View.GONE){
                   mRelativeLayoutSend.setVisibility(View.VISIBLE);
                }else{
                    mRelativeLayoutSend.setVisibility(View.GONE);
                }
            }break;
            case R.id.send:{
                comment=mEditTextComment.getText().toString().trim();
                time=getSystemTime();
                if(comment.equals("")){
                    Toast.makeText(CommentActivity.this,"评论不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    sendComment();
                }
                //评论
            }break;
        }
    }

    public void sendComment(){
        final ProgressDialog progressDialog=ProgressDialog.show(this,null,"评论中...");
        StringRequest sendRequest=new StringRequest(Request.Method.POST,sendActionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj=new JSONObject(s);
                            int code=obj.getInt("code");
                            int result=obj.getInt("result");
                            if(code==20){
                                switch (result){
                                    case 0:{
                                        Toast.makeText(CommentActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                                    }break;
                                    case 2:{
                                        Toast.makeText(CommentActivity.this,"评论过程中出了一点小问题，请您稍后再试试",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CommentActivity.this,"网络链接出了点小问题，请您检查检查网络",Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                map.put("json",getJson());
                return map;
            }
        };
        queue.add(sendRequest);
    }


    public String getJson(){
        JSONObject info = new JSONObject();
        try {
            info.put("code", 30);
            JSONObject value = new JSONObject();
            value.put("id",userId);
            value.put("post",postId);
            value.put("content",comment);
            value.put("time",time);
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }


    class MyListAdapter extends BaseAdapter {

        Context mContext;

        List<PostCommentData> mList;

        MyListAdapter(Context context, List<PostCommentData> list) {
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
            viewHolder.putData((PostCommentData) getItem(position));
            return convertView;
        }


        class ViewHolder {
            ImageView mImageViewPhoto;
            TextView mTextViewAuthor;
            TextView mTextViewContent;
            TextView mImageViewDate;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.post_comment_item, null);
                this.mImageViewPhoto = (ImageView) view.findViewById(R.id.imagePhoto);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewContent = (TextView) view.findViewById(R.id.content);
                this.mImageViewDate= (TextView) view.findViewById(R.id.date);
                return view;
            }

            void putData(PostCommentData postCommentData) {
                this.mImageViewPhoto.setBackgroundResource(R.drawable.ic_test);
                this.mTextViewAuthor.setText(postCommentData.author);
                this.mTextViewContent.setText(postCommentData.content);
                this.mImageViewDate.setText("2015-02-03 12:12");
            }
        }
    }

}
