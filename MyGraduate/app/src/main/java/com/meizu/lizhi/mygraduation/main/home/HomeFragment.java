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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = HomeFragment.class.getName();

    ActionBar mActionBar;

    ListView mListView;

    List<PostData> mList;

    MyListAdapter myListAdapter;

    private View mCustomView;

    private int mActionBarOptions;

    ImageView mImageViewWrite;

    RelativeLayout mRelativeLayoutNoData=null;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getPost";

    String json="";

    void downloadData() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
        StringRequest getPostRequest = new StringRequest(Request.Method.POST, actionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        json=s;
                        doAdapter(json);
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

    public void doAdapter(String json){
        mList = new ArrayList<PostData>();
        mList.clear();
        try {
            JSONObject obj = new JSONObject(json);
            int code = obj.getInt("code");
            int result = obj.getInt("result");
            if (code == 26) {
                switch (result) {
                    case 0: {
                        JSONArray data = obj.getJSONArray("data");
                        if(data.length()==0){
                            mListView.setVisibility(View.GONE);
                            mRelativeLayoutNoData.setVisibility(View.VISIBLE);
                            return;
                        }else{
                            mRelativeLayoutNoData.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            PostData postData = new PostData();
                            postData.id = value.getLong("id");
                            postData.author = value.getString("name");
                            postData.content = value.getString("content");
                            postData.photoUrl = value.getString("photo");
                            postData.commentNum = value.getInt("count");
                            postData.time = value.getLong("time");
                            mList.add(postData);
                        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);
        mRelativeLayoutNoData= (RelativeLayout) view.findViewById(R.id.noDataLayout);
        mActionBar = getActivity().getActionBar();
        mCustomView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.home_custom_title_view, null);
        mImageViewWrite= (ImageView) mCustomView.findViewById(R.id.writePost);
        mImageViewWrite.setOnClickListener(this);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mListView = (ListView) view.findViewById(R.id.homeList);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        if(json.equals("")){
            downloadData();
        }else{
            doAdapter(json);
        }
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

    class MyListAdapter extends BaseAdapter {

        Context mContext;

        List<PostData> mList;

        MyListAdapter(Context context, List<PostData> list) {
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
                this.mImageViewPost.setBackgroundResource(R.drawable.icon_photo);
                this.mTextViewAuthor.setText(postData.author);
                String content = postData.content;
                this.mTextViewContent.setText(content.length() > 40 ? content.substring(0, 39) + "..." : content);
                this.mTextViewDate.setText(Operate.getFormatTime(postData.time));
                this.mTextViewCommentNum.setText(postData.commentNum + "");
                this.mTextViewCommentNum.setVisibility(View.INVISIBLE);
            }
        }
    }


}
