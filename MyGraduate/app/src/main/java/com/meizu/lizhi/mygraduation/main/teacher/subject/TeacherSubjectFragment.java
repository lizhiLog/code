package com.meizu.lizhi.mygraduation.main.teacher.subject;



import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
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

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class TeacherSubjectFragment extends Fragment {


    static final String TAG=TeacherSubjectFragment.class.getName();

    ActionBar mActionBar;

    ListView mListView;

    List<SubjectData> mList;

    MyListAdapter myListAdapter;

    private View mCustomView;

    private int mActionBarOptions;

    RelativeLayout mRelativeLayoutFooter;

    RelativeLayout mRelativeLayoutNoData=null;

    RequestQueue queue;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getTeacherSubject";

    void downData() {
        mList = new ArrayList<SubjectData>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
        StringRequest getSubjectRequest = new StringRequest(Request.Method.POST, actionUrl,
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
                            if (code == 36) {
                                switch (result) {
                                    case 0: {
                                        JSONArray data = obj.getJSONArray("data");
                                        Log.e(TAG, "data:" + data);
                                        Log.e(TAG, "downLoad over");
                                        Log.e(TAG,data.length()+"");
                                        if(data.length()==0){
                                            mListView.setVisibility(View.GONE);
                                            mRelativeLayoutNoData.setVisibility(View.VISIBLE);
                                            return;
                                        }else{
                                            mRelativeLayoutNoData.setVisibility(View.GONE);
                                            mListView.setVisibility(View.VISIBLE);
                                        }
                                        for (int i = 0; i < data.length(); i++) {
                                            Log.e(TAG, "" + i);
                                            JSONObject value = data.getJSONObject(i);
                                            Log.e(TAG, "" + value.toString().trim());
                                            SubjectData subjectData = new SubjectData();
                                            subjectData.id=value.getLong("id");
                                            subjectData.imageUrl= "http://" + StaticIp.IP + ":8080/graduationServlet/photo/subject/"+value.getString("photo");
                                            subjectData.name=value.getString("name");
                                            subjectData.author=value.getString("author");
                                            subjectData.school=value.getString("school");
                                            subjectData.academy=value.getString("academy");
                                            subjectData.detail=value.getString("detail");
                                            mList.add(subjectData);
                                            Log.e(TAG, "" + i);
                                        }
                                        Log.e(TAG, "mListSize:" + mList.size());
                                        myListAdapter = new MyListAdapter(getActivity().getApplicationContext(), mList);
                                        mListView.setAdapter(myListAdapter);
                                        mListView.addFooterView(mRelativeLayoutFooter);
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
                map.put("json",getJson());
                return map;
            }

        };
        queue.add(getSubjectRequest);
    }

    public String getJson(){
        JSONObject info = new JSONObject();
        try {
            info.put("code", 36);
            JSONObject value = new JSONObject();
            value.put("id", CurrentUser.getCurentUserId(getActivity()));
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_subject, container, false);
        mRelativeLayoutNoData= (RelativeLayout) view.findViewById(R.id.noDataLayout);
        mActionBar = getActivity().getActionBar();
        mCustomView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.subject_custom_title_view, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mListView = (ListView) view.findViewById(R.id.list);
        mRelativeLayoutFooter = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.teacher_subject_footer, null);
        mListView.addFooterView(mRelativeLayoutFooter);
        downData();
        return view;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = viewHolder.createView(mContext);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.putData((SubjectData) getItem(position));
            viewHolder.mRelativeLayoutSubjectItem.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getActivity(),TeacherSubjectResourceActivity.class);
                            intent.putExtra("id",mList.get(position).id);
                            startActivity(intent);
                        }
                    }
            );
            viewHolder.mTextViewDetail.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getActivity(),TeacherSubjectDetailActivity.class);
                            intent.putExtra("id",mList.get(position).id);
                            startActivity(intent);
                        }
                    }
            );
            return convertView;

        }


        class ViewHolder {
            RelativeLayout mRelativeLayoutSubjectItem;
            ImageView mImageViewSubject;
            TextView mTextViewName;
            TextView mTextViewScore;
            TextView mTextViewAuthor;
            TextView mTextViewSchoolAndAcademy;
            TextView mTextViewDescribe;
            TextView mTextViewDetail;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.subject_item, null);
                this.mRelativeLayoutSubjectItem= (RelativeLayout) view.findViewById(R.id.subjectItemLayout);
                this.mImageViewSubject = (ImageView) view.findViewById(R.id.subjectImage);
                this.mTextViewName = (TextView) view.findViewById(R.id.subjectName);
                this.mTextViewScore = (TextView) view.findViewById(R.id.subjectScore);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewSchoolAndAcademy = (TextView) view.findViewById(R.id.schoolAndAcademy);
                this.mTextViewDescribe = (TextView) view.findViewById(R.id.describe);
                this.mTextViewDetail= (TextView) view.findViewById(R.id.detail);
                return view;
            }

            void putData(SubjectData subjectData) {
                initPhoto(subjectData.imageUrl);
                this.mTextViewName.setText(subjectData.name);
                this.mTextViewAuthor.setText(subjectData.author);
                String schoolAndAcademy=subjectData.school + " " + subjectData.academy;
                this.mTextViewSchoolAndAcademy.setText(schoolAndAcademy.length()>12?schoolAndAcademy.substring(0,11)+"..":schoolAndAcademy);
                String detail = subjectData.detail;
                this.mTextViewDescribe.setText(detail.length() > 11 ? detail.substring(0, 10) + "..." : detail);
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

