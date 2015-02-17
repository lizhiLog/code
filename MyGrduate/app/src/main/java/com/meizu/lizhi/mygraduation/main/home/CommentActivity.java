package com.meizu.lizhi.mygraduation.main.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.PostCommentData;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends Activity {

    ListView mListView;

    List<PostCommentData> mList;

    MyListAdapter myListAdapter;

    RelativeLayout mRelativeLayoutHeader = null;

    void initView(){
        mListView= (ListView) findViewById(R.id.list);
        mRelativeLayoutHeader = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.comment_detail_header, null);
        mListView.addHeaderView(mRelativeLayoutHeader);
        downData();

    }

    void downData() {
        mList = new ArrayList<PostCommentData>();
        for (int i = 0; i < 10; i++) {
            PostCommentData postCommentData = new PostCommentData();
            postCommentData.photoUrl = "http://png";
            postCommentData.author = "李志";
            postCommentData.content = "啊哈哈哈机啊哈的哈简单哈觉得很阿娇大环境啊哈搭建";
            postCommentData.date=12232;
            mList.add(postCommentData);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        myListAdapter=new MyListAdapter(this,mList);
        mListView.setAdapter(myListAdapter);

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

            void putData(PostCommentData postCommentData) {
                this.mImageViewPhoto.setBackground(getResources().getDrawable(R.drawable.ic_test));
                this.mTextViewAuthor.setText(postCommentData.author);
                this.mTextViewContent.setText(postCommentData.content);
                this.mImageViewDate.setText("2015-02-03 12:12");
            }
        }
    }

}
