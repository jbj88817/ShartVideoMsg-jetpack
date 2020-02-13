package us.bojie.libcommon.utils;

import android.widget.Toast;

import androidx.arch.core.executor.ArchTaskExecutor;
import us.bojie.libcommon.AppGlobals;

public class ToastUtils {

    public static void showToast(String message) {
        ArchTaskExecutor.getMainThreadExecutor().execute(() -> Toast.makeText(AppGlobals.getApplication(), message, Toast.LENGTH_SHORT).show());
    }
}
