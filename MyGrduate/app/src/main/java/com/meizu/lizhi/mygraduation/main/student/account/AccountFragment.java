package com.meizu.lizhi.mygraduation.main.student.account;



import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meizu.lizhi.mygraduation.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AccountFragment extends Fragment {


   ActionBar mActionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_account, container, false);
        mActionBar = getActivity().getActionBar();
        mActionBar.setTitle("个人中心");
        return view;
    }


}
