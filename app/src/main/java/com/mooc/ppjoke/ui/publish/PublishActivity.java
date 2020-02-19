package com.mooc.ppjoke.ui.publish;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.ActivityLayoutPublishBinding;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.model.TagList;
import com.mooc.ppjoke.ui.login.UserManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.databinding.DataBindingUtil;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import us.bojie.libcommon.dialog.LoadingDialog;
import us.bojie.libcommon.utils.FileUtils;
import us.bojie.libnavannotation.ActivityDestination;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;
import us.bojie.libnetwork.JsonCallback;

import static us.bojie.libcommon.utils.ToastUtils.showToast;

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLayoutPublishBinding mBinding;
    private int width;
    private int height;
    private String filePath;
    private boolean isVideo;
    private String mCoverPath;
    private UUID coverUUID;
    private UUID fileUploadUUID;
    private String coverUploadUrl;
    private String fileUploadUrl;
    private TagList mTagList;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_publish);
        mBinding.actionClose.setOnClickListener(this);
        mBinding.actionPublish.setOnClickListener(this);
        mBinding.actionDeleteFile.setOnClickListener(this);
        mBinding.actionAddTag.setOnClickListener(this);
        mBinding.actionAddFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.action_close:
                showExitDialog();
                break;
            case R.id.action_publish:
                publish();
                break;
            case R.id.action_add_tag:
                TagBottomSheetDialogFragment fragment = new TagBottomSheetDialogFragment();
                fragment.setOnTagItemSelectedListener(tagList -> {
                    mTagList = tagList;
                    mBinding.actionAddTag.setText(tagList.title);
                });
                fragment.show(getSupportFragmentManager(), "tag_dialog");
                break;
            case R.id.action_add_file:
                CaptureActivity.startActivityForResult(this);
                break;
            case R.id.action_delete_file:
                mBinding.actionAddFile.setVisibility(View.VISIBLE);
                mBinding.fileContainer.setVisibility(View.GONE);
                mBinding.cover.setImageDrawable(null);
                filePath = null;
                width = 0;
                height = 0;
                isVideo = false;
                break;
        }
    }

    private void publish() {
        showLoading();
        List<OneTimeWorkRequest> workRequests = new ArrayList<>();
        if (!TextUtils.isEmpty(filePath)) {
            if (isVideo) {
                FileUtils.generateVideoCover(filePath).observe(this, coverPath -> {
                    mCoverPath = coverPath;
                    OneTimeWorkRequest request = getOneTimeWorkRequest(coverPath);
                    workRequests.add(request);

                    enqueue(workRequests);
                });
            }

            OneTimeWorkRequest request = getOneTimeWorkRequest(filePath);
            fileUploadUUID = request.getId();
            workRequests.add(request);
            if (!isVideo) {
                enqueue(workRequests);
            }
        } else {
            publishFeed();
        }
    }

    private void enqueue(List<OneTimeWorkRequest> workRequests) {
        WorkContinuation workContinuation = WorkManager.getInstance(PublishActivity.this).beginWith(workRequests);
        workContinuation.enqueue();

        workContinuation.getWorkInfosLiveData().observe(PublishActivity.this, workInfos -> {
            int completedCount = 0;
            for (WorkInfo workInfo : workInfos) {
                WorkInfo.State state = workInfo.getState();
                Data outputData = workInfo.getOutputData();
                UUID uuid = workInfo.getId();
                if (state == WorkInfo.State.FAILED) {
                    if (uuid.equals(coverUUID)) {
                        showToast(getString(R.string.file_upload_cover_failed_message));

                    } else if (uuid.equals(fileUploadUUID)) {
                        showToast(getString(R.string.file_upload_failed_message));
                    }
                } else if (state == WorkInfo.State.SUCCEEDED) {
                    String fileUrl = outputData.getString("fileUrl");
                    if (uuid.equals(coverUUID)) {
                        coverUploadUrl = fileUrl;
                    } else if (uuid.equals(fileUploadUUID)) {
                        fileUploadUrl = fileUrl;
                    }
                    completedCount++;
                }
            }

            if (completedCount >= workInfos.size()) {
                publishFeed();
            }
        });
    }

    private void publishFeed() {
        ApiService.post("/feeds/publish")
                .addParam("coverUrl", coverUploadUrl)
                .addParam("fileUrl", fileUploadUrl)
                .addParam("fileWidth", width)
                .addParam("fileHeight", height)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("tagId", mTagList == null ? 0 : mTagList.tagId)
                .addParam("tagTitle", mTagList == null ? "" : mTagList.title)
                .addParam("feedText", mBinding.inputView.getText().toString())
                .addParam("feedType", isVideo ? Feed.TYPE_VIDEO : Feed.TYPE_IMAGE)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        showToast(getString(R.string.publish_post_success));
                        PublishActivity.this.finish();
                        dismissLoading();
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                        dismissLoading();
                    }
                });

    }

    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setLoadingText(getString(R.string.posting));
        }
        mLoadingDialog.show();
    }

    private void dismissLoading() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        } else {
            ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            });
        }

    }

    @NotNull
    private OneTimeWorkRequest getOneTimeWorkRequest(String filePath) {
        Data inputData = new Data.Builder()
                .putString("file", filePath)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(UploadfileWorker.class)
                .setInputData(inputData)
                .build();

        coverUUID = request.getId();
        return request;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CAPTURE && resultCode == RESULT_OK && data != null) {
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);

            showFileThumbnail();
        }
    }

    private void showFileThumbnail() {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        mBinding.actionAddFile.setVisibility(View.GONE);
        mBinding.fileContainer.setVisibility(View.VISIBLE);
        mBinding.cover.setImageUrl(filePath);
        mBinding.videoIcon.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        mBinding.cover.setOnClickListener(v -> PreviewActivity
                .startActivityForResult(PublishActivity.this, filePath, isVideo, null));
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.publish_exit_message))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.exit), (dialog, which) -> {
                    dialog.dismiss();
                    PublishActivity.this.finish();
                }).create().show();
    }
}
