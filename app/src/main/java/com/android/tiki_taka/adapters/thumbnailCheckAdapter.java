package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.utils.ImageUtils;

import java.util.ArrayList;

public class thumbnailCheckAdapter extends RecyclerView.Adapter<thumbnailCheckAdapter.ViewHolder> {
    private ArrayList<Uri> uriList;
    private Context context;
    private int currentPosition = -1; // 어댑터 수준에서 현재 선택된 위치 관리

    public thumbnailCheckAdapter(ArrayList<Uri> uriList, Context context) {
        this.uriList = uriList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_check_story_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // checkBox 클릭 리스너는 onBindViewHolder에서 설정
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri uri = uriList.get(position);
        ImageUtils.loadImage(String.valueOf(uri), holder.imageView, context);
        //  단일 선택만 가능한 체크박스를 구현
        holder.checkBox.setChecked(position == currentPosition); // 현재 위치가 선택된 위치와 같다면 체크 박스 선택
        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                int previousPosition = currentPosition;
                currentPosition = position;
                notifyItemChanged(previousPosition); // 이전 선택 해제
                notifyItemChanged(currentPosition); // 현재 선택
            } else {
                currentPosition = -1; // 선택 해제
            }
        });
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
