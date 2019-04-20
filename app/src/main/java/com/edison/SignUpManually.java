package com.edison;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.edison.MainFragment.RegisWithEmail_Fragment;
import com.edison.MainFragment.RegisWithOTPFragment;

import java.util.ArrayList;
import java.util.List;

public class SignUpManually extends AppCompatActivity {

    TabLayout tabview;
    ViewPager viewpager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_manually);

        INIT();

    }

    private void INIT() {

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tabview = (TabLayout) findViewById(R.id.tabview);
        tabview.setupWithViewPager(viewpager);

        setupViewPager(viewpager);

    }

    private void setupViewPager(ViewPager viewpager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RegisWithOTPFragment(), "Phone");
        adapter.addFragment(new RegisWithEmail_Fragment(), "Email");
        viewpager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
