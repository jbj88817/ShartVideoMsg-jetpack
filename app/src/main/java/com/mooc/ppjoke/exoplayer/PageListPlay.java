package com.mooc.ppjoke.exoplayer;

import android.app.Application;
import android.view.LayoutInflater;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.mooc.ppjoke.R;

import us.bojie.libcommon.AppGlobals;

public class PageListPlay {

    public SimpleExoPlayer mExoPlayer;
    public PlayerView playerView;
    public PlayerControlView controlView;
    public String playUrl;

    public PageListPlay() {
        Application application = AppGlobals.getApplication();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(application,
                new DefaultRenderersFactory(application),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        playerView = (PlayerView) LayoutInflater.from(application)
                .inflate(R.layout.layout_exo_player_view, null, false);
        controlView = (PlayerControlView) LayoutInflater.from(application)
                .inflate(R.layout.layout_exo_player_controller_view, null, false);

        playerView.setPlayer(mExoPlayer);
        controlView.setPlayer(mExoPlayer);
    }

    public void release() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.stop(true);
            mExoPlayer.release();
            mExoPlayer = null;
        }

        if (playerView != null) {
            playerView.setPlayer(null);
            playerView = null;
        }

        if (controlView != null) {
            controlView.setPlayer(null);
            controlView.setVisibilityListener(null);
            controlView = null;
        }
    }

    public void switchPlayerView(PlayerView newPlayerView, boolean attach) {
        playerView.setPlayer(attach ? null : mExoPlayer);
        newPlayerView.setPlayer(attach ? mExoPlayer : null);
    }
}
