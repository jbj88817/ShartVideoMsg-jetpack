package com.mooc.ppjoke.ui.publish;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import us.bojie.libcommon.utils.FileUploadManager;

public class UploadfileWorker extends Worker {

    public UploadfileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String filePath = inputData.getString("file");
        String fileUrl = FileUploadManager.upload(filePath);
        if (TextUtils.isEmpty(fileUrl)) {
            return Result.failure();
        } else {
            Data outputData = new Data.Builder().putString("fileUrl", fileUrl).build();
            return Result.success(outputData);
        }
    }
}
