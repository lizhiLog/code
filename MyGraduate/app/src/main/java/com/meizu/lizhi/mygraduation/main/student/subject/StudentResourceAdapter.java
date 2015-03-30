package com.meizu.lizhi.mygraduation.main.student.subject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.ResourceData;

import java.util.List;

/**
 * Created by lizhi on 15-2-27.
 */
public class StudentResourceAdapter extends BaseAdapter {
    Context mContext;

    List<ResourceData> mList;

    public StudentResourceAdapter(Context context, List<ResourceData> list) {
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
        TextView mTextViewTitle;
        TextView mTextViewDescribe;
        ImageView mImageViewHandle;

        View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.recourse_item, null);
            this.mTextViewTitle = (TextView) view.findViewById(R.id.resourceTitle);
            this.mTextViewDescribe = (TextView) view.findViewById(R.id.resourceDescribe);
            this.mImageViewHandle= (ImageView) view.findViewById(R.id.handleImage);
            return view;
        }

        void putData(ResourceData resourceData) {
            this.mTextViewTitle.setText(resourceData.title);
            this.mTextViewDescribe.setText(resourceData.detail);
            this.mImageViewHandle.setBackgroundResource(R.drawable.icon_download);
        }
    }
}
