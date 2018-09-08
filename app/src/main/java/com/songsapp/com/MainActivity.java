package com.songsapp.com;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.songsapp.com.common.adapters.FragmentAdapter;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    // Member variables.
    // Member variables.
    private Context mContext;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        // Get views.
        getViews();
    }

    private void getViews() {

        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.materialTabHost);

        if (mViewPager != null) {
            adapter = new FragmentAdapter(getSupportFragmentManager());
            adapter.addFragment(new MusicFragment(), mContext.getResources().getString(R.string.music_title));
            adapter.addFragment(new VideoFragment(), mContext.getResources().getString(R.string.video_title));
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(adapter.getCount() + 1);
            mTabLayout.setupWithViewPager(mViewPager);
        }
        // insert all tabs from pagerAdapter data
        mTabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // when the tab is clicked the pager swipe content to the tab position
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
