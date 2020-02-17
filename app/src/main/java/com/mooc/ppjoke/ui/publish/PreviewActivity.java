package com.mooc.ppjoke.ui.publish;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.ActivityLayoutPreviewBinding;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLayoutPreviewBinding mBinding;
    public static final String KEY_PREVIEW_URL = "key_preview_url";
    public static final String KEY_PREVIEW_VIDEO = "key_preview_video";
    public static final String KEY_PREVIEW_BTN_TEXT = "key_preview_btn_text";
    public static final int REQ_PREVIEW = 1000;
    private SimpleExoPlayer player;

    public static void startActivityForResult(Activity activity, String previewUrl,
                                              boolean isVideo, String buttonText) {
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra(KEY_PREVIEW_URL, previewUrl);
        intent.putExtra(KEY_PREVIEW_VIDEO, isVideo);
        intent.putExtra(KEY_PREVIEW_BTN_TEXT, buttonText);
        activity.startActivityForResult(intent, REQ_PREVIEW);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_preview);
        String previewUrl = getIntent().getStringExtra(KEY_PREVIEW_URL);
        boolean isVideo = getIntent().getBooleanExtra(KEY_PREVIEW_VIDEO, false);
        String btnText = getIntent().getStringExtra(KEY_PREVIEW_BTN_TEXT);
        if (TextUtils.isEmpty(btnText)) {
            mBinding.actionOk.setVisibility(View.GONE);
        } else {
            mBinding.actionOk.setVisibility(View.VISIBLE);
            mBinding.actionOk.setText(btnText);
            mBinding.actionOk.setOnClickListener(this);
        }

        mBinding.actionClose.setOnClickListener(this);

        if (isVideo) {
            previewVideo(previewUrl);
        } else {
            previewImage(previewUrl);
        }
    }

    private void previewImage(String previewUrl) {
        mBinding.photoView.setVisibility(View.VISIBLE);
        Glide.with(this).load(previewUrl).into(mBinding.photoView);
    }

    private void previewVideo(String previewUrl) {
        mBinding.playerView.setVisibility(View.VISIBLE);
        player = ExoPlayerFactory.newSimpleInstance(this,
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        Uri uri = null;
        File file = new File(previewUrl);
        if (file.exists()) {
            DataSpec dataSpec = new DataSpec(Uri.fromFile(file));
            FileDataSource fileDataSource = new FileDataSource();
            try {
                fileDataSource.open(dataSpec);
                uri = fileDataSource.getUri();
            } catch (FileDataSource.FileDataSourceException e) {
                e.printStackTrace();
            }
        } else {
            uri = Uri.parse(previewUrl);
        }

        ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(
                new DefaultDataSourceFactory(this,
                        Util.getUserAgent(this, getPackageName())));
        ProgressiveMediaSource mediaSource = factory.createMediaSource(uri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        mBinding.playerView.setPlayer(player);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop(true);
            player.release();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_close) {
            finish();
        } else if (v.getId() == R.id.action_ok) {
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }
}
