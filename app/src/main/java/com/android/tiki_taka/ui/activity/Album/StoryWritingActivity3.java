package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.CommentInputAdapter;

import java.util.ArrayList;

public class StoryWritingActivity3 extends AppCompatActivity  {
    RecyclerView recyclerView;
    ArrayList<Uri> selectedUris;
    CommentInputAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing3);

        TextView cancelBtn = findViewById(R.id.textView33);
        TextView saveBtn = findViewById(R.id.textView34);

        cancelBtn.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        if (intent != null) {
            selectedUris = intent.getParcelableArrayListExtra("selectedImages");
        }

        recyclerView = findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CommentInputAdapter(selectedUris);
        recyclerView.setAdapter(adapter);

    }
}