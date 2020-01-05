package us.bojie.shortvideomsg.ui.home;


import androidx.annotation.NonNull;

import androidx.paging.PagedListAdapter;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

import us.bojie.libnavannotation.FragmentDestination;
import us.bojie.shortvideomsg.model.Feed;
import us.bojie.shortvideomsg.ui.AbsListFragment;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {
    private static final String TAG = "HomeFragment";

    @Override
    protected void afterCreateView() {

    }

    @Override
    protected PagedListAdapter getAdapter() {
        String feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(), feedType);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}