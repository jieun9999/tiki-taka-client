package com.android.tiki_taka.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.utils.ImageUtils;

import java.util.ArrayList;

public class StoryWritingAdapter extends RecyclerView.Adapter<StoryWritingAdapter.ViewHolder> {

    private ArrayList<Uri> uriList;
    private Context context;

    public StoryWritingAdapter(ArrayList<Uri> uriList, Context context) {
        this.uriList = uriList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageView pencilIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            pencilIcon = imageView.findViewById(R.id.imageView42);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_small_story_card, parent, false);
        return new ViewHolder(view);
    }

    // 각 항목을 화면에 표시할 준비가 될 때마다 이 메소드가 호출되며, 호출될 때마다 하나의 항목에 대한 데이터를 뷰 홀더와 연결하는 역할을 합니다
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = uriList.get(position);
        ImageUtils.loadImage(String.valueOf(uri), holder.imageView, context);
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }
}