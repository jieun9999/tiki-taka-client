package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.tiki_taka.R;

public class LocalVideoPlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_player);

        VideoView videoView = findViewById(R.id.videoView);
        String videoUriString = getIntent().getStringExtra("videoUriString");
        if (videoUriString != null) {
            Uri videoUri = Uri.parse(videoUriString);

            videoView.setVideoURI(videoUri);
            videoView.start();

        } else {
            Toast.makeText(this, "동영상을 재생할 수 없습니다.", Toast.LENGTH_LONG).show();
        }
    }
}