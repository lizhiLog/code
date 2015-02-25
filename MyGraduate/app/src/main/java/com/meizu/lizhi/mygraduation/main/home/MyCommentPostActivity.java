package com.meizu.lizhi.mygraduation.main.home;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.PostCommentData;
import com.meizu.lizhi.mygraduation.data.PostData;

import java.util.ArrayList;
import java.util.List;

public class MyCommentPostActivity extends Activity {

    ListView mListView;

    List<PostData> mList;

    MyListAdapter myListAdapter;

    void downData() {
        mList = new ArrayList<PostData>();
        for (int i = 0; i < 10; i++) {
            PostData postData = new PostData();
            postData.photoUrl = "http://png";
            postData.content = "交互分阶段更高的如果如果广东人个人大概广东人个人个个人大股东如果广东人广东阶段更高的如果如果广东人个人大概广东人个人个个人大股东如果广东人人";
            postData.author = "上官飞燕";
            postData.time = 1223;
            postData.commentNum = 11;
            mList.add(postData);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment_post);
        downData();
        mListView = (ListView)findViewById(R.id.list);
        myListAdapter = new MyListAdapter(this, mList);
        mListView.setAdapter(myListAdapter);
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
                String content=postData.content;
                this.mTextViewContent.setText(content.length()>40?content.substring(0,39)+"...":content);
                this.mTextViewDate.setText("2015-02-03 12:23");
                this.mTextViewCommentNum.setText(postData.commentNum+"");
            }
        }
    }


}
