package com.android.tiki_taka.adapters;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.listeners.ItemClickListener;
import com.android.tiki_taka.models.dtos.StoryFolderDto;
import com.android.tiki_taka.utils.DateUtils;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.List;

public class StoryFolderAdapter extends RecyclerView.Adapter<StoryFolderAdapter.ViewHolder> {
    private List<StoryFolderDto> storyFolders;
    private ItemClickListener itemClickListener;
    public StoryFolderAdapter(List<StoryFolderDto> storyFolders, ItemClickListener itemClickListener) {
        this.storyFolders = storyFolders;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public StoryFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_folder_image, parent, false);
        return new ViewHolder(view); // ViewHolder 객체를 반환해야 합니다.
    }

    @Override
    public void onBindViewHolder(@NonNull StoryFolderAdapter.ViewHolder holder, int position) {
        // 각 아이템을 가리키는 folder
        StoryFolderDto folder = storyFolders.get(position);

        // 서버 날짜 문자열(2024-01-31 12:24:40) => 2023년 12월 25일 (월) 변환
        String inputDateString = folder.getUpdatedAt();
        try {
            String outputDateString = DateUtils.convertDateString(inputDateString);
            holder.date.setText(outputDateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        holder.title.setText(folder.getTitle());
        holder.location.setText(folder.getLocation());
        //글라이드 이미지 렌더링
        Glide.with(holder.itemView.getContext())
                .load(folder.getDisplayImageUrl()) // URL 경로
                .into(holder.backImg); // 이미지를 표시할 ImageView

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener( v -> itemClickListener.onItemClick(position));

    }

    @Override
    public int getItemCount() {
        return storyFolders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView title;
        TextView location;
        ImageView backImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textView26);
            title = itemView.findViewById(R.id.textView27);
            location = itemView.findViewById(R.id.textView28);
            backImg = itemView.findViewById(R.id.imageView26);
        }
    }

    // 데이터를 설정하는 setData 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<StoryFolderDto> newData){
        storyFolders.clear(); // 기존 데이터를 모두 제거
        storyFolders.addAll(newData); // 새 데이터를 추가
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알림
    }

    //  아이템의 ID 가져오는 getItem 메서드 추가
    public StoryFolderDto getItem(int position){
        return storyFolders.get(position);
    }
    // return 값의 형태가 StoryFolder라서 메서드 앞부분에 적어줌
}
