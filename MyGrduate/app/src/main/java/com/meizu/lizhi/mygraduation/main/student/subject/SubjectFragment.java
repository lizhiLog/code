package com.meizu.lizhi.mygraduation.main.student.subject;



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
public class SubjectFragment extends Fragment {


    public SubjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_subject, container, false);
        return view;
    }


}
