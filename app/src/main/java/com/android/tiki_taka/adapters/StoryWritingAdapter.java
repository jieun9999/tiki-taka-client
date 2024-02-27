package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.listeners.PencilIconClickListener;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.VideoUtils;

import java.util.ArrayList;

public class StoryWritingAdapter extends RecyclerView.Adapter<StoryWritingAdapter.ViewHolder> {
    private ArrayList<Uri> uriList;
    private Context context;
    private PencilIconClickListener listener;

    public StoryWritingAdapter(ArrayList<Uri> uriList, Context context, PencilIconClickListener listener) {
        this.uriList = uriList;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageView pencilIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            pencilIcon = itemView.findViewById(R.id.imageView42);
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
    //onBindViewHolder에서 클릭 리스너를 설정하면, 뷰 홀더가 재사용될 때마다 리스너가 새롭게 설정되므로, 현재 위치(position)에 대한 정확한 참조를 유지하는 데 유리
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri uri = uriList.get(position);

        //onBindViewHolder 메서드 내에서 Uri의 타입을 확인하고,
        // 해당하는 처리 방식(이미지 로드 또는 동영상 썸네일 추출 및 로드)을 적용
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("video/")) {
                // 동영상 썸네일 처리
                VideoUtils.loadVideoThumbnail(context, uri, holder.imageView);

            } else if (mimeType.startsWith("image/")) {
                // 이미지 처리
                ImageUtils.loadImage(String.valueOf(uri), holder.imageView, context);
            }
        }

        holder.pencilIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.pencilIconClicked(uriList, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }
}
