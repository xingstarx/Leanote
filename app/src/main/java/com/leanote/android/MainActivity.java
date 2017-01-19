package com.leanote.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;

import com.leanote.android.database.AppDataBase;
import com.leanote.android.service.SyncService;
import com.leanote.android.weidget.TabBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private TabBarView mTabBarView;
    private String[] mTabTitles = new String[]{"最新", "全部"};
    private int[] mTabIcons = new int[]{R.drawable.ic_action_last, R.drawable.ic_action_last};
    private MainAdapter mMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AppDataBase.getAccountWithToken() == null) {
            LoginActivity.startLogin(this);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        SyncService.startSyncAll(this);
    }

    private void init() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        mMainAdapter = new MainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainAdapter);
        mTabBarView = new TabBarView(getSupportActionBar().getThemedContext());
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT, GravityCompat.START);
        getSupportActionBar().setCustomView(mTabBarView, params);

        mTabBarView.setViewPager(mViewPager);
        mTabBarView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }
        });
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    class MainAdapter extends FragmentPagerAdapter implements TabBarView.IconTabProvider {
        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return HeadlineFragment.newInstance();
            } else {
                return BrowserFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getPageIconResId(int position) {
            return mTabIcons[position];
        }
    }
}
