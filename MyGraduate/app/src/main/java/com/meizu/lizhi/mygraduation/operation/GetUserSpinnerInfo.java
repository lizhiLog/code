package com.meizu.lizhi.mygraduation.operation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhi on 15-2-26.
 */
public class GetUserSpinnerInfo {
    String[] school=new String[]{"华中科技大学","武汉大学","华中师范大学","武汉理工大学"};
    String[] academy=new String[]{"计算机学院","化学学院","文学院","外国语学院","物理学院","生命与科学学院","马克思主义学院"};
    Map<String,Integer> map=new HashMap<String,Integer>();
    Map<String,Integer> map1=new HashMap<String,Integer>();

    public GetUserSpinnerInfo(){
        for(int i=0;i<school.length;i++){
            map.put(school[i],i);
        }
        for(int i=0;i<academy.length;i++){
            map1.put(academy[i],i);
        }
    }

    public int getSchoolPosition(String school){
        return map.get(school);
    }
    public int getAcademyPosition(String academy){
        return map1.get(academy);
    }

}
