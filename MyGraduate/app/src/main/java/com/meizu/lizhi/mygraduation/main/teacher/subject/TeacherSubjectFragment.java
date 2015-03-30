package com.meizu.lizhi.mygraduation.main.teacher.subject;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.meizu.lizhi.mygraduation.data.SubjectData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.operation.CurrentUser;

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

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class TeacherSubjectFragment extends Fragment {


    static final String TAG = TeacherSubjectFragment.class.getName();

    ActionBar mActionBar;

    ListView mListView;

    List<SubjectData> mList;

    MyListAdapter myListAdapter;

    private int mActionBarOptions;

    RelativeLayout mRelativeLayoutFooter;

    RelativeLayout mRelativeLayoutNetworkWrong;

    RequestQueue queue;

    Map<String, Bitmap> map;

    static final int MAP_SIZE = 10;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getTeacherSubject";

    String json = "";

    SwipeRefreshLayout mRefreshLayout;


    void downloadData() {
        mRefreshLayout.setRefreshing(true);
        StringRequest getSubjectRequest = new StringRequest(Request.Method.POST, actionUrl,
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
                map.put("json", getJson());
                return map;
            }

        };
        queue.add(getSubjectRequest);
    }

    public void doAdapter(String json) {
        mList = new ArrayList<SubjectData>();
        mList.clear();
        try {
            JSONObject obj = new JSONObject(json);
            int code = obj.getInt("code");
            int result = obj.getInt("result");
            if (code == 36) {
                switch (result) {
                    case 0: {
                        JSONArray data = obj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            SubjectData subjectData = new SubjectData();
                            subjectData.id = value.getLong("id");
                            subjectData.imageUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/photo/subject/" + value.getString("photo");
                            subjectData.name = value.getString("name");
                            subjectData.author = value.getString("author");
                            subjectData.school = value.getString("school");
                            subjectData.academy = value.getString("academy");
                            subjectData.detail = value.getString("detail");
                            mList.add(subjectData);
                        }
                        myListAdapter = new MyListAdapter(getActivity().getApplicationContext(), mList);
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

    public String getJson() {
        JSONObject info = new JSONObject();
        try {
            info.put("code", 36);
            JSONObject value = new JSONObject();
            value.put("id", CurrentUser.getCurrentUserId(getActivity()));
            info.put("data", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = info.toString();
        return json;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        map = new HashMap<String, Bitmap>();
        View view = inflater.inflate(R.layout.fragment_teacher_subject, container, false);
        mRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        mRelativeLayoutNetworkWrong = (RelativeLayout) view.findViewById(R.id.networkWrongLayout);
        mRelativeLayoutNetworkWrong.setVisibility(View.GONE);
        mActionBar = getActivity().getActionBar();
        mActionBar.setTitle("我的课程");
        mActionBar.setCustomView(null);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mListView = (ListView) view.findViewById(R.id.list);
        mRelativeLayoutFooter = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.teacher_subject_footer, null);
        mListView.addFooterView(mRelativeLayoutFooter);
        mRelativeLayoutFooter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), CreateSubjectActivity.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
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
        mListView.setOnScrollListener(new MyScrollListener());
        if (json.equals("")) {
            downloadData();
        } else {
            doAdapter(json);
        }
        return view;
    }

    class MyScrollListener implements AbsListView.OnScrollListener {

        boolean isFullPull = false;

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == SCROLL_STATE_IDLE) {
                if (isFullPull == true) {
                    isFullPull = false;
                    downloadData();
                }
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == 2) {
                downloadData();
            }
        }
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
                            Intent intent = new Intent(getActivity(), TeacherSubjectResourceActivity.class);
                            intent.putExtra("id", mList.get(position).id);
                            startActivity(intent);
                        }
                    }
            );
            viewHolder.mTextViewDetail.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), TeacherSubjectDetailActivity.class);
                            intent.putExtra("id", mList.get(position).id);
                            startActivityForResult(intent, 2);
                        }
                    }
            );
            return convertView;

        }


        class ViewHolder {
            RelativeLayout mRelativeLayoutSubjectItem;
            ImageView mImageViewSubject;
            TextView mTextViewName;
            TextView mTextViewAuthor;
            TextView mTextViewSchoolAndAcademy;
            TextView mTextViewDescribe;
            TextView mTextViewDetail;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.subject_item, null);
                this.mRelativeLayoutSubjectItem = (RelativeLayout) view.findViewById(R.id.subjectItemLayout);
                this.mImageViewSubject = (ImageView) view.findViewById(R.id.subjectImage);
                this.mTextViewName = (TextView) view.findViewById(R.id.subjectName);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewSchoolAndAcademy = (TextView) view.findViewById(R.id.schoolAndAcademy);
                this.mTextViewDescribe = (TextView) view.findViewById(R.id.describe);
                this.mTextViewDetail = (TextView) view.findViewById(R.id.detail);
                return view;
            }

            void putData(final SubjectData subjectData) {

                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 1: {
                                mImageViewSubject.setImageBitmap(map.get(subjectData.imageUrl));
                            }
                        }
                        super.handleMessage(msg);
                    }
                };
                if (map.containsKey(subjectData.imageUrl)) {
                    mImageViewSubject.setImageBitmap(map.get(subjectData.imageUrl));
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadImage(subjectData.imageUrl);
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }).start();
                }

                this.mTextViewName.setText(subjectData.name);
                this.mTextViewAuthor.setText(subjectData.author);
                String schoolAndAcademy = subjectData.school + " " + subjectData.academy;
                this.mTextViewSchoolAndAcademy.setText(schoolAndAcademy.length() > 12 ? schoolAndAcademy.substring(0, 11) + ".." : schoolAndAcademy);
                String detail = subjectData.detail;
                this.mTextViewDescribe.setText(detail.length() > 15 ? detail.substring(0, 14) + "..." : detail);
            }


            private void loadImage(String url) {
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
    }

}

