package com.mooc.ppjoke.ui.find;

import android.text.TextUtils;

import com.mooc.ppjoke.model.SofaTab;
import com.mooc.ppjoke.ui.sofa.SofaFragment;
import com.mooc.ppjoke.utils.AppConfig;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import us.bojie.libnavannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/find")
public class FindFragment extends SofaFragment {

    @Override
    protected Fragment getTabFragment(int position) {
        return TagListFragment.newInstance(getTabConfig().tabs.get(position).tag);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        String tagType = childFragment.getArguments().getString(TagListFragment.KEY_TAG_TYPE);
        if (TextUtils.equals(tagType, "onlyFollow")) {
            ViewModelProviders.of(childFragment).get(TagListViewModel.class)
                    .getSwitchTabLiveData().observe(this, o -> viewPager.setCurrentItem(1));
        }
    }

    @Override
    protected SofaTab getTabConfig() {
        return AppConfig.getsFindTab();
    }
}