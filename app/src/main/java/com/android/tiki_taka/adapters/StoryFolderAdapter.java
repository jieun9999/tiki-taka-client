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
import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.utils.TimeUtils;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.List;

public class StoryFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IMAGE_TYPE = 1;
    private static final int TEXT_TYPE = 2;

    private List<StoryFolder> storyFolders;
    private ItemClickListener itemClickListener;

    public StoryFolderAdapter(List<StoryFolder> storyFolders, ItemClickListener itemClickListener) {
        this.storyFolders = storyFolders;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        StoryFolder storyFolderDto = storyFolders.get(position);
        switch (storyFolderDto.getDataType()){
            case "image" :
                return IMAGE_TYPE;
            case "text" :
                return TEXT_TYPE;
            default :
                return -1;
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView title;
        TextView location;
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textView26);
            title = itemView.findViewById(R.id.textView27);
            location = itemView.findViewById(R.id.textView28);
            imageView = itemView.findViewById(R.id.imageView26);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        TextView memo;
        TextView date;

        public TextViewHolder(View itemView){
            super(itemView);
            memo = itemView.findViewById(R.id.textView27);
            date = itemView.findViewById(R.id.textView28);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == IMAGE_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_folder_image, parent, false);
            return new ImageViewHolder(view);

        } else if (viewType == TEXT_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_folder_memo, parent, false);
            return new TextViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoryFolder folder = storyFolders.get(position);

        if(holder.getItemViewType() == IMAGE_TYPE){
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            // 서버 날짜 문자열(2024-01-31 12:24:40) => 2023년 12월 25일 (월) 변환
            String inputDateString = folder.getCreatedAt();
            try {
                String outputDateString = TimeUtils.convertDateString(inputDateString);
                imageViewHolder.date.setText(outputDateString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            imageViewHolder.title.setText(folder.getTitle());
            imageViewHolder.location.setText(folder.getLocation());
            //글라이드 이미지 렌더링
            Glide.with(imageViewHolder.itemView.getContext())
                    .load(folder.getDisplayImage())
                    .into(imageViewHolder.imageView);

        } else if (holder.getItemViewType() == TEXT_TYPE) {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.memo.setText(folder.getTitle());
            String inputDateString = folder.getCreatedAt();
            try {
                String outputDateString = TimeUtils.convertDateString(inputDateString);
                textViewHolder.date.setText(outputDateString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
        // 클릭 리스너 설정
        holder.itemView.setOnClickListener( v -> itemClickListener.onItemClick(position));

    }


    @Override
    public int getItemCount() {
        return storyFolders.size();
    }


    // 데이터를 설정하는 setData 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<StoryFolder> newData){
        storyFolders.clear(); // 기존 데이터를 모두 제거
        storyFolders.addAll(newData); // 새 데이터를 추가
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알림
    }

    //  아이템의 ID 가져오는 getItem 메서드 추가
    public StoryFolder getItem(int position){
        return storyFolders.get(position);
    }
    // return 값의 형태가 StoryFolder라서 메서드 앞부분에 적어줌
}
