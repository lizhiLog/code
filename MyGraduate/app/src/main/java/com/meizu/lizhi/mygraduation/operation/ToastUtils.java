package com.meizu.lizhi.mygraduation.operation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meizu.lizhi.mygraduation.R;

/**
 * Created by lizhi on 15-3-20.
 */
@SuppressWarnings("unused")
public class ToastUtils {
    public static void showToast(Context context,String text){
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.custom_toast_view, null);
        Toast toast=new Toast(context);
        toast.setView(toastRoot);
        TextView tv=(TextView)toastRoot.findViewById(R.id.TextViewInfo);
        tv.setTextSize(16);
        tv.setText(text);
        toast.show();
    }
}
