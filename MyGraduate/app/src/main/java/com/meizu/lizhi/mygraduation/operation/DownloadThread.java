package com.meizu.lizhi.mygraduation.operation;

import android.os.Handler;
import android.os.Message;

import com.meizu.lizhi.mygraduation.internet.StaticIp;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DownloadThread extends Thread {
    String name;
    String otherName;

    Handler mHandler;

    public DownloadThread(Handler handler,String name, String otherName) {
        mHandler=handler;
        this.name = name;
        this.otherName=otherName;
    }

    @Override
    public void run() {
        String url = "http://" + StaticIp.IP + ":8080/graduationServlet/resource/" + name;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpResponse=httpClient.execute(httpGet);
            StatusLine statusLine=httpResponse.getStatusLine();
            if(statusLine.getStatusCode()==200){
                File dir=new File("/sdcard/graduation/downloadFiles");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File("/sdcard/graduation/downloadFiles/",
                        otherName+ Operate.getSystemTime()+name.substring(name.lastIndexOf("."),name.length()));
                FileOutputStream outputStream=new FileOutputStream(file);
                InputStream inputStream=httpResponse.getEntity().getContent();
                byte b[]=new byte[1024];
                int j;
                while ((j = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, j);
                }
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.getConnectionManager().shutdown();
        }
        Message message=new Message();
        message.what=1;
        mHandler.sendMessage(message);
        super.run();
    }
}
