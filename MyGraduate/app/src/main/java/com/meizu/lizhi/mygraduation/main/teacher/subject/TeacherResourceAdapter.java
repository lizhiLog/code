package com.meizu.lizhi.mygraduation.main.teacher.subject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.data.ResourceData;
import com.meizu.lizhi.mygraduation.internet.StaticIp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhi on 15-2-27.
 */
public class TeacherResourceAdapter extends BaseAdapter {
    Context mContext;

    List<ResourceData> mList;

    final String deleteResourceUrl = "http://" + StaticIp.IP + ":8080/graduationServlet/deleteResource";

    Handler mHandler;


    public TeacherResourceAdapter(Context context, List<ResourceData> list,Handler handler) {
        this.mList = list;
        this.mContext = context;
        this.mHandler=handler;
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
            convertView = viewHolder.createView(mContext,position);
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
        RelativeLayout mRelativeLayout;

        View createView(Context context, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.recourse_item, null);
            this.mRelativeLayout= (RelativeLayout) view.findViewById(R.id.resourceItemLayout);
            this.mTextViewTitle = (TextView) view.findViewById(R.id.resourceTitle);
            this.mTextViewDescribe = (TextView) view.findViewById(R.id.resourceDescribe);
            this.mImageViewHandle= (ImageView) view.findViewById(R.id.handleImage);
            this.mRelativeLayout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Dialog dialog = new AlertDialog.Builder(mContext)
                                    .setIcon(android.R.drawable.btn_star)//设置对话框图标
                                    .setMessage("你真的要删除该资源吗?")
                                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            deleteResource(mList.get(position).id);
                                        }
                                    })
                                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                            dialog.show();
                        }
                    }
            );
            return view;
        }

        void putData(ResourceData resourceData) {
            this.mTextViewTitle.setText(resourceData.title);
            this.mTextViewDescribe.setText(resourceData.detail);
            this.mImageViewHandle.setBackgroundResource(R.drawable.icon_subject_delete);
        }

        void deleteResource(final long id){
            RequestQueue queue= Volley.newRequestQueue(mContext);
            StringRequest deleteResourceRequest=new StringRequest(
                    Request.Method.POST,deleteResourceUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                int code=jsonObject.getInt("code");
                                if(code==60){
                                    int result=jsonObject.getInt("result");
                                    switch (result){
                                        case 0:{
                                            Message message=new Message();
                                            message.what=1;
                                            mHandler.sendMessage(message);
                                        }break;
                                        case 1:{
                                            Toast.makeText(mContext,"删除过程出现错误!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext,"网络接连错误,请检查您的网络",Toast.LENGTH_SHORT).show();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    JSONObject info = new JSONObject();
                    try {
                        info.put("code", 60);
                        JSONObject value = new JSONObject();
                        value.put("id", id);
                        info.put("data", value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    map.put("json", info.toString().trim());
                    return map;
                }
            };
            queue.add(deleteResourceRequest);
        }
    }
}
