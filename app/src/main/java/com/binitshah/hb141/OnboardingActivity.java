package com.binitshah.hb141;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static java.security.AccessController.getContext;


/*
* OnboardingActivity provides the onboarding experience.
 */
public class OnboardingActivity extends FragmentActivity {
    private ViewPager mPager;
    private OnboardingPagerAdapter mPagerAdapter;

    //variables to assist animation
    private int previousPage;
    private float modPosition;

    //Variables to change with the background color
    private RelativeLayout relativeLayout;
    private int prevColor;
    private int thisColor;
    private int nextColor;

    //variables to change the view height
    private View onboardingPageTitleBackground;
    private int prevHeight;
    private int thisHeight;
    private int nextHeight;

    //Nav Control
    private Button prevButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mPagerAdapter);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_onboarding);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        //initializes color
        prevColor = ContextCompat.getColor(this ,R.color.colorTransparent);
        thisColor = ContextCompat.getColor(this ,R.color.colorOnboarding1);
        nextColor = ContextCompat.getColor(this ,R.color.colorOnboarding2);
        relativeLayout.setBackgroundColor(thisColor);

        //initializes height
        onboardingPageTitleBackground = (View) findViewById(R.id.titleBackground);
        mPager.post(new Runnable() {
            @Override
            public void run() {
                OnboardingFragment mOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem());
                OnboardingFragment mNextOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem()+1);
                prevHeight = 0;
                thisHeight = mOF.getTitleHeight();
                nextHeight = mNextOF.getTitleHeight();
                onboardingPageTitleBackground.getLayoutParams().height = thisHeight;
                onboardingPageTitleBackground.setLayoutParams(onboardingPageTitleBackground.getLayoutParams());
            }
        });

        prevButton = (Button) findViewById(R.id.backwards_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPager.getCurrentItem() != 0) {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
                }
                else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });

        nextButton = (Button) findViewById(R.id.forward_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPager.getCurrentItem() != 4){
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                }
                else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
                        int heighted = calculateHeight(thisHeight, prevHeight, modPosition);
                        onboardingPageTitleBackground.getLayoutParams().height = heighted;
                        onboardingPageTitleBackground.setLayoutParams(onboardingPageTitleBackground.getLayoutParams());
                    }
                    if(modPosition == 1){
                        changeColors(position);
                        changeHeights();
                    }
                } else {
                    if(positionOffset <= 0.985) {
                        int blended = blendColors(thisColor, nextColor, positionOffset);
                        relativeLayout.setBackgroundColor(blended);
                        int heighted = calculateHeight(thisHeight, nextHeight, positionOffset);
                        onboardingPageTitleBackground.getLayoutParams().height = heighted;
                        onboardingPageTitleBackground.setLayoutParams(onboardingPageTitleBackground.getLayoutParams());
                    }
                    else {
                        changeColors(position+1);
                        changeHeights();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    prevButton.setText("SKIP");
                }
                else {
                    prevButton.setText("BACK");
                }

                if(position == 4) {
                    nextButton.setText("JOIN");
                }
                else {
                    nextButton.setText("NEXT");
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
    public class OnboardingPagerAdapter extends FragmentStatePagerAdapter {
        private final int NUM_PAGES = 5;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        OnboardingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() { return NUM_PAGES; }

        @Override
        public Fragment getItem(int position) {
            return OnboardingFragment.newInstance(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
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

    public void changeHeights(){
        switch (previousPage){
            case 0:
                mPager.post(new Runnable() {
                    @Override
                    public void run() {
                        OnboardingFragment mOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(previousPage);
                        OnboardingFragment mNextOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(previousPage+1);
                        prevHeight = 0;
                        thisHeight = mOF.getTitleHeight();
                        nextHeight = mNextOF.getTitleHeight();
                        onboardingPageTitleBackground.getLayoutParams().height = thisHeight;
                        onboardingPageTitleBackground.setLayoutParams(onboardingPageTitleBackground.getLayoutParams());
                    }
                });
                break;
            case 4:
                mPager.post(new Runnable() {
                    @Override
                    public void run() {
                        OnboardingFragment mPrevOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(previousPage-1);
                        OnboardingFragment mOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(previousPage);
                        prevHeight = mPrevOF.getTitleHeight();
                        thisHeight = mOF.getTitleHeight();
                        nextHeight = 0;
                        onboardingPageTitleBackground.getLayoutParams().height = thisHeight;
                        onboardingPageTitleBackground.setLayoutParams(onboardingPageTitleBackground.getLayoutParams());
                    }
                });
                break;
            default:
                mPager.post(new Runnable() {
                    @Override
                    public void run() {
                        OnboardingFragment mPrevOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(previousPage-1);
                        OnboardingFragment mOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(previousPage);
                        OnboardingFragment mNextOF = (OnboardingFragment) mPagerAdapter.getRegisteredFragment(previousPage+1);
                        prevHeight = mPrevOF.getTitleHeight();
                        thisHeight = mOF.getTitleHeight();
                        nextHeight = mNextOF.getTitleHeight();
                        onboardingPageTitleBackground.getLayoutParams().height = thisHeight;
                        onboardingPageTitleBackground.setLayoutParams(onboardingPageTitleBackground.getLayoutParams());
                    }
                });
                break;
        }
    }

    private int calculateHeight(int from, int to, float ratio) {
        final float modifiedHeight = from + ((to - from)*ratio);
        return (int)modifiedHeight;
    }
}
