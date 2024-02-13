package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import com.android.tiki_taka.R;

import java.util.ArrayList;

public class StoryWritingActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing1);

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        ArrayList<Parcelable> selectedUris = getIntent().getParcelableArrayListExtra("selectedUris");
        // 어댑터를 초기화하고 리사이클러뷰에 설정합니다.
        // 예: recyclerView.setAdapter(new MyAdapter(selectedUris));
    }
}