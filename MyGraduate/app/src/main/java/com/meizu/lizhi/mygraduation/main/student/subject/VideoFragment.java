package com.meizu.lizhi.mygraduation.main.student.subject;



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
import com.meizu.lizhi.mygraduation.data.ResourceData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class VideoFragment extends Fragment {
    ListView mListView;

    List<ResourceData> mList;

    void downData() {
        mList = new ArrayList<ResourceData>();
        for (int i = 0; i < 20; i++) {
            ResourceData resourceData=new ResourceData();
            resourceData.imageUrl="http";
            resourceData.title="视频文件";
            resourceData.describe="视频文件描述";
            mList.add(resourceData);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_resource, container, false);
        mListView= (ListView) view.findViewById(R.id.documentList);
        downData();
        MyListAdapter myListAdapter=new MyListAdapter(getActivity().getApplicationContext(),mList);
        mListView.setAdapter(myListAdapter);
        return view;
    }

    class MyListAdapter extends BaseAdapter {

        Context mContext;

        List<ResourceData> mList;

        MyListAdapter(Context context, List<ResourceData> list) {
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
            viewHolder.putData((ResourceData) getItem(position));
            return convertView;
        }


        class ViewHolder {
            ImageView mImageViewResource;
            TextView mTextViewTitle;
            TextView mTextViewDescribe;

            View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.recourse_item, null);
                this.mImageViewResource = (ImageView) view.findViewById(R.id.resourceImage);
                this.mTextViewTitle = (TextView) view.findViewById(R.id.resourceTitle);
                this.mTextViewDescribe = (TextView) view.findViewById(R.id.resourceDescribe);
                return view;
            }

            void putData(ResourceData resourceData) {
                this.mImageViewResource.setBackground(getResources().getDrawable(R.drawable.ic_test));
                this.mTextViewTitle.setText(resourceData.title);
                this.mTextViewDescribe.setText(resourceData.describe);
            }
        }
    }

}
