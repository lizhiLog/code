package com.meizu.lizhi.mygraduation.main.student.subject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

public class SelectSubjectActivity extends Activity {

    ListView mListView;

    List<SubjectData> mList;

    MyListAdapter myListAdapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_subject);
        downData();
        mListView = (ListView)findViewById(R.id.list);
        myListAdapter = new MyListAdapter(this, mList);
        mListView.setAdapter(myListAdapter);
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
