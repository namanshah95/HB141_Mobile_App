package com.binitshah.hb141;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import me.relex.circleindicator.CircleIndicator;

/*
* OnboardingActivity provides the onboarding experience.
 */
public class OnboardingActivity extends FragmentActivity {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    //Variables to mess with the background color
    private RelativeLayout relativeLayout;
    private int prevColor;
    private int thisColor;
    private int nextColor;
    private int previousPage;
    private float modPosition;

    //Nav Control
    private Button prevButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_onboarding);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        prevColor = ContextCompat.getColor(this ,R.color.colorTransparent);
        thisColor = ContextCompat.getColor(this ,R.color.colorOnboarding1);
        nextColor = ContextCompat.getColor(this ,R.color.colorOnboarding2);
        relativeLayout.setBackgroundColor(thisColor);

        prevButton = (Button) findViewById(R.id.backwards_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPager.getCurrentItem() != 0) {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
                }
            }
        });
        prevButton.setVisibility(View.GONE);

        nextButton = (Button) findViewById(R.id.forward_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPager.getCurrentItem() != 6){
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                }
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (previousPage > position) {
                    modPosition = (1 - positionOffset);
                    if(modPosition >= 0.007){
                        int blended = blendColors(thisColor, prevColor, modPosition);
                        relativeLayout.setBackgroundColor(blended);
                    }
                    if(modPosition == 1){
                        changeColors(position);
                    }
                } else {
                    if(positionOffset <= 0.985) {
                        int blended = blendColors(thisColor, nextColor, positionOffset);
                        relativeLayout.setBackgroundColor(blended);
                    }
                    else {
                        changeColors(position+1);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(position != 0){
                    prevButton.setVisibility(View.VISIBLE);
                }
                else {
                    prevButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /**
     * A simple pager adapter that represents 4 OnboardingFragment objects, in
     * sequence.
     */
    private class OnboardingPagerAdapter extends FragmentStatePagerAdapter {
        private final int NUM_PAGES = 7;

        public OnboardingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() { return NUM_PAGES; }

        @Override
        public Fragment getItem(int position) {
            if(position <= 6){
                return OnboardingFragment.newInstance(position);
            }
            else{
                return OnboardingFragment.newInstance(1);
            }
        }
    }

    public void changeColors(int position){
        previousPage = position;
        switch (position){
            case 0:
                prevColor = ContextCompat.getColor(getApplicationContext(), R.color.colorTransparent);
                thisColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding1);
                relativeLayout.setBackgroundColor(thisColor);
                nextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding2);
                break;
            case 1:
                prevColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding1);
                thisColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding2);
                relativeLayout.setBackgroundColor(thisColor);
                nextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding3);
                break;
            case 2:
                prevColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding2);
                thisColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding3);
                relativeLayout.setBackgroundColor(thisColor);
                nextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding4);
                break;
            case 3:
                prevColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding3);
                thisColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding4);
                relativeLayout.setBackgroundColor(thisColor);
                nextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding5);
                break;
            case 4:
                prevColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding4);
                thisColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding5);
                relativeLayout.setBackgroundColor(thisColor);
                nextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding6);
                break;
            case 5:
                prevColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding5);
                thisColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding6);
                relativeLayout.setBackgroundColor(thisColor);
                nextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding7);
                break;
            case 6:
                prevColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding6);
                thisColor = ContextCompat.getColor(getApplicationContext(), R.color.colorOnboarding7);
                relativeLayout.setBackgroundColor(thisColor);
                nextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorTransparent);
                break;
            default:
                prevColor = ContextCompat.getColor(getApplicationContext(), R.color.colorTransparent);
                thisColor = ContextCompat.getColor(getApplicationContext(), R.color.colorTransparent);
                relativeLayout.setBackgroundColor(thisColor);
                nextColor = ContextCompat.getColor(getApplicationContext(), R.color.colorTransparent);
                break;
        }
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }
}
