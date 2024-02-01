package com.android.tiki_taka.adapters;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dtos.StoryCardDto;
import com.bumptech.glide.Glide;

public class StoryCardAdapter extends RecyclerView.Adapter<StoryCardAdapter.ViewHolder> {
    private List<StoryCardDto> storyCards;


    public StoryCardAdapter(List<StoryCardDto> storyCards) {
        this.storyCards = storyCards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_card_image, parent, false);
        return new ViewHolder(view); // ViewHolder 객체를 반환해야 합니다.
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 각 아이템을 가리키는 folder
        StoryCardDto card = storyCards.get(position);

        //글라이드 이미지 렌더링
        Glide.with(holder.itemView.getContext())
                .load(card.getImageUrl())
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return storyCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView40);
        }
    }

    // 데이터를 설정하는 setData 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<StoryCardDto> newData){
        storyCards.clear(); //기존 데이터 모두 제거
        storyCards.addAll(newData); //새 데이터를 추가
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알림
    }
}
