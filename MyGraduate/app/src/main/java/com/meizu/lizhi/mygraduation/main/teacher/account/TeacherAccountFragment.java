package com.meizu.lizhi.mygraduation.main.teacher.account;



import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.internet.StaticIp;
import com.meizu.lizhi.mygraduation.main.home.MyCommentPostActivity;
import com.meizu.lizhi.mygraduation.main.home.MySendPostActivity;
import com.meizu.lizhi.mygraduation.main.student.account.AccountActivity;
import com.meizu.lizhi.mygraduation.operation.CurrentUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class TeacherAccountFragment extends Fragment implements View.OnClickListener {


    ActionBar mActionBar;

    private int mActionBarOptions;

    ImageView mImageViewPhoto;
    TextView mTextViewName;
    RelativeLayout mRelativeLayoutUser;
    RelativeLayout mRelativeLayoutMyCommentPost;
    RelativeLayout mRelativeLayoutMyWritePost;

    RequestQueue queue;

    Map<String, Bitmap> map;

    static final int MAP_SIZE = 10;

    String photoUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queue= Volley.newRequestQueue(getActivity());
        map = new HashMap<String, Bitmap>();
        View view=inflater.inflate(R.layout.fragment_teacher_account, container, false);
        mRelativeLayoutUser= (RelativeLayout) view.findViewById(R.id.userLayout);
        mImageViewPhoto= (ImageView) view.findViewById(R.id.photo);
        mTextViewName= (TextView) view.findViewById(R.id.userName);
        mRelativeLayoutMyCommentPost= (RelativeLayout) view.findViewById(R.id.myCommentPostLayout);
        mRelativeLayoutMyWritePost= (RelativeLayout) view.findViewById(R.id.myWritePostLayout);
        mActionBar = getActivity().getActionBar();
        mActionBar.setTitle("个人中心");
        mActionBar.setCustomView(null);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mRelativeLayoutUser.setOnClickListener(this);
        mRelativeLayoutMyWritePost.setOnClickListener(this);
        mRelativeLayoutMyCommentPost.setOnClickListener(this);
        initData();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.userLayout:{
                Intent intent=new Intent(getActivity(),AccountActivity.class);
                startActivity(intent);
            }break;
            case R.id.myWritePostLayout:{
                Intent intent=new Intent(getActivity(), MySendPostActivity.class);
                startActivity(intent);
            }break;
            case R.id.myCommentPostLayout:{
                Intent intent=new Intent(getActivity(), MyCommentPostActivity.class);
                startActivity(intent);
            }
        }
    }


    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    mImageViewPhoto.setImageBitmap(map.get(photoUrl));
                }
            }
            super.handleMessage(msg);
        }
    };

    public void initData(){
        String photo= CurrentUser.getCurrentUserPhoto(getActivity());
        if(photo.equals("empty")){
            mImageViewPhoto.setBackgroundResource(R.drawable.icon_photo);
        }else{
             photoUrl="http://" + StaticIp.IP + ":8080/graduationServlet/photo/user/"+photo;
            if(map.containsKey(photoUrl)){
                mImageViewPhoto.setImageBitmap(map.get(photoUrl));
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadImage(photoUrl);
                        Message message = new Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        }
        mTextViewName.setText(CurrentUser.getCurrentUserName(getActivity()));
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

    public void loadImage(String url) {
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
