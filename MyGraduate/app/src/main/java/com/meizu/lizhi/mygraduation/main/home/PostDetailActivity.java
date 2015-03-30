package com.meizu.lizhi.mygraduation.main.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.meizu.flyme.reflect.StatusBarProxy;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.PostCommentData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.CurrentUser;
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

public class PostDetailActivity extends Activity implements View.OnClickListener {

    private static final String TAG = PostDetailActivity.class.getName();

    ListView mListView;

    List<PostCommentData> mList;

    MyListAdapter myListAdapter;

    RelativeLayout mRelativeLayoutHeader = null;

    ImageView mImageViewPostPhoto;
    TextView mTextViewPostAuthor;
    TextView mTextViewTime;
    TextView mTextViewContent;
    TextView mTextViewCommentNum;
    ImageView mImageViewToComment;
    EditText mEditTextComment;
    Button mButtonSend;
    RelativeLayout mRelativeLayoutSend;

    String comment;
    long time;
    long postId;

    String postPhoto;

    RequestQueue queue;

    Map<String, Bitmap> map;

    static final int MAP_SIZE = 10;

    SwipeRefreshLayout mRefreshLayout;


    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getCommentList";
    final String sendActionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/sendPostComment";

    void initView(){
        mListView= (ListView) findViewById(R.id.list);
        mRelativeLayoutHeader = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.comment_detail_header, null);
        mImageViewPostPhoto= (ImageView) mRelativeLayoutHeader.findViewById(R.id.imagePhoto);
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


    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    mImageViewPostPhoto.setImageBitmap(map.get(postPhoto));
                }
            }
            super.handleMessage(msg);
        }
    };

    void downloadData() {
        mList = new ArrayList<PostCommentData>();
        mRefreshLayout.setRefreshing(true);
        StringRequest getCommentRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.e("",s);
                        mRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject obj = new JSONObject(s);
                            int code = obj.getInt("code");
                            int result = obj.getInt("result");
                            if (code == 28) {
                                switch (result) {
                                    case 0: {
                                        JSONObject data1=obj.getJSONObject("data1");
                                        if(!data1.getString("photo").equals("empty")){
                                            postPhoto= "http://" + StaticIp.IP + ":8080/graduationServlet/photo/user/"+data1.getString("photo");
                                            if(!map.containsKey(postPhoto)){
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        loadImage(postPhoto);
                                                        Message message = new Message();
                                                        message.what = 1;
                                                        mHandler.sendMessage(message);
                                                    }
                                                }).start();
                                            }else {
                                                mImageViewPostPhoto.setImageBitmap(map.get(postPhoto));
                                            }

                                        }else{
                                            mImageViewPostPhoto.setBackgroundResource(R.drawable.icon_photo);
                                        }
                                        mTextViewPostAuthor.setText(data1.getString("name"));
                                        mTextViewTime.setText(Operate.getFormatTime(data1.getLong("time")));
                                        mTextViewContent.setText(data1.getString("content"));
                                        mTextViewCommentNum.setText("("+data1.getInt("count")+")");

                                        JSONArray data2 = obj.getJSONArray("data2");
                                        Log.e(TAG, "data" + data2.length());
                                        for (int i = 0; i < data2.length(); i++) {
                                            Log.e(TAG, "" + i);
                                            JSONObject value = data2.getJSONObject(i);
                                            PostCommentData postCommentData = new PostCommentData();
                                            postCommentData.id=value.getLong("id");
                                            String photo=value.getString("photo");
                                            if(photo.equals("empty")){
                                                postCommentData.photoUrl=photo;
                                            }else{
                                                postCommentData.photoUrl="http://" + StaticIp.IP + ":8080/graduationServlet/photo/user/"+value.getString("photo");
                                            }
                                            postCommentData.author=value.getString("name");
                                            postCommentData.content=value.getString("content");
                                            postCommentData.time=value.getLong("time");
                                            mList.add(postCommentData);
                                            Log.e(TAG, "" + i);
                                        }
                                        myListAdapter = new MyListAdapter(PostDetailActivity.this, mList);
                                        mListView.setAdapter(myListAdapter);
                                    }
                                    break;
                                    case 1: {
                                        ToastUtils.showToast(PostDetailActivity.this, "服务器出错,请您稍候再试!");
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
                        mRefreshLayout.setRefreshing(false);
                        ToastUtils.showToast(PostDetailActivity.this, "网络链接出了点小问题，请您检查检查网络!");
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
        StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
        setContentView(R.layout.activity_post_detail);
        mRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipe);
        map = new HashMap<String, Bitmap>();
        Intent intent=getIntent();
        postId=intent.getLongExtra("id",0);
        queue = Volley.newRequestQueue(this);
        initView();
        mImageViewToComment.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);
        mRelativeLayoutSend.setVisibility(View.GONE);
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
        downloadData();

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
                time= Operate.getSystemTime();
                if(comment.equals("")){
                    ToastUtils.showToast(PostDetailActivity.this,"评论不能为空");
                    return;
                }else{
                    mButtonSend.setEnabled(false);
                    sendComment();
                }
                //评论
            }break;
        }
    }

    public void sendComment(){
        StringRequest sendRequest=new StringRequest(Request.Method.POST,sendActionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject obj=new JSONObject(s);
                            int code=obj.getInt("code");
                            int result=obj.getInt("result");
                            mButtonSend.setEnabled(true);
                            if(code==30){
                                switch (result){
                                    case 0:{
                                        downloadData();
                                        mButtonSend.setEnabled(true);
                                        mRelativeLayoutSend.setVisibility(View.GONE);
                                    }break;
                                    case 1:{
                                        ToastUtils.showToast(PostDetailActivity.this,"评论过程中出了一点小问题，请您稍后再试试");
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
                        ToastUtils.showToast(PostDetailActivity.this,"网络链接出了点小问题，请您检查检查网络");
                        mButtonSend.setEnabled(true);
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
            value.put("author", CurrentUser.getCurrentUserId(this));
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

            void putData(final PostCommentData postCommentData) {

                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 1: {
                               mImageViewPhoto.setImageBitmap(map.get(postCommentData.photoUrl));
                            }
                        }
                        super.handleMessage(msg);
                    }
                };
                if(!postCommentData.photoUrl.equals("empty")) {
                    if (map.containsKey(postCommentData.photoUrl)) {
                        mImageViewPhoto.setImageBitmap(map.get(postCommentData.photoUrl));
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                loadImage(postCommentData.photoUrl);
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        }).start();
                    }
                }else{
                    this.mImageViewPhoto.setBackgroundResource(R.drawable.icon_photo);

                }
                this.mTextViewAuthor.setText(postCommentData.author);
                this.mTextViewContent.setText(postCommentData.content);
                this.mImageViewDate.setText(Operate.getFormatTime(postCommentData.time));
            }
        }
    }
    public void loadImage(String url) {
        URL myFileUrl;
        HttpURLConnection connection;
        try {
            myFileUrl = new URL(url);
            connection = (HttpURLConnection) myFileUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            if (map.size() == MAP_SIZE) {
                Iterator<String> iterator = map.keySet().iterator();
                if (iterator.hasNext()) {
                    map.remove(iterator.next());
                }
            }
            map.put(url, bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
