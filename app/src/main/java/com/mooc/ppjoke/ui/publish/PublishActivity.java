package com.mooc.ppjoke.ui.publish;

import android.os.Bundle;

import com.mooc.ppjoke.R;

import androidx.appcompat.app.AppCompatActivity;
import us.bojie.libnavannotation.ActivityDestination;

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_publish);
    }
}
