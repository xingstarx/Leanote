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
import android.view.Menu;
import android.view.MenuItem;

import com.leanote.android.base.BaseFragment;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.rxbus.MenuRefreshEvent;
import com.leanote.android.rxbus.RxBus;
import com.leanote.android.rxbus.SyncEvent;
import com.leanote.android.service.SyncService;
import com.leanote.android.weidget.TabBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private TabBarView mTabBarView;
    private String[] mTabTitles = new String[]{"全部", "最新"};
    private int[] mTabIcons = new int[]{R.drawable.ic_menu_all_note, R.drawable.ic_doc_selected};
    private MainAdapter mMainAdapter;
    private BrowserFragment browserFragment;
    private HeadlineFragment headlineFragment;
    private int mCurrentItem;
    private boolean isShowMenu = true;

    public static void show(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (isShowMenu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                BaseFragment fragment = (BaseFragment) mMainAdapter.getItem(mCurrentItem);
                if (fragment instanceof BrowserFragment) {
                    ((BrowserFragment) fragment).onRefresh();
                } else if (fragment instanceof HeadlineFragment) {
                    ((HeadlineFragment) fragment).onRefresh();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppDataBase.getAccountWithToken() == null) {
            LoginActivity.startLogin(this);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        RxBus.getInstance().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof MenuRefreshEvent) {
                    final MenuRefreshEvent refreshEvent = (MenuRefreshEvent) event;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (refreshEvent.type == MenuRefreshEvent.TYPE_HIDE_MENU) {
                                isShowMenu = false;
                            } else {
                                isShowMenu = true;
                            }
                            invalidateOptionsMenu();
                        }
                    });
                }
            }
        });
        SyncService.startSyncNote(this);
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
                mCurrentItem = position;
                if (mCurrentItem == 0 && !browserFragment.isRootFolder()) {
                    isShowMenu = false;
                } else {
                    isShowMenu = true;
                }
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = (BaseFragment) mMainAdapter.getItem(mCurrentItem);
        if (!fragment.onBackPressed()) {
            super.onBackPressed();
        }
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
                if (browserFragment == null) {
                    browserFragment = BrowserFragment.newInstance();
                }
                return browserFragment;
            } else {
                if (headlineFragment == null) {
                    headlineFragment = HeadlineFragment.newInstance();
                }
                return headlineFragment;
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
