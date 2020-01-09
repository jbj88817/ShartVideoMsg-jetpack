package com.mooc.ppjoke.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import us.bojie.libcommon.PixUtils;
import com.mooc.ppjoke.R;

public class ListPlayerView extends FrameLayout {
    private View bufferView;
    private SVMImageView cover;
    private SVMImageView blur;
    private ImageView playBtn;
    private String mCategroy;
    private String mVideoUrl;

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
    }

    public void bindData(String category, int widthPx, int heightPx, String coverUrl, String videoUrl) {
        mCategroy = category;
        mVideoUrl = videoUrl;

        cover.setImageUrl(cover, coverUrl, false);
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
}
