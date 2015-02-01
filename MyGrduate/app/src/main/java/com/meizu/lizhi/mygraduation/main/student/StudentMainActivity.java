package com.meizu.lizhi.mygraduation.main.student;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.meizu.lizhi.mygraduation.R;
import com.meizu.lizhi.mygraduation.main.student.account.AccountFragment;
import com.meizu.lizhi.mygraduation.main.student.home.HomeFragment;
import com.meizu.lizhi.mygraduation.main.student.subject.SubjectFragment;
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

    void initView() {
        mActionBar = getActionBar();
        mActionBar.setDisplayOptions(0);
        SmartBarUtils.setActionBarTabsShowAtBottom(mActionBar, true);
        SmartBarUtils.setActionBarViewCollapsable(mActionBar, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mMenuItemSubject.setIcon(getResources().getDrawable(R.drawable.icon_subject_normal));
        mMenuItemHome.setIcon(getResources().getDrawable(R.drawable.icon_home_normal));
        mMenuItemAccount.setIcon(getResources().getDrawable(R.drawable.icon_account_normal));
        mMenuItemMore.setIcon(getResources().getDrawable(R.drawable.mz_ic_sb_more));

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
                //handle more
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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
                    mFragmentSubject = new SubjectFragment();
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
                    mFragmentAccount = new AccountFragment();
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
