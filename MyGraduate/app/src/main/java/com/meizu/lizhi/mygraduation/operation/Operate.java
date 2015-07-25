package com.meizu.lizhi.mygraduation.operation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lizhi on 15-2-23.
 */
public class Operate {
    public static String getFormatTime(long time){
        Date date=new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    public static long getSystemTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    public static int checkFileEnd(String fileEnd){
        if(fileEnd.equals("zip")||fileEnd.equals("rar")||fileEnd.equals("doc")||fileEnd.equals("md")
                ||fileEnd.equals("pdf")||fileEnd.equals("docx")||fileEnd.equals("txt")){
            return 1;
        }
        if(fileEnd.equals("ppt")||fileEnd.equals("pptx")){
            return 2;
        }
        if(fileEnd.equals("mp4")||fileEnd.equals("rmvb")
                ||fileEnd.equals("avi")||fileEnd.equals("3gp")||fileEnd.equals("flv")){
            return 3;
        }
        if(fileEnd.equals("jpeg")||fileEnd.equals("jpg")||fileEnd.equals("png")||fileEnd.equals("gif")
                ||fileEnd.equals("xls")||fileEnd.equals("xlsx")){
            return 4;
        }
        return 0;
    }
}
