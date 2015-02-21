package com.meizu.lizhi.mygraduation.main.home;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private static final String TAG = HomeFragment.class.getName();

    ActionBar mActionBar;

    ListView mListView;

    List<PostData> mList;

    MyListAdapter myListAdapter;

    private View mCustomView;

    private int mActionBarOptions;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getPost";

    void downData() {
        mList = new ArrayList<PostData>();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
        StringRequest getPostRequest = new StringRequest(Request.Method.POST, actionUrl,
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
                            if (code == 26) {
                                switch (result) {
                                    case 0: {
                                        JSONArray data = obj.getJSONArray("data");
                                        Log.e(TAG, "data:" + data);
                                        Log.e(TAG, "downLoad over");
                                        Log.e(TAG, "data" + data.length());
                                        for (int i = 0; i < data.length(); i++) {
                                            Log.e(TAG, "" + i);
                                            JSONObject value = data.getJSONObject(i);
                                            Log.e(TAG, "" + value.toString().trim());
                                            PostData postData = new PostData();
                                            postData.id = value.getLong("id");
                                            postData.author = value.getString("name");
                                            postData.content = value.getString("content");
                                            postData.photoUrl = value.getString("photo");
                                            postData.commentNum = value.getInt("count");
                                            postData.time = value.getLong("time");
                                            mList.add(postData);
                                            Log.e(TAG, "" + i);
                                        }
                                        Log.e(TAG, "mListSize:" + mList.size());
                                        myListAdapter = new MyListAdapter(getActivity().getApplicationContext(), mList);
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

        };
        queue.add(getPostRequest);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreteView");
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);
        mActionBar = getActivity().getActionBar();
        mCustomView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.home_custom_title_view, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mListView = (ListView) view.findViewById(R.id.homeList);
        downData();
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             long myId=mList.get(position).id;
             Intent intent=new Intent(getActivity(),CommentActivity.class);
             intent.putExtra("id",myId);
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

    class MyListAdapter extends BaseAdapter {

        Context mContext;

        List<PostData> mList;

        MyListAdapter(Context context, List<PostData> list) {
            this.mList = list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            Log.e(TAG, "size:" + mList.size());
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
            Log.e(TAG, "getView");
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = viewHolder.createView(mContext);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.putData((PostData) getItem(position));
            return convertView;
        }


        class ViewHolder {
            ImageView mImageViewPost;
            TextView mTextViewContent;
            TextView mTextViewDate;
            TextView mTextViewAuthor;
            TextView mTextViewCommentNum;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.post_item, null);
                this.mImageViewPost = (ImageView) view.findViewById(R.id.postImage);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.postAuthor);
                this.mTextViewDate = (TextView) view.findViewById(R.id.date);
                this.mTextViewContent = (TextView) view.findViewById(R.id.content);
                this.mTextViewCommentNum = (TextView) view.findViewById(R.id.commentNum);
                return view;
            }

            void putData(PostData postData) {
                this.mImageViewPost.setBackgroundResource(R.drawable.ic_test);
                this.mTextViewAuthor.setText(postData.author);
                String content = postData.content;
                this.mTextViewContent.setText(content.length() > 40 ? content.substring(0, 39) + "..." : content);
                this.mTextViewDate.setText("2015-02-03 12:23");
                this.mTextViewCommentNum.setText(postData.commentNum + "");
            }
        }
    }


}
