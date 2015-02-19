package com.meizu.lizhi.mygraduation.main.student.subject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.SubjectCommentData;

import java.util.ArrayList;
import java.util.List;

public class SubjectDetailActivity extends Activity {


    ListView mListView;

    List<SubjectCommentData> mList;

   MyListAdapter myListAdapter;

   RelativeLayout mRelativeLayoutHeader = null;

    void initView(){
        mListView= (ListView) findViewById(R.id.list);
        mRelativeLayoutHeader = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.subject_detail_header, null);
        mListView.addHeaderView(mRelativeLayoutHeader);
        downData();
    }

    void downData() {
        mList = new ArrayList<SubjectCommentData>();
        for (int i = 0; i < 10; i++) {
            SubjectCommentData subjectCommentData = new SubjectCommentData();
            subjectCommentData.photoUrl = "http://png";
            subjectCommentData.author = "李志";
            subjectCommentData.content = "啊哈哈哈机啊哈的哈简单哈觉得很阿娇大环境啊哈搭建";
            subjectCommentData.StarNum=3.5f;
            subjectCommentData.date=12232;
            mList.add(subjectCommentData);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        initView();
        myListAdapter=new MyListAdapter(this,mList);
        mListView.setAdapter(myListAdapter);
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
            ImageView mImageViewPhoto;
            TextView mTextViewAuthor;
            TextView mTextViewContent;
            RatingBar mRatingBar;
            TextView mImageViewDate;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.subject_comment_item, null);
                this.mImageViewPhoto = (ImageView) view.findViewById(R.id.imagePhoto);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewContent = (TextView) view.findViewById(R.id.content);
                this.mRatingBar= (RatingBar) view.findViewById(R.id.subjectRatingBar);
                this.mImageViewDate= (TextView) view.findViewById(R.id.date);
                return view;
            }

            void putData(SubjectCommentData subjectCommentData) {
                this.mImageViewPhoto.setBackground(getResources().getDrawable(R.drawable.ic_test));
                this.mTextViewAuthor.setText(subjectCommentData.author);
                this.mTextViewContent.setText(subjectCommentData.content);
                this.mRatingBar.setRating(subjectCommentData.StarNum);
                this.mImageViewDate.setText("2015-02-03 12:12");
            }
        }
    }

}
