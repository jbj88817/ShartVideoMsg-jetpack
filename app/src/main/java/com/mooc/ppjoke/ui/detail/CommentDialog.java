package com.mooc.ppjoke.ui.detail;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.LayoutCommentDialogBinding;
import com.mooc.ppjoke.model.Comment;
import com.mooc.ppjoke.ui.login.UserManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;
import us.bojie.libnetwork.JsonCallback;

import static us.bojie.libcommon.utils.ToastUtils.showToast;

public class CommentDialog extends DialogFragment implements View.OnClickListener {
    private LayoutCommentDialogBinding mBinding;
    private long itemId;
    private CommentAddListener mListener;
    public static final String KEY_ITEM_ID = "key_item_id";

    public static CommentDialog newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(KEY_ITEM_ID, itemId);
        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = LayoutCommentDialogBinding.inflate(inflater, container, false);
        mBinding.commentVideo.setOnClickListener(this);
        mBinding.commentDelete.setOnClickListener(this);
        mBinding.commentSend.setOnClickListener(this);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        itemId = getArguments().getLong(KEY_ITEM_ID);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.comment_send) {
            publishComment();

        } else if (v.getId() == R.id.comment_video) {

        } else if (v.getId() == R.id.comment_delete) {

        }
    }

    private void publishComment() {

        String commentText = mBinding.inputView.getText().toString();
        if (TextUtils.isEmpty(commentText)) {
            return;
        }

        ApiService.post("/comment/addComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", commentText)
                .addParam("image_url", null)
                .addParam("video_url", null)
                .addParam("width", 0)
                .addParam("height", 0)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommentSuccess(response.body);
                    }

                    @Override
                    public void onError(ApiResponse response) {
                        showToast(getString(R.string.comment_failed));
                    }
                });

    }

    private void onCommentSuccess(Comment body) {
        showToast("Comment succeed");
        if (mListener != null) {
            mListener.onAddComment(body);
        }
    }

    public interface CommentAddListener {
        void onAddComment(Comment comment);
    }

    public void setCommentAddListener(CommentAddListener listener) {
        mListener = listener;
    }

}
