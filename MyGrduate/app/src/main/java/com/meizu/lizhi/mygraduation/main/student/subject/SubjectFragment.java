package com.meizu.lizhi.mygraduation.main.student.subject;


import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.SubjectData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectFragment extends Fragment {

    private final String TAG = SubjectFragment.class.getName();

    ActionBar mActionBar;

    ListView mListView;

    List<SubjectData> mList;

    MyListAdapter myListAdapter;

    private View mCustomView;

    private int mActionBarOptions;

    void downData() {
        mList = new ArrayList<SubjectData>();
        for (int i = 0; i < 10; i++) {
            SubjectData subjectData = new SubjectData();
            subjectData.imageUrl = "http://png";
            subjectData.name = "生命与健康";
            subjectData.score = "4.6";
            subjectData.author = "魏开平";
            subjectData.school = "华中师范大学";
            subjectData.academy = "计算机学院";
            subjectData.detail = "简介啊哈哈哈机啊哈的哈简单哈觉得很阿娇大环境啊哈搭建";
            mList.add(subjectData);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,                    //获取数据服务端生成一个json，返回Url
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject, container, false);
        mActionBar = getActivity().getActionBar();
        mCustomView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.subject_custom_title_view, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        downData();
        mListView = (ListView) view.findViewById(R.id.subjectList);
        myListAdapter = new MyListAdapter(getActivity().getApplicationContext(), mList);
        mListView.setAdapter(myListAdapter);
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
                this.mImageViewSubject.setBackground(getResources().getDrawable(R.drawable.ic_test));
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
