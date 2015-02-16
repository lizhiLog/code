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
import com.meizu.lizhi.mygraduation.data.CommentData;
import com.meizu.lizhi.mygraduation.data.ResourceData;
import com.meizu.lizhi.mygraduation.data.SubjectData;

import java.util.ArrayList;
import java.util.List;

public class SubjectDetailActivity extends Activity {


    ListView mListView;

    List<CommentData> mList;

   MyListAdapter myListAdapter;

    void initView(){
        mListView= (ListView) findViewById(R.id.list);
        downData();
    }

    void downData() {
        mList = new ArrayList<CommentData>();
        for (int i = 0; i < 10; i++) {
            CommentData commentData = new CommentData();
            commentData.photoUrl = "http://png";
            commentData.author = "李志";
            commentData.content = "啊哈哈哈机啊哈的哈简单哈觉得很阿娇大环境啊哈搭建";
            commentData.date=12232;
            mList.add(commentData);
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

        List<CommentData> mList;

        MyListAdapter(Context context, List<CommentData> list) {
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
            viewHolder.putData((CommentData) getItem(position));
            return convertView;
        }


        class ViewHolder {
            ImageView mImageViewPhoto;
            TextView mTextViewAuthor;
            TextView mTextViewContent;
            TextView mImageViewDate;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
                this.mImageViewPhoto = (ImageView) view.findViewById(R.id.imagePhoto);
                this.mTextViewAuthor = (TextView) view.findViewById(R.id.author);
                this.mTextViewContent = (TextView) view.findViewById(R.id.content);
                this.mImageViewDate= (TextView) view.findViewById(R.id.date);
                return view;
            }

            void putData(CommentData commentData) {
                this.mImageViewPhoto.setBackground(getResources().getDrawable(R.drawable.ic_test));
                this.mTextViewAuthor.setText(commentData.author);
                this.mTextViewContent.setText(commentData.content);
                this.mImageViewDate.setText("2015-02-03 12:12");
            }
        }
    }

}
