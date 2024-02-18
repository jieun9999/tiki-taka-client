package com.android.tiki_taka.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.utils.ImageUtils;

import java.util.ArrayList;

public class CommentInputAdapter extends RecyclerView.Adapter<CommentInputAdapter.CommentViewHolder> {
    ArrayList<Uri> selectedUris;

    public CommentInputAdapter(ArrayList<Uri> selectedUris) {
        this.selectedUris = selectedUris;
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
        Uri storyImage = selectedUris.get(position);
        ImageUtils.loadImage(storyImage.toString(), holder.storyImage, holder.itemView.getContext());

        holder.storyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            storyImage = itemView.findViewById(R.id.story_image);
            storyComment = itemView.findViewById(R.id.story_comment);
        }
    }

//    private void createSendBundle(Context context, Uri storyImage, EditText storyComment){
//        Intent intent = new Intent(context, StoryWritingActivity1.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("imageUriString", storyImage.toString());
//        String commentText = storyComment.getText().toString();
//        bundle.putString("commentText", commentText);
//
//        intent.putExtras(bundle);
////        setResult(RESULT_OK, intent);
////        finish();
//    }
}
