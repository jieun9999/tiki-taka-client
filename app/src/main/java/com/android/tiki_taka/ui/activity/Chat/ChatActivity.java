package com.android.tiki_taka.ui.activity.Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.request.StoryCardRequest;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupUIListeners();
    }

    private void setupUIListeners(){
        ImageView cancelBtn = findViewById(R.id.imageView36);
        cancelBtn.setOnClickListener( v -> finish());
    }
}