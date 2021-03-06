package com.mooc.ppjoke.ui.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.LayoutCommentDialogBinding;
import com.mooc.ppjoke.model.Comment;
import com.mooc.ppjoke.ui.login.UserManager;
import com.mooc.ppjoke.ui.publish.CaptureActivity;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.arch.core.executor.ArchTaskExecutor;
import us.bojie.libcommon.ViewHelper;
import us.bojie.libcommon.dialog.LoadingDialog;
import us.bojie.libcommon.utils.FileUploadManager;
import us.bojie.libcommon.utils.FileUtils;
import us.bojie.libcommon.utils.PixUtils;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;
import us.bojie.libnetwork.JsonCallback;

import static us.bojie.libcommon.utils.ToastUtils.showToast;

public class CommentDialog extends AppCompatDialogFragment implements View.OnClickListener {
    private LayoutCommentDialogBinding mBinding;
    private long itemId;
    private CommentAddListener mListener;
    public static final String KEY_ITEM_ID = "key_item_id";
    private String filePath;
    private int width;
    private int height;
    private boolean isVideo;
    private LoadingDialog loadingDialog;
    private String fileUrl;
    private String coverUrl;

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
        Window window = getDialog().getWindow();
        window.setWindowAnimations(0);

        mBinding = LayoutCommentDialogBinding.inflate(inflater, window.findViewById(android.R.id.content), false);
        mBinding.commentVideo.setOnClickListener(this);
        mBinding.commentDelete.setOnClickListener(this);
        mBinding.commentSend.setOnClickListener(this);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        itemId = getArguments().getLong(KEY_ITEM_ID);

        ViewHelper.setViewOutline(mBinding.getRoot(), PixUtils.dp2px(10), ViewHelper.RADIUS_TOP);

        mBinding.getRoot().post(() -> showSoftInputMethod());

        return mBinding.getRoot();
    }

    private void showSoftInputMethod() {
        mBinding.inputView.setFocusable(true);
        mBinding.inputView.setFocusableInTouchMode(true);
        mBinding.inputView.requestFocus();
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(mBinding.inputView, 0);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.comment_send) {
            publishComment();

        } else if (v.getId() == R.id.comment_video) {
            CaptureActivity.startActivityForResult(getActivity());
        } else if (v.getId() == R.id.comment_delete) {
            filePath = null;
            isVideo = false;
            width = 0;
            height = 0;
            mBinding.commentCover.setImageDrawable(null);
            mBinding.commentExtLayout.setVisibility(View.GONE);

            mBinding.commentVideo.setEnabled(true);
            mBinding.commentVideo.setImageAlpha(255);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CAPTURE && resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);

            if (!TextUtils.isEmpty(filePath)) {
                mBinding.commentExtLayout.setVisibility(View.VISIBLE);
                mBinding.commentCover.setImageUrl(filePath);
                if (isVideo) {
                    mBinding.commentIconVideo.setVisibility(View.VISIBLE);
                }
            }

            mBinding.commentVideo.setEnabled(false);
            mBinding.commentVideo.setImageAlpha(80);
        }
    }

    private void publishComment() {
        if (TextUtils.isEmpty(mBinding.inputView.getText())) {
            return;
        }

        if (isVideo && !TextUtils.isEmpty(filePath)) {
            FileUtils.generateVideoCover(filePath).observe(this, coverPath ->
                    uploadFile(coverPath, filePath));
        } else if (!TextUtils.isEmpty(filePath)) {
            uploadFile(null, filePath);
        } else {
            publish();
        }
    }

    private void uploadFile(String coverPath, @NonNull String filePath) {
        showLoadingDialog();
        AtomicInteger count = new AtomicInteger(1);
        if (!TextUtils.isEmpty(coverPath)) {
            count.set(2);
            ArchTaskExecutor.getIOThreadExecutor().execute(() -> {
                int remain = count.decrementAndGet();
                coverUrl = FileUploadManager.upload(coverPath);
                if (remain <= 0) {
                    if (!TextUtils.isEmpty(fileUrl) && !TextUtils.isEmpty(coverUrl)) {
                        publish();
                    } else {
                        dismissLoadingDialog();
                        showToast(getString(R.string.file_upload_failed));
                    }
                }
            });
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(() -> {
            int remain = count.decrementAndGet();
            fileUrl = FileUploadManager.upload(filePath);
            if (remain <= 0) {
                if (!TextUtils.isEmpty(fileUrl) || !TextUtils.isEmpty(coverPath) && !TextUtils.isEmpty(coverUrl)) {
                    publish();
                } else {
                    dismissLoadingDialog();
                    showToast(getString(R.string.file_upload_failed));
                }
            }
        });
    }

    private void publish() {

        String commentText = mBinding.inputView.getText().toString();
        ApiService.post("/comment/addComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", commentText)
                .addParam("image_url", isVideo ? coverUrl : fileUrl)
                .addParam("video_url", isVideo ? fileUrl : null)
                .addParam("width", width)
                .addParam("height", height)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommentSuccess(response.body);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(ApiResponse response) {
                        showToast(getString(R.string.comment_failed));
                        dismissLoadingDialog();
                    }
                });
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
            loadingDialog.setLoadingText(getString(R.string.uploading));
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(false);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            //dismissLoadingDialog  的调用可能会出现在异步线程调用
            if (Looper.myLooper() == Looper.getMainLooper()) {
                ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                });
            } else if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    private void onCommentSuccess(Comment body) {
        showToast("Comment succeed");
        ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
            if (mListener != null) {
                mListener.onAddComment(body);
            }
            dismiss();
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissLoadingDialog();
        filePath = null;
        fileUrl = null;
        coverUrl = null;
        isVideo = false;
        width = 0;
        height = 0;
    }

    public interface CommentAddListener {
        void onAddComment(Comment comment);
    }

    public void setCommentAddListener(CommentAddListener listener) {
        mListener = listener;
    }
}
