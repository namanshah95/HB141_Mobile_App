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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;



public class OnboardingFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnboardingFragment.
     */

    private int position;

    public static OnboardingFragment newInstance(int positionHolder) {
        OnboardingFragment fragment = new OnboardingFragment();
        fragment.setPosition(positionHolder);
        return fragment;
    }

    public void setPosition(int positionHolder){
        position = positionHolder;
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
        switch (position) {
            case 0:
                RelativeLayout relativeLayout1 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page1);
                relativeLayout1.setVisibility(View.VISIBLE);
                break;
            case 1:
                RelativeLayout relativeLayout2 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page2);
                relativeLayout2.setVisibility(View.VISIBLE);
                break;
            case 2:
                RelativeLayout relativeLayout3 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page3);
                relativeLayout3.setVisibility(View.VISIBLE);
                break;
            case 3:
                RelativeLayout relativeLayout4 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page4);
                relativeLayout4.setVisibility(View.VISIBLE);
                break;
            case 4:
                RelativeLayout relativeLayout5 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page5);
                relativeLayout5.setVisibility(View.VISIBLE);
                break;
            case 5:
                RelativeLayout relativeLayout6 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page6);
                relativeLayout6.setVisibility(View.VISIBLE);
                break;
        }
        return rootView;
    }

}
