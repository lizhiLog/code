package com.meizu.lizhi.mygraduation.main.teacher.account;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meizu.lizhi.mygraduation.R;

/**
 * Created by lizhi on 15-3-2.
 */
public class ExitPopWindow extends PopupWindow {
    private TextView mTextViewChange;
    private TextView mTextViewExit;
    private TextView mTextViewCancel;

    private View customView;

    public ExitPopWindow(Activity context,View.OnClickListener itemsOnClick){
        customView= LayoutInflater.from(context).inflate(R.layout.exit_dialog_layout,null);
        mTextViewChange= (TextView) customView.findViewById(R.id.change);
        mTextViewExit= (TextView) customView.findViewById(R.id.exit);
        mTextViewCancel= (TextView) customView.findViewById(R.id.cancel);
        mTextViewChange.setOnClickListener(itemsOnClick);
        mTextViewExit.setOnClickListener(itemsOnClick);
        mTextViewCancel.setOnClickListener(itemsOnClick);

        this.setContentView(customView);
        this.setWidth(LinearLayout.LayoutParams.FILL_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.mystyle);

        customView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int height = customView.findViewById(R.id.popLayout).getTop();
                        int y=(int) event.getY();
                        if(event.getAction()==MotionEvent.ACTION_UP){
                            if(y<height){
                                dismiss();
                            }
                        }
                        return true;
                    }
                }
        );
    }

}
