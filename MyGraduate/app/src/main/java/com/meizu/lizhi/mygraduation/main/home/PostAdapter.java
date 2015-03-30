package com.meizu.lizhi.mygraduation.main.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.PostData;
import com.meizu.lizhi.mygraduation.operation.Operate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhi on 15-2-27.
 */
public class PostAdapter extends BaseAdapter {
    Context mContext;

    List<PostData> mList;

    Map<String, Bitmap> map;

    static final int MAP_SIZE = 10;

    public PostAdapter(Context context, List<PostData> list) {
        this.mList = list;
        this.mContext = context;
        this.map = new HashMap<String, Bitmap>();
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

        View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_item, null);
            this.mImageViewPost = (ImageView) view.findViewById(R.id.postImage);
            this.mTextViewAuthor = (TextView) view.findViewById(R.id.postAuthor);
            this.mTextViewDate = (TextView) view.findViewById(R.id.date);
            this.mTextViewContent = (TextView) view.findViewById(R.id.content);
            return view;
        }

        void putData(final PostData postData) {

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1: {
                            mImageViewPost.setImageBitmap(map.get(postData.photoUrl));
                        }
                    }
                    super.handleMessage(msg);
                }
            };
            if (!postData.photoUrl.equals("empty")) {
                if (map.containsKey(postData.photoUrl)) {
                    mImageViewPost.setImageBitmap(map.get(postData.photoUrl));
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadImage(postData.photoUrl);
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }else{
                mImageViewPost.setBackgroundResource(R.drawable.icon_photo);
            }
            this.mTextViewAuthor.setText(postData.author);
            String content = postData.content;
            this.mTextViewContent.setText(content.length() > 40 ? content.substring(0, 39) + "..." : content);
            this.mTextViewDate.setText(Operate.getFormatTime(postData.time));
        }

        private void loadImage(String url) {
            Log.e("vv",url);
            URL myFileUrl;
            HttpURLConnection connection;
            try {
                myFileUrl = new URL(url);
                connection = (HttpURLConnection) myFileUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                if (map.size() == MAP_SIZE) {
                    Iterator<String> iterator = map.keySet().iterator();
                    if (iterator.hasNext()) {
                        map.remove(iterator.next());
                    }
                }
                map.put(url, bitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
