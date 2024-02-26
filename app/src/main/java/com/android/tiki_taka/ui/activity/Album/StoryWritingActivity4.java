package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.tiki_taka.R;

public class StoryWritingActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing4);

        String currentDate = getIntent().getStringExtra("currentDate");
        TextView toolbarDateView = findViewById(R.id.textView35);
        toolbarDateView.setText(currentDate);

        TextView cancelBtn = findViewById(R.id.textView33);
        cancelBtn.setOnClickListener( v -> finish());
        TextView uploadBtn = findViewById(R.id.textView34);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "여기에 결과값 입력");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }
}