package com.mooc.ppjoke.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.exoplayer.IPlayTarget;
import com.mooc.ppjoke.exoplayer.PageListPlay;
import com.mooc.ppjoke.exoplayer.PageListPlayManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import us.bojie.libcommon.utils.PixUtils;

public class ListPlayerView extends FrameLayout implements IPlayTarget,
        PlayerControlView.VisibilityListener, Player.EventListener {

    private View bufferView;
    protected PPImageView cover;
    private PPImageView blur;
    private ImageView playBtn;
    protected String mCategory;
    protected int mWidthPx;
    protected int mHeightPx;
    protected String mVideoUrl;
    private boolean isPlaying;

    public ListPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true);
        bufferView = findViewById(R.id.buffer_view);
        cover = findViewById(R.id.cover);
        blur = findViewById(R.id.blur_background);
        playBtn = findViewById(R.id.play_btn);

        playBtn.setOnClickListener(v -> {
            if (isPlaying) {
                inActive();
            } else {
                onActive();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        pageListPlay.controlView.show();
        return true;
    }

    public void bindData(String category, int widthPx, int heightPx, String coverUrl, String videoUrl) {
        mCategory = category;
        mWidthPx = widthPx;
        mHeightPx = heightPx;
        mVideoUrl = videoUrl;
        cover.setImageUrl(coverUrl);

        if (widthPx < heightPx) {
            blur.setBlurImageUrl(coverUrl, 50);
            blur.setVisibility(VISIBLE);
        } else {
            blur.setVisibility(INVISIBLE);
        }

        setSize(widthPx, heightPx);
    }

    protected void setSize(int widthPx, int heightPx) {
        int maxWidth = PixUtils.getScreenWidth();
        int maxHeight = maxWidth;

        int layoutWidth = maxWidth;
        int layoutHeight;

        int coverWidth;
        int coverHeight;
        if (widthPx >= heightPx) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPx / (widthPx * 1.0f / maxWidth));
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (heightPx * 1.0f / maxHeight));
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;
        setLayoutParams(params);

        ViewGroup.LayoutParams blurLayoutParams = blur.getLayoutParams();
        blurLayoutParams.width = layoutWidth;
        blurLayoutParams.height = layoutHeight;
        blur.setLayoutParams(blurLayoutParams);

        FrameLayout.LayoutParams coverLayoutParams = (LayoutParams) cover.getLayoutParams();
        coverLayoutParams.width = coverWidth;
        coverLayoutParams.height = coverHeight;
        coverLayoutParams.gravity = Gravity.CENTER;
        cover.setLayoutParams(coverLayoutParams);

        FrameLayout.LayoutParams playBtnLayoutParams = (LayoutParams) playBtn.getLayoutParams();
        playBtnLayoutParams.gravity = Gravity.CENTER;
        playBtn.setLayoutParams(playBtnLayoutParams);
    }

    @Override
    public ViewGroup getOwner() {
        return this;
    }

    @Override
    public void onActive() {
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        PlayerView playerView = pageListPlay.playerView;
        PlayerControlView controlView = pageListPlay.controlView;
        SimpleExoPlayer exoPlayer = pageListPlay.mExoPlayer;
        if (playerView == null) {
            return;
        }

        pageListPlay.switchPlayerView(playerView, true);
        ViewParent parent = playerView.getParent();
        if (parent != this) {
            if (parent != null) {
                ((ViewGroup) parent).removeView(playerView);
                ((ListPlayerView) parent).inActive();
            }
            ViewGroup.LayoutParams params = cover.getLayoutParams();
            this.addView(playerView, 1, params);
        }

        ViewParent ctrlParent = controlView.getParent();
        if (ctrlParent != this) {
            if (ctrlParent != null) {
                ((ViewGroup) ctrlParent).removeView(controlView);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            this.addView(controlView, params);
            controlView.setVisibilityListener(this);
        }
        controlView.show();

        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY);
        } else {
            MediaSource mediaSource = PageListPlayManager.createMediaSource(mVideoUrl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            pageListPlay.playUrl = mVideoUrl;
        }

        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isPlaying = false;
        bufferView.setVisibility(GONE);
        cover.setVisibility(VISIBLE);
        playBtn.setVisibility(VISIBLE);
        playBtn.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public void inActive() {
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        pageListPlay.mExoPlayer.setPlayWhenReady(false);
        playBtn.setVisibility(VISIBLE);
        playBtn.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void onVisibilityChange(int visibility) {
        playBtn.setVisibility(visibility);
        playBtn.setImageResource(isPlaying() ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        SimpleExoPlayer exoPlayer = pageListPlay.mExoPlayer;

        if (playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0) {
            cover.setVisibility(INVISIBLE);
            bufferView.setVisibility(INVISIBLE);
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferView.setVisibility(VISIBLE);
        }

        isPlaying = playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady;
        playBtn.setImageResource(isPlaying ? R.drawable.icon_video_pause : R.drawable.icon_video_play);

    }

    public View getPlayController() {
        PageListPlay listPlay = PageListPlayManager.get(mCategory);
        return listPlay.controlView;
    }
}
