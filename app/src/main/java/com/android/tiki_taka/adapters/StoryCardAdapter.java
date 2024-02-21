package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.android.tiki_taka.R;
import com.android.tiki_taka.listeners.ItemClickListener;
import com.android.tiki_taka.models.dtos.CommentItem;
import com.android.tiki_taka.models.dtos.StoryCard;
import com.android.tiki_taka.ui.activity.Album.VideoPlayerActivity;
import com.android.tiki_taka.utils.VideoUtils;
import com.bumptech.glide.Glide;

public class StoryCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IMAGE_TYPE = 1;
    private static final int TEXT_TYPE = 2;
    private static final int VIDEO_TYPE = 3;

    private final List<StoryCard> storyCards;
    private static ItemClickListener itemClickListener;

    public StoryCardAdapter(List<StoryCard> storyCards,  ItemClickListener itemClickListener) {
        this.storyCards = storyCards;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        StoryCard storyCardDto = storyCards.get(position);
        switch (storyCardDto.getDataType()){
            case "image" :
                return IMAGE_TYPE;
            case "text" :
                return TEXT_TYPE;
            case "video":
                return VIDEO_TYPE;
            default :
                return -1;
        }
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView allCommentsView;

        public ImageViewHolder(@NonNull View itemView, final ItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            allCommentsView = itemView.findViewById(R.id.all_comments);

            // 클릭 리스너 설정
            allCommentsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        itemClickListener.onItemClick(position);
                    }
                }
            });
        }

    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public TextViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.editBtn);
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageView playBtn;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            playBtn = itemView.findViewById(R.id.playBtn);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMAGE_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_card_image, parent, false);
            return new ImageViewHolder(view, itemClickListener);

        } else if (viewType == TEXT_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_card_memo, parent, false);
            return new TextViewHolder(view);

        }else if(viewType == VIDEO_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_card_video,parent,false);
            return new VideoViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoryCard card = storyCards.get(position);
        
        if (holder.getItemViewType() == IMAGE_TYPE){
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            Glide.with(imageViewHolder.itemView.getContext())
                    .load(card.getImage())
                    .into(imageViewHolder.imageView);
            
        }
        else if (holder.getItemViewType() == TEXT_TYPE) {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.textView.setText(card.getMemo());

        } else if (holder.getItemViewType() == VIDEO_TYPE) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            String videoId = VideoUtils.extractYoutubeVideoId(card.getVideo());
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg"; // 썸네일 URL 생성

            Glide.with(videoViewHolder.itemView.getContext())
                    .asBitmap()
                    .load(thumbnailUrl)
                    .into(videoViewHolder.imageView);

            videoViewHolder.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(videoViewHolder.itemView.getContext(), VideoPlayerActivity.class);
                    intent.putExtra("VIDEO_ID", videoId);
                    videoViewHolder.itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return storyCards.size();
    }

    // 데이터를 설정하는 setData 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    public void setCardsData(List<StoryCard> newData){
        storyCards.clear(); //기존 데이터 모두 제거
        storyCards.addAll(newData); //새 데이터를 추가
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알림
    }

}
