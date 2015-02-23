package com.meizu.lizhi.mygraduation.main.student.subject;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.PostData;
import com.meizu.lizhi.mygraduation.data.SubjectData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class StudentSubjectFragment extends Fragment {

    private final String TAG = StudentSubjectFragment.class.getName();

    ActionBar mActionBar;

    ListView mListView;

    MyListAdapter myListAdapter;

    private View mCustomView;

    private int mActionBarOptions;
    
    List<SubjectData> mList;

    RelativeLayout mRelativeLayoutFooter = null;

    final String actionUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/getSubject";


    void downData() {
        mList = new ArrayList<SubjectData>();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                                            SubjectData subjectData = new SubjectData();
                                            subjectData.id=value.getLong("id");
                                            subjectData.imageUrl= "http://" + StaticIp.IP + ":8080/graduationServlet/photo/subject/"+value.getString("photo");
                                            subjectData.name=value.getString("name");
                                            subjectData.author=value.getString("author");
                                            subjectData.score=value.getString("score");
                                            subjectData.school=value.getString("school");
                                            subjectData.academy=value.getString("academy");
                                            subjectData.detail=value.getString("detail");
                                            mList.add(subjectData);
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
        queue.add(getSubjectRequest);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,                    //获取数据服务端生成一个json，返回Url
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_subject, container, false);
        mActionBar = getActivity().getActionBar();
        mCustomView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.subject_custom_title_view, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mListView = (ListView) view.findViewById(R.id.subjectList);
        downData();
        mRelativeLayoutFooter = (RelativeLayout) LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.subject_footer, null);
        mListView.addFooterView(mRelativeLayoutFooter);
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
            TextView mTextViewScore;
            TextView mTextViewAuthor;
            TextView mTextViewSchoolAndAcademy;
            TextView mTextViewDescribe;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.subject_item, null);
                this.mImageViewSubject = (ImageView) view.findViewById(R.id.subjectImage);
                this.mTextViewName = (TextView) view.findViewById(R.id.subjectName);
                this.mTextViewScore = (TextView) view.findViewById(R.id.subjectScore);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewSchoolAndAcademy = (TextView) view.findViewById(R.id.schoolAndAcademy);
                this.mTextViewDescribe = (TextView) view.findViewById(R.id.describe);
                return view;
            }

            void putData(SubjectData subjectData) {
                this.mImageViewSubject.setBackgroundResource(R.drawable.ic_test);
                this.mTextViewName.setText(subjectData.name);
                this.mTextViewScore.setText(subjectData.score);
                this.mTextViewAuthor.setText(subjectData.author);
                this.mTextViewSchoolAndAcademy.setText(subjectData.school + " " + subjectData.academy);
                String detail = subjectData.detail;
                this.mTextViewDescribe.setText(detail.length() > 10 ? detail.substring(0, 8) + "..." : detail);
            }
        }
    }

}
