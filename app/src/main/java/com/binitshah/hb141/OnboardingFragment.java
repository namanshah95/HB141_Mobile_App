package com.binitshah.hb141;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class OnboardingFragment extends Fragment {

    public TextView onboardingPageTitle;
    public TextView onboardingPageSubTitle;
    public TextView onboardingPageMainContent;

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

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Changa 800.ttf");
        Typeface fontThin = Typeface.createFromAsset(getActivity().getAssets(), "Changa 200.ttf");

        switch (position) {
            case 0:
                RelativeLayout relativeLayout1 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page1);
                relativeLayout1.setVisibility(View.VISIBLE);
                onboardingPageTitle = (TextView) rootView.findViewById(R.id.onboarding_page1Title);
                onboardingPageSubTitle = (TextView) rootView.findViewById(R.id.partneredWithTitle);
                onboardingPageTitle.setTypeface(font);
                onboardingPageSubTitle.setTypeface(font);
                break;
            case 1:
                RelativeLayout relativeLayout2 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page2);
                relativeLayout2.setVisibility(View.VISIBLE);
                onboardingPageTitle = (TextView) rootView.findViewById(R.id.onboarding_page2Title);
                onboardingPageMainContent = (TextView) rootView.findViewById(R.id.onboarding_page2Content);
                onboardingPageTitle.setTypeface(font);
                onboardingPageMainContent.setTypeface(font);
                break;
            case 2:
                RelativeLayout relativeLayout3 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page3);
                relativeLayout3.setVisibility(View.VISIBLE);
                onboardingPageSubTitle = (TextView) rootView.findViewById(R.id.onboarding_page3Subtitle);
                onboardingPageTitle = (TextView) rootView.findViewById(R.id.onboarding_page3Title);
                TextView statnum1 = (TextView) rootView.findViewById(R.id.onboarding_page3StatNum1);
                TextView statnum2 = (TextView) rootView.findViewById(R.id.onboarding_page3StatNum2);
                TextView statnum3 = (TextView) rootView.findViewById(R.id.onboarding_page3StatNum3);
                TextView statnum4 = (TextView) rootView.findViewById(R.id.onboarding_page3StatNum4);
                TextView stat1 = (TextView) rootView.findViewById(R.id.onboarding_page3Stat1);
                TextView stat2 = (TextView) rootView.findViewById(R.id.onboarding_page3Stat2);
                TextView stat3 = (TextView) rootView.findViewById(R.id.onboarding_page3Stat3);
                TextView stat4 = (TextView) rootView.findViewById(R.id.onboarding_page3Stat4);
                onboardingPageSubTitle.setTypeface(font);
                onboardingPageTitle.setTypeface(font);
                statnum1.setTypeface(fontThin);
                statnum2.setTypeface(fontThin);
                statnum3.setTypeface(fontThin);
                statnum4.setTypeface(fontThin);
                stat1.setTypeface(font);
                stat2.setTypeface(font);
                stat3.setTypeface(font);
                stat4.setTypeface(font);
                break;
            case 3:
                RelativeLayout relativeLayout4 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page4);
                relativeLayout4.setVisibility(View.VISIBLE);
                onboardingPageTitle = (TextView) rootView.findViewById(R.id.onboarding_page4Title);
                onboardingPageMainContent = (TextView) rootView.findViewById(R.id.onboarding_page4Content);
                onboardingPageMainContent.setTypeface(font);
                onboardingPageTitle.setTypeface(font);
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

    public int getTitleHeight() {
        if (position == 0 || position == 1 || position == 2 || position == 3) {
            return onboardingPageTitle.getHeight() + dpToPx(16);
        } else {
            return position*100  + dpToPx(16);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}