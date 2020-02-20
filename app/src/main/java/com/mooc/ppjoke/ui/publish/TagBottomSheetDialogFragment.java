package com.mooc.ppjoke.ui.publish;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.LayoutTagBottomSheetDialogBinding;
import com.mooc.ppjoke.model.TagList;
import com.mooc.ppjoke.ui.login.UserManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import us.bojie.libcommon.utils.PixUtils;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;
import us.bojie.libnetwork.JsonCallback;

import static us.bojie.libcommon.utils.ToastUtils.showToast;

public class TagBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private LayoutTagBottomSheetDialogBinding mBinding;
    private RecyclerView recyclerView;
    private TagsAdapter adapter;
    private List<TagList> mTagLists = new ArrayList<>();
    private OnTagItemSelectedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_tag_bottom_sheet_dialog, null, false);
        recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TagsAdapter();
        recyclerView.setAdapter(adapter);

        View root = mBinding.getRoot();
        dialog.setContentView(root);
        ViewGroup parent = (ViewGroup) root.getParent();
        BottomSheetBehavior<ViewGroup> behavior = BottomSheetBehavior.from(parent);
        behavior.setPeekHeight(PixUtils.getScreenHeight() / 3);
        behavior.setHideable(false);

        ViewGroup.LayoutParams params = parent.getLayoutParams();
        params.height = PixUtils.getScreenHeight() / 3 * 2;
        parent.setLayoutParams(params);

        queryTagList();
        return dialog;
    }

    private void queryTagList() {
        ApiService.get("/tag/queryTagList")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("pageCount", 100)
                .addParam("tagId", 0)
                .execute(new JsonCallback<List<TagList>>() {
                    @Override
                    public void onSuccess(ApiResponse<List<TagList>> response) {

                        if (response.body != null) {
                            List<TagList> body = response.body;
                            mTagLists.clear();
                            mTagLists.addAll(body);
                            ArchTaskExecutor.getMainThreadExecutor().execute(() -> adapter.notifyDataSetChanged());
                        }
                    }

                    @Override
                    public void onError(ApiResponse<List<TagList>> response) {
                        showToast(response.message);
                    }
                });
    }

    class TagsAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setTextSize(13);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.color_000));
            textView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT, PixUtils.dp2px(45)));
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;
            TagList tagList = mTagLists.get(position);
            textView.setText(tagList.title);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTagItemSelected(tagList);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTagLists.size();
        }
    }

    public void setOnTagItemSelectedListener(OnTagItemSelectedListener listener) {
        this.listener = listener;
    }

    interface OnTagItemSelectedListener {
        void onTagItemSelected(TagList tagList);
    }
}
