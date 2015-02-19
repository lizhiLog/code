package com.meizu.lizhi.mygraduation.main.student.subject;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meizu.lizhi.mygraduation.R;

public class SubjectResourceActivity extends FragmentActivity implements View.OnClickListener {

    FragmentManager mFragmentManager;
    ActionBar mActionBar;

    ViewPager mViewPager;

    private View mCustomView;

    private int mActionBarOptions;

    MyFragmentPagerAdapter myFragmentPagerAdapter;

    ImageView mImageViewScrollOne;
    ImageView mImageViewScrollTwo;
    ImageView mImageViewScrollThree;
    ImageView mImageViewScrollFour;


    TextView mTextViewOne;
    TextView mTextViewTwo;
    TextView mTextViewThere;
    TextView mTextViewFour;


    int currentIndex;

    void initView(){
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPager.setPageMarginDrawable(android.R.drawable.divider_horizontal_bright);
        mFragmentManager=getSupportFragmentManager();

        myFragmentPagerAdapter=new MyFragmentPagerAdapter(mFragmentManager);
        mViewPager.setAdapter(myFragmentPagerAdapter);

        mActionBar=getActionBar();
        mCustomView = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);

        mImageViewScrollOne = (ImageView) mCustomView.findViewById(R.id.scroll_1);
        mImageViewScrollTwo = (ImageView) mCustomView.findViewById(R.id.scroll_2);
        mImageViewScrollThree = (ImageView) mCustomView.findViewById(R.id.scroll_3);
        mImageViewScrollFour = (ImageView) mCustomView.findViewById(R.id.scroll_4);

        mTextViewOne= (TextView) mCustomView.findViewById(R.id.tab_text_1);
        mTextViewTwo= (TextView) mCustomView.findViewById(R.id.tab_text_2);
        mTextViewThere= (TextView) mCustomView.findViewById(R.id.tab_text_3);
        mTextViewFour= (TextView) mCustomView.findViewById(R.id.tab_text_4);

        mActionBar.setCustomView(mCustomView);

        setCurrentScroll(0);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);

        mTextViewOne.setOnClickListener(this);
        mTextViewTwo.setOnClickListener(this);
        mTextViewThere.setOnClickListener(this);
        mTextViewFour.setOnClickListener(this);

    }

    private void setCurrentScroll(int selection) {
        currentIndex=selection;
        if (mImageViewScrollOne != null && mImageViewScrollTwo != null
                && mImageViewScrollThree != null && mImageViewScrollFour != null) {
            mImageViewScrollOne.setVisibility(selection == 0 ? View.VISIBLE : View.INVISIBLE);
            mImageViewScrollTwo.setVisibility(selection == 1 ? View.VISIBLE : View.INVISIBLE);
            mImageViewScrollThree.setVisibility(selection == 2 ? View.VISIBLE : View.INVISIBLE);
            mImageViewScrollFour.setVisibility(selection == 3 ? View.VISIBLE : View.INVISIBLE);
            mTextViewOne.setTextColor(selection==0? Color.rgb(7, 151, 237):Color.GRAY);
            mTextViewTwo.setTextColor(selection==1?Color.rgb(7,151,237):Color.GRAY);
            mTextViewThere.setTextColor(selection==2?Color.rgb(7,151,237):Color.GRAY);
            mTextViewFour.setTextColor(selection==3?Color.rgb(7,151,237):Color.GRAY);


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        initView();
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {
                    }
                    @Override
                    public void onPageSelected(int position) {
                        setCurrentScroll(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
                }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        mActionBarOptions = mActionBar.getDisplayOptions();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActionBar.setDisplayOptions(mActionBarOptions, ActionBar.DISPLAY_SHOW_CUSTOM | mActionBarOptions);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab_text_1:
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.tab_text_2:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.tab_text_3:
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.tab_text_4:
                mViewPager.setCurrentItem(3, false);
                break;
        }
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        Fragment fragment;

        VideoFragment mVideoFragment;
        DocumentFragment mDocumentFragment;
        CourseWareFragment mCourseWareFragment;
        ExcelFragment mExcelFragment;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mVideoFragment=new VideoFragment();
            mDocumentFragment=new DocumentFragment();
            mCourseWareFragment=new CourseWareFragment();
            mExcelFragment=new ExcelFragment();
        }


        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    this.fragment=mDocumentFragment;
                    break;
                case 1:
                    this.fragment=mVideoFragment;
                    break;
                case 2:
                    this.fragment=mCourseWareFragment;
                    break;
                case 3:
                    this.fragment=mExcelFragment;
                    break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }
}
