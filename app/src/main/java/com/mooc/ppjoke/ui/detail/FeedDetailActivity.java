package com.mooc.ppjoke.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mooc.ppjoke.model.Feed;

import androidx.appcompat.app.AppCompatActivity;

public class FeedDetailActivity extends AppCompatActivity {

    public static final String KEY_FEED = "key_feed";
    public static final String KEY_CATEGORY = "key_category";

    public static void startFeedDetailActivity(Context context, Feed item, String category) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        intent.putExtra(KEY_FEED, item);
        intent.putExtra(KEY_CATEGORY, category);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Feed feed = (Feed) getIntent().getSerializableExtra(KEY_FEED);
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
