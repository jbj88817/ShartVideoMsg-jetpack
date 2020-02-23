package com.mooc.ppjoke.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.ActivityLayoutProfileBinding;
import com.mooc.ppjoke.model.User;
import com.mooc.ppjoke.ui.login.UserManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAB_TYPE_ALL = "tab_all";
    public static final String TAB_TYPE_FEED = "tab_feed";
    public static final String TAB_TYPE_COMMENT = "tab_comment";
    public static final String KEY_TAB_TYPE = "key_tab_type";

    private ActivityLayoutProfileBinding mBinding;


    public static void startProfileActivity(Context context, String tabType) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TAB_TYPE, tabType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_profile);
        User user = UserManager.get().getUser();
        mBinding.setUser(user);
        mBinding.actionBack.setOnClickListener(v -> finish());

        String[] tabs = getResources().getStringArray(R.array.profile_tabs);
        ViewPager2 viewPager = mBinding.viewPager;
        TabLayout tabLayout = mBinding.tabLayout;
        new TabLayoutMediator(tabLayout, viewPager, false, (tab, position) -> {
            tab.setText(tabs[position]);
        }).attach();

        int initTabPosition = getInitTabPosition();

        if (initTabPosition != 0) {
            viewPager.post(() -> viewPager.setCurrentItem(initTabPosition));
        }

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return ProfileListFragment.newInstance(getTabTypeByPosition(position));
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });

        mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            boolean expend = Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange();
            mBinding.setExpend(expend);
        });
    }

    private String getTabTypeByPosition(int position) {
        switch (position) {
            case 0:
                return TAB_TYPE_ALL;
            case 1:
                return TAB_TYPE_FEED;
            case 2:
                return TAB_TYPE_COMMENT;
        }

        return TAB_TYPE_ALL;
    }

    private int getInitTabPosition() {
        String initTab = getIntent().getStringExtra(KEY_TAB_TYPE);

        switch (initTab) {
            case TAB_TYPE_FEED:
                return 1;
            case TAB_TYPE_COMMENT:
                return 2;
            case TAB_TYPE_ALL:
            default:
                return 0;
        }
    }
}
