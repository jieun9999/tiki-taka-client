package com.android.tiki_taka.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dtos.CommentText;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.UriUtils;
import com.android.tiki_taka.utils.VideoUtils;

import java.util.ArrayList;

public class CommentInputAdapter extends RecyclerView.Adapter<CommentInputAdapter.CommentViewHolder> {
    private ArrayList<Uri> selectedUris;
    private ArrayList<CommentText> commentItems;

    public CommentInputAdapter(ArrayList<Uri> selectedUris) {
        this.selectedUris = selectedUris;
        this.commentItems = new ArrayList<>();
        //초기 댓글 아이템 리스트를 설정
        for(int i = 0; i < selectedUris.size(); i++){
            commentItems.add(new CommentText(""));
        }
        // commentItems 리스트에 CommentItem 객체를 미리 추가함으로써,
        // onBindViewHolder에서 각 뷰 홀더의 위치(position)에 해당하는 CommentItem 객체에 접근할 때 항상 유효한 객체를 얻을 수 있습니다.
    }

    @NonNull
    @Override
    public CommentInputAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.item_story_comment_input,parent,false);
       return new CommentViewHolder(itemView);
    }

    @Override
    // RecyclerView는 스크롤 등의 사용자 액션에 의해 새로운 아이템이 화면에 표시되어야 할 때마다 onBindViewHolder를 호출
    // 두 가지 주요 매개변수 : ViewHolder, position
    public void onBindViewHolder(@NonNull CommentInputAdapter.CommentViewHolder holder, int position) {
        // 이전에 설정된 TextWatcher를 제거한다
        if(holder.textWatcher != null){
            holder.storyComment.removeTextChangedListener(holder.textWatcher);
        }

        Uri itemUri = selectedUris.get(position);
        if(UriUtils.isVideoUri(itemUri, holder.itemView.getContext())){
            // 동영상 URI인 경우 썸네일을 Glide를 사용하여 렌더링
            VideoUtils.loadVideoThumbnail(holder.itemView.getContext(), itemUri, holder.storyImage);
        } else if (UriUtils.isImageUri(itemUri, holder.itemView.getContext())){
            // 이미지 URI인 경우 이미지를 그대로 렌더링
            ImageUtils.loadImage(itemUri.toString(), holder.storyImage, holder.itemView.getContext());
        }

        // 새 TextWatcher 인스턴스 생성
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CommentText commentItem = commentItems.get(holder.getAdapterPosition());
                commentItem.setCommentText(s.toString());
            }
        };

        // EditText에 TextWatcher 추가
        holder.storyComment.addTextChangedListener(textWatcher);
        // ViewHolder의 textWatcher 필드에 현재 TextWatcher 저장
        holder.textWatcher = textWatcher;
    }

    @Override
    public int getItemCount() {
        return selectedUris.size();
    }

    // 뷰 홀더는 UI 컴포넌트를 초기화하는 역할만 하며, 클릭 이벤트 처리 로직은 onBindViewHolder에서 구현됨.
    // 이 방식은 뷰 홀더의 재사용성을 높이고, UI 설정과 이벤트 처리 로직을 분리
    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView storyImage;
        EditText storyComment;
        TextWatcher textWatcher;
        // 뷰홀더의 TextWatcher 인스턴스를 추적하고 관리
        //onBindViewHolder 메서드에서 새로운 TextWatcher 인스턴스를 생성하고
        // 이를 뷰 홀더의 textWatcher 필드에 할당하는 것은, RecyclerView의 각 항목이 사용자의 입력을 독립적으로 처리할 수 있도록 하기 위함입니다.

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            storyImage = itemView.findViewById(R.id.story_image);
            storyComment = itemView.findViewById(R.id.story_comment);
        }
    }

    // 사용자 댓글 입력을 수집하는 메소드
    public ArrayList<String> collectCommentText(){
        ArrayList<String> commentTexts = new ArrayList<>();
        for(CommentText commentItem : commentItems){
            commentTexts.add(commentItem.getCommentText());
        }
        return commentTexts;
    }

}
