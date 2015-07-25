package com.meizu.lizhi.mygraduation.main.student;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.meizu.flyme.blur.drawable.BlurDrawable;
import com.meizu.flyme.reflect.StatusBarProxy;
import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.login.LoginActivity;
import com.meizu.lizhi.mygraduation.main.student.account.StudentAccountFragment;
import com.meizu.lizhi.mygraduation.main.home.HomeFragment;
import com.meizu.lizhi.mygraduation.main.student.subject.StudentSubjectFragment;
import com.meizu.lizhi.mygraduation.main.teacher.account.ExitPopWindow;
import com.meizu.smartbar.SmartBarUtils;

import java.lang.reflect.Method;

public class StudentMainActivity extends FragmentActivity {

    ActionBar mActionBar;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    MenuItem mMenuItemSubject;
    MenuItem mMenuItemHome;
    MenuItem mMenuItemAccount;
    MenuItem mMenuItemMore;

    Fragment mFragmentSubject;
    Fragment mFragmentHome;
    Fragment mFragmentAccount;

    ExitPopWindow mExitPopWindow;


    void initView() {
        mActionBar = getActionBar();
        SmartBarUtils.setActionBarTabsShowAtBottom(mActionBar, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
        if (hasSmartBar()) {
            getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
        } else {
            getWindow().setUiOptions(0);
        }
        setContentView(R.layout.activity_student_main);
        initView();
        setCurrentFragment(R.id.menuSubject);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_main, menu);
        mMenuItemSubject = menu.findItem(R.id.menuSubject);
        mMenuItemHome = menu.findItem(R.id.menuHome);
        mMenuItemAccount = menu.findItem(R.id.menuAccount);
        mMenuItemMore = menu.findItem(R.id.menuMore);

        mMenuItemSubject.setIcon(getResources().getDrawable(R.drawable.icon_subject_checked));
        mMenuItemHome.setIcon(getResources().getDrawable(R.drawable.icon_home_normal));
        mMenuItemAccount.setIcon(getResources().getDrawable(R.drawable.icon_account_normal));
        mMenuItemMore.setIcon(getResources().getDrawable(R.drawable.mz_ic_sb_more));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()!=R.id.menuMore) {
            mMenuItemSubject.setIcon(getResources().getDrawable(R.drawable.icon_subject_normal));
            mMenuItemHome.setIcon(getResources().getDrawable(R.drawable.icon_home_normal));
            mMenuItemAccount.setIcon(getResources().getDrawable(R.drawable.icon_account_normal));
        }
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.menuSubject:
                mMenuItemSubject.setIcon(getResources().getDrawable(R.drawable.icon_subject_checked));
                setCurrentFragment(id);
                break;
            case R.id.menuHome:
                mMenuItemHome.setIcon(getResources().getDrawable(R.drawable.icon_home_checked));
                setCurrentFragment(id);
                break;
            case R.id.menuAccount:
                mMenuItemAccount.setIcon(getResources().getDrawable(R.drawable.icon_account_checked));
                setCurrentFragment(id);
                break;
            case R.id.menuMore:
                mMenuItemMore.setIcon(getResources().getDrawable(R.drawable.mz_ic_sb_more));
                mExitPopWindow=new ExitPopWindow(StudentMainActivity.this,mOnClickListener);
                mExitPopWindow.showAtLocation(StudentMainActivity.this.findViewById(R.id.fragment), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,10,10);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener mOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.change: {
                    SharedPreferences sharedPreferences=getSharedPreferences("currentUserInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putInt("type",2);
                    editor.commit();
                    Intent intent=new Intent(StudentMainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    StudentMainActivity.this.finish();
                }break;
                case R.id.exit:{
                    StudentMainActivity.this.finish();
                }break;
                case R.id.cancel:{
                    mExitPopWindow.dismiss();
                }
            }
        }
    };

    private void setCurrentFragment(int itemId) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment, findFragment(itemId));
        mFragmentTransaction.commit();
    }

    private Fragment findFragment(int itemId) {

        Fragment fragment;
        switch (itemId) {
            case R.id.menuSubject:
                if (mMenuItemSubject == null) {
                    Log.e("","new Subject");
                    mFragmentSubject = new StudentSubjectFragment();
                }
                fragment = mFragmentSubject;
                break;
            case R.id.menuHome:
                if (mFragmentHome == null) {
                    mFragmentHome = new HomeFragment();
                }
                fragment = mFragmentHome;
                break;
            case R.id.menuAccount:
                if (mFragmentAccount == null) {
                    mFragmentAccount = new StudentAccountFragment();
                }
                fragment = mFragmentAccount;
                break;
            default:
                fragment = null;
                break;
        }
        return fragment;
    }

    private boolean hasSmartBar() {
        try {
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
        }
        return false;
    }
}
