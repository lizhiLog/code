package com.meizu.lizhi.mygraduation.main.student.home;


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
import com.meizu.lizhi.mygraduation.data.PostData;
import com.meizu.lizhi.mygraduation.data.SubjectData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    ActionBar mActionBar;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mActionBar = getActivity().getActionBar();
        mActionBar.setTitle("社区");
        downData();
        mListView = (ListView) view.findViewById(R.id.homeList);
        myListAdapter = new MyListAdapter(getActivity().getApplicationContext(), mList);
        mListView.setAdapter(myListAdapter);
        return view;
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
                this.mImageViewPost.setBackground(getResources().getDrawable(R.drawable.ic_test));
                this.mTextViewAuthor.setText(postData.author);
                String content=postData.content;
                this.mTextViewContent.setText(content.length()>40?content.substring(0,39)+"...":content);
                this.mTextViewDate.setText("2015-02-03 12:23");
                this.mTextViewCommentNum.setText(postData.commentNum+"");
            }
        }
    }


}
