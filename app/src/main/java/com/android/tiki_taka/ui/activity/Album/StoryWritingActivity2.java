package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tiki_taka.R;

public class StoryWritingActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing2);

        EditText editTextStoryTitle = findViewById(R.id.editTextStoryTitle);
        TextView textViewStoryTitleCount = findViewById(R.id.textViewStoryTitleCount);

        editTextStoryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                textViewStoryTitleCount.setText(text.length() + "/50");
            }

        });

        EditText editTextLocation = findViewById(R.id.editTextLocation);
        TextView textViewLocationCount = findViewById(R.id.textViewLocationCount);

        editTextLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                textViewLocationCount.setText(text.length() + "/30");
            }
        });

    }
}