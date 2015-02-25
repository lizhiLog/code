package com.meizu.lizhi.mygraduation.operation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.main.teacher.subject.AddResourceActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SelectFileActivity extends Activity {

    List<HashMap<String,Object>> mList;
    ListView mListView;

    void initData(int type){
        File dir = new File("/sdcard/graduation/uploadFile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File[] files=new File("/sdcard/graduation/uploadFile/").listFiles();
        mList=new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<files.length;i++){
            File f=files[i];
            String path=f.getPath();
            String fileEnd=path.substring(path.lastIndexOf(".") + 1,path.length()).toLowerCase();
            if(f.isFile()&&Operate.checkFileEnd(fileEnd)==type){
                HashMap<String,Object> map=new HashMap<String, Object>();
                map.put("path",path);
                mList.add(map);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        mListView= (ListView) findViewById(R.id.list);
        Intent intent=getIntent();
        initData(intent.getIntExtra("type",0));
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,mList,R.layout.file_item,new String[]{"path"},new int[]{R.id.path});
        mListView.setAdapter(simpleAdapter);
        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(SelectFileActivity.this, AddResourceActivity.class);
                        intent.putExtra("path", (String) mList.get(position).get("path"));
                        setResult(2,intent);
                        SelectFileActivity.this.finish();
                    }
                }
        );
    }
}
