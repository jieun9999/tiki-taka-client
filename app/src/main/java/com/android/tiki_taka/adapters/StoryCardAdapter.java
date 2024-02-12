package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dtos.StoryCardDto;
import com.bumptech.glide.Glide;

public class StoryCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IMAGE_TYPE = 1;
    private static final int TEXT_TYPE = 2;

    private final List<StoryCardDto> storyCards;
    public StoryCardAdapter(List<StoryCardDto> storyCards) {
        this.storyCards = storyCards;
    }

    @Override
    public int getItemViewType(int position) {
        StoryCardDto storyCardDto = storyCards.get(position);
        switch (storyCardDto.getDataType()){
            case "image" :
                return IMAGE_TYPE;
            case "text" :
                return TEXT_TYPE;
            default :
                return -1;
        }
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public TextViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMAGE_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_card_image, parent, false);
            return new ImageViewHolder(view);

        } else if (viewType == TEXT_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_card_memo, parent, false);
            return new TextViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoryCardDto card = storyCards.get(position);
        
        if (holder.getItemViewType() == IMAGE_TYPE){
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            Glide.with(imageViewHolder.itemView.getContext())
                    .load(card.getImage())
                    .into((imageViewHolder).imageView);
            
        }
        else if (holder.getItemViewType() == TEXT_TYPE) {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.textView.setText(card.getMemo());
        }
    }

    @Override
    public int getItemCount() {
        return storyCards.size();
    }

    // 데이터를 설정하는 setData 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<StoryCardDto> newData){
        storyCards.clear(); //기존 데이터 모두 제거
        storyCards.addAll(newData); //새 데이터를 추가
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알림
    }
}
