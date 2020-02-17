package com.mooc.ppjoke.ui.publish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.ActivityLayoutCaptureBinding;
import com.mooc.ppjoke.view.RecordView;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import static us.bojie.libcommon.utils.ToastUtils.showToast;

public class CaptureActivity extends AppCompatActivity {

    private ActivityLayoutCaptureBinding mBinding;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_CODE = 1000;
    private ArrayList<String> deniedPermission = new ArrayList<>();
    private CameraX.LensFacing mLensFacing = CameraX.LensFacing.BACK;
    private int rotation = Surface.ROTATION_0;
    private Size resolution = new Size(1280, 720);
    private Rational aspectRatio = new Rational(9, 16);
    private Preview preview;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private boolean takingPicture;
    private String outputFilePath;

    public static final String RESULT_FILE_PATH = "file_path";
    public static final String RESULT_FILE_WIDTH = "file_width";
    public static final String RESULT_FILE_HEIGHT = "file_height";
    public static final String RESULT_FILE_TYPE = "file_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_capture);
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        mBinding.recordView.setOnRecordListener(new RecordView.OnRecordListener() {
            @Override
            public void onClick() {
                takingPicture = true;
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + ".jpeg");
                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        onFileSaved(file);

                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        showToast(useCaseError.toString());
                    }
                });
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onLongClick() {
                takingPicture = false;
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + ".mp4");
                videoCapture.startRecording(file, new VideoCapture.OnVideoSavedListener() {
                    @Override
                    public void onVideoSaved(File file) {
                        onFileSaved(file);
                    }

                    @Override
                    public void onError(VideoCapture.UseCaseError useCaseError, String message, @Nullable Throwable cause) {
                        showToast(useCaseError.toString());
                    }
                });

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinish() {
                videoCapture.stopRecording();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PreviewActivity.REQ_PREVIEW && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_FILE_PATH, outputFilePath);
            intent.putExtra(RESULT_FILE_WIDTH, resolution.getHeight());
            intent.putExtra(RESULT_FILE_HEIGHT, resolution.getWidth());
            intent.putExtra(RESULT_FILE_TYPE, !takingPicture);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void onFileSaved(File file) {
        outputFilePath = file.getAbsolutePath();
        String mimeType = takingPicture ? "image/jpeg" : "video/mp4";
        MediaScannerConnection.scanFile(this, new String[]{outputFilePath},
                new String[]{mimeType}, null);
        PreviewActivity.startActivityForResult(this, outputFilePath, !takingPicture, getString(R.string.done));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            deniedPermission.clear();
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int result = grantResults[i];
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission.add(permission);
                }
            }
            if (deniedPermission.isEmpty()) {
                bindCameraX();

            } else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.capture_permission_message))
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                CaptureActivity.this.finish();
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                            String[] denied = new String[deniedPermission.size()];
                            ActivityCompat.requestPermissions(CaptureActivity.this,
                                    deniedPermission.toArray(denied), PERMISSION_CODE);
                        }).create().show();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void bindCameraX() {
        PreviewConfig config = new PreviewConfig.Builder()
                .setLensFacing(mLensFacing)
                .setTargetRotation(rotation)
                .setTargetResolution(resolution)
                .setTargetAspectRatio(aspectRatio)
                .build();

        preview = new Preview(config);

        imageCapture = new ImageCapture(new ImageCaptureConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(resolution)
                .setLensFacing(mLensFacing)
                .setTargetRotation(rotation)
                .build());

        imageCapture = new ImageCapture(new ImageCaptureConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(resolution)
                .setLensFacing(mLensFacing)
                .setTargetRotation(rotation)
                .build());

        videoCapture = new VideoCapture(new VideoCaptureConfig.Builder()
                .setTargetRotation(rotation)
                .setLensFacing(mLensFacing)
                .setTargetResolution(resolution)
                .setTargetAspectRatio(aspectRatio)
                .setVideoFrameRate(25)
                .setBitRate(3 * 1024 * 1024)
                .build());

        preview.setOnPreviewOutputUpdateListener(output -> {
            TextureView textureView = mBinding.textureView;
            ViewGroup parent = (ViewGroup) textureView.getParent();
            parent.removeView(textureView);
            parent.addView(textureView, 0);
            textureView.setSurfaceTexture(output.getSurfaceTexture());
        });

        CameraX.unbindAll();
        CameraX.bindToLifecycle(this, preview, imageCapture, videoCapture);
    }
}
