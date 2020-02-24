package com.mooc.ppjoke.ui.me;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.FragmentMeBinding;
import com.mooc.ppjoke.model.User;
import com.mooc.ppjoke.ui.login.UserManager;
import com.mooc.ppjoke.utils.StatusBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import us.bojie.libnavannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/my", needLogin = true)
public class MyFragment extends Fragment {
    private FragmentMeBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = UserManager.get().getUser();
        mBinding.setUser(user);

        UserManager.get().refresh().observe(this, user1 -> mBinding.setUser(user1));

        mBinding.actionLogout.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.logout))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    UserManager.get().logout();
                    getActivity().onBackPressed();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss()).create().show());

        mBinding.goDetail.setOnClickListener(v -> ProfileActivity.startProfileActivity(getContext(), ProfileActivity.TAB_TYPE_ALL));
        mBinding.userFeed.setOnClickListener(v -> ProfileActivity.startProfileActivity(getContext(), ProfileActivity.TAB_TYPE_FEED));
        mBinding.userComment.setOnClickListener(v -> ProfileActivity.startProfileActivity(getContext(), ProfileActivity.TAB_TYPE_COMMENT));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.lightStatusBar(getActivity(), false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        StatusBar.lightStatusBar(getActivity(), hidden);
    }
}