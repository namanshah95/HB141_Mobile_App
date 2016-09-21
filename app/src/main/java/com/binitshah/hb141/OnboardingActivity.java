package com.binitshah.hb141;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
* OnboardingActivity provides the onboarding experience.
 */
public class OnboardingActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    /**
     * A simple pager adapter that represents 4 OnboardingFragment objects, in
     * sequence.
     */
    private class OnboardingPagerAdapter extends FragmentStatePagerAdapter {
        public OnboardingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OnboardingFragment.newInstance("Onboarding Screen 1", Color.parseColor("#E74C3C"));
                case 1:
                    return OnboardingFragment.newInstance("Onboarding Screen 2", Color.parseColor("#F1C40F"));
                case 2:
                    return OnboardingFragment.newInstance("Onboarding Screen 3", Color.parseColor("#1ABC9C"));
                case 3:
                    return OnboardingFragment.newInstance("Onboarding Screen 4", Color.parseColor("#2ECC71"));
                default:
                    return OnboardingFragment.newInstance("Incorrect Position", Color.parseColor("#FFFFFF"));
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
