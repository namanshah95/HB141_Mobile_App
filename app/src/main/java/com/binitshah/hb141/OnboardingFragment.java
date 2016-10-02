package com.binitshah.hb141;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnboardingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OnboardingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnboardingFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnboardingFragment.
     */

    public static OnboardingFragment newInstance(int position) {
        OnboardingFragment fragment = new OnboardingFragment();
        return fragment;
    }

    public OnboardingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_onboarding, container, false);
        return rootView;
    }

}
