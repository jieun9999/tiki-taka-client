package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.android.tiki_taka.R;
import com.android.tiki_taka.listeners.ItemClickListener;
import com.android.tiki_taka.models.dto.CommentItem;
import com.android.tiki_taka.models.dto.PartnerDataManager;
import com.android.tiki_taka.models.dto.StoryCard;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.LikesUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.android.tiki_taka.utils.VideoUtils;
import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        ImageView myLikesView;
        ImageView partnerLikesProfileView;
        FrameLayout partnerLikesView;
        TextView allCommentsView;
        RecyclerView commentRecyclerView;
        CommentAdapter commentAdapter;


        public ImageViewHolder(@NonNull View itemView, final ItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnailView);
            myLikesView = itemView.findViewById(R.id.imageView31);
            partnerLikesProfileView = itemView.findViewById(R.id.imageView33);
            partnerLikesView = itemView.findViewById(R.id.frameLayout9);

            allCommentsView = itemView.findViewById(R.id.all_comments);
            commentRecyclerView = itemView.findViewById(R.id.recyclerView3);

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

            // commentRecyclerView 설정
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            commentAdapter = new CommentAdapter(new ArrayList<>(),false);
            commentRecyclerView.setAdapter(commentAdapter);

        }

    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TextView allCommentsView;
        ImageView myLikesView;
        ImageView partnerLikesProfileView;
        FrameLayout partnerLikesView;
        RecyclerView commentRecyclerView;
        CommentAdapter commentAdapter;

        public TextViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.memoView);
            myLikesView = itemView.findViewById(R.id.imageView31);
            partnerLikesProfileView = itemView.findViewById(R.id.imageView33);
            partnerLikesView = itemView.findViewById(R.id.frameLayout9);
            allCommentsView = itemView.findViewById(R.id.all_comments);
            commentRecyclerView = itemView.findViewById(R.id.recyclerView3);

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

            // commentRecyclerView 설정
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            commentAdapter = new CommentAdapter(new ArrayList<>(), false);
            commentRecyclerView.setAdapter(commentAdapter);

        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageView myLikesView;
        ImageView partnerLikesProfileView;
        FrameLayout partnerLikesView;
        ImageView playBtn;
        TextView allCommentsView;
        RecyclerView commentRecyclerView;
        CommentAdapter commentAdapter;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnailView);
            myLikesView = itemView.findViewById(R.id.imageView31);
            partnerLikesProfileView = itemView.findViewById(R.id.imageView33);
            partnerLikesView = itemView.findViewById(R.id.frameLayout9);
            playBtn = itemView.findViewById(R.id.playBtn);
            allCommentsView = itemView.findViewById(R.id.all_comments);
            commentRecyclerView = itemView.findViewById(R.id.recyclerView3);

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

            // commentRecyclerView 설정
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            commentAdapter = new CommentAdapter(new ArrayList<>(), false);
            commentRecyclerView.setAdapter(commentAdapter);
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
        int userId = SharedPreferencesHelper.getUserId(holder.itemView.getContext());
        int partnerId = PartnerDataManager.getPartnerId();
        String partnerImg = PartnerDataManager.getPartnerImg();
        
        if (holder.getItemViewType() == IMAGE_TYPE){
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            Glide.with(imageViewHolder.itemView.getContext())
                    .load(card.getImage())
                    .into(imageViewHolder.imageView);

            //나와 상대방의 좋아요 상태 렌더링
            Pair<Integer, Integer> likes = LikesUtils.getLikesFor2Users(card, userId, partnerId);
            renderLikes( imageViewHolder.myLikesView, imageViewHolder.partnerLikesView, imageViewHolder.partnerLikesProfileView, likes, partnerImg);

            // 비동기적으로 댓글 데이터 요청
            loadPreviewComments(card.getCardId(), imageViewHolder.commentAdapter);
            
        }
        else if (holder.getItemViewType() == TEXT_TYPE) {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.textView.setText(card.getMemo());

            //나와 상대방의 좋아요 상태 렌더링
            Pair<Integer, Integer> likes = LikesUtils.getLikesFor2Users(card, userId, partnerId);
            renderLikes(textViewHolder.myLikesView, textViewHolder.partnerLikesView, textViewHolder.partnerLikesProfileView, likes, partnerImg);

            // 비동기적으로 댓글 데이터 요청
            loadPreviewComments(card.getCardId(), textViewHolder.commentAdapter);

        } else if (holder.getItemViewType() == VIDEO_TYPE) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            String video = card.getVideo();

            if(video.startsWith("https://")){
                ImageUtils.loadImage(video,  videoViewHolder.imageView, videoViewHolder.itemView.getContext());

            } else {
                // 크롭한 사진은 화질이 너무 저하되서 글라이드 동영상 uri로 렌더링
                Uri videoUri = Uri.parse(video);
                VideoUtils.loadVideoThumbnail( videoViewHolder.itemView.getContext() , videoUri, videoViewHolder.imageView);
            }

            videoViewHolder.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                VideoUtils.openVideo(videoViewHolder.itemView.getContext(), video);
                }
            });

            //나와 상대방의 좋아요 상태 렌더링
            Pair<Integer, Integer> likes = LikesUtils.getLikesFor2Users(card, userId, partnerId);
            renderLikes( videoViewHolder.myLikesView, videoViewHolder.partnerLikesView, videoViewHolder.partnerLikesProfileView, likes, partnerImg);

            // 비동기적으로 댓글 데이터 요청
            loadPreviewComments(card.getCardId(), videoViewHolder.commentAdapter);
        }

    }

    public static void loadPreviewComments(int cardId, CommentAdapter commentAdapter){
        StoryApiService service = RetrofitClient.getClient().create(StoryApiService.class);
        service.getPreviewComments(cardId).enqueue(new Callback<List<CommentItem>>() {
            @Override
            public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
                if(response.isSuccessful()){
                    List<CommentItem> comments = response.body();
                    if (!comments.isEmpty()) {
                        commentAdapter.setCommentsData(comments);
                    }
                }
            }

            @Override
            // 서버 응답이 비어있거나, 서버에서 오류가 발생했을 때
            public void onFailure(Call<List<CommentItem>> call, Throwable t) {
                Log.e("loadCommentsAsync", "댓글 데이터 요청 실패: " + t.getMessage());
            }
        });
    }

    private void renderLikes(ImageView myLikesImageView, FrameLayout partnerLikesView,  ImageView partnerLikesProfileView, Pair<Integer, Integer> likes, String partnerImg){
        int myLikes = likes.first;
        int partnerLikes = likes.second;
        if(myLikes == 0){
            ImageUtils.loadDrawableIntoView(myLikesImageView.getContext(), myLikesImageView, "akar_icons_heart");
        }else if(myLikes == 1){
            ImageUtils.loadDrawableIntoView(myLikesImageView.getContext(), myLikesImageView, "fluent_emoji_flat_red_heart");
        }
        ImageUtils.loadImage(partnerImg, partnerLikesProfileView, partnerLikesProfileView.getContext());
        if(partnerLikes == 0){
            partnerLikesView.setVisibility(View.GONE);
        } else if (partnerLikes == 1) {
            partnerLikesView.setVisibility(View.VISIBLE);
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
