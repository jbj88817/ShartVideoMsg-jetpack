package com.mooc.ppjoke.ui.find;

import com.mooc.ppjoke.model.SofaTab;
import com.mooc.ppjoke.ui.sofa.SofaFragment;
import com.mooc.ppjoke.utils.AppConfig;

import androidx.fragment.app.Fragment;
import us.bojie.libnavannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/find")
public class FindFragment extends SofaFragment {
    private static final String TAG = "FindFragment";

    @Override
    protected Fragment getTabFragment(int position) {
        return TagListFragment.newInstance(getTabConfig().tabs.get(position).tag);
    }

    @Override
    protected SofaTab getTabConfig() {
        return AppConfig.getsFindTab();
    }
}