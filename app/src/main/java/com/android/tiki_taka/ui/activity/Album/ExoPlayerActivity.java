package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class ExoPlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        playerView = findViewById(R.id.playerView);

        // ExoPlayer 인스턴스 생성
        player = new SimpleExoPlayer.Builder(this).build();

        // PlayerView와 ExoPlayer 인스턴스 연결
        playerView.setPlayer(player);

        // 비디오 URL
        String videoUriString = getIntent().getStringExtra("videoUri");
        if (videoUriString != null) {
            Uri videoUri = Uri.parse(videoUriString);
            // 미디어 아이템을 생성하고 준비
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.prepare();

            // 재생 시작
            player.play();
        } else {
            Toast.makeText(this, "동영상을 재생할 수 없습니다.", Toast.LENGTH_LONG).show();
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        // 리소스 해제
        player.release();
        player = null;
    }
}