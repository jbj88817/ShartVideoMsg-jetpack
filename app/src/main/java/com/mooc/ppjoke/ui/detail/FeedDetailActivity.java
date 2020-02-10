package com.mooc.ppjoke.ui.detail;

import android.os.Bundle;

import com.mooc.ppjoke.model.Feed;

import androidx.appcompat.app.AppCompatActivity;

public class FeedDetailActivity extends AppCompatActivity {

    public static final String KEY_FEED = "key_feed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_feed_detail);

        Feed feed = getIntent().getParcelableExtra(KEY_FEED);
        if (feed == null) {
            finish();
            return;
        }

        ViewHandler viewHandler = null;
        if (feed.getItemType() == Feed.TYPE_IMAGE) {
            viewHandler = new ImageViewHandler(this);
        } else {
            viewHandler = new VideoViewHandler(this);
        }

        viewHandler.bindInitData(feed);
    }
}
