package com.meizu.lizhi.mygraduation.main.student.account;



import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meizu.lizhi.mygraduation.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class AccountFragment extends Fragment {


   ActionBar mActionBar;

    private View mCustomView;

    private int mActionBarOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_student_account, container, false);
        mActionBar = getActivity().getActionBar();
        mCustomView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.account_custom_title_view, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionBarOptions = mActionBar.getDisplayOptions();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActionBar.setDisplayOptions(mActionBarOptions, ActionBar.DISPLAY_SHOW_CUSTOM | mActionBarOptions);

    }
}
