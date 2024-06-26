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
import com.android.tiki_taka.listeners.DeleteCommentListener;
import com.android.tiki_taka.listeners.EditCommentListener;
import com.android.tiki_taka.models.request.CommentRequest;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.android.tiki_taka.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private static ArrayList<CommentRequest> commentItems;
    private int currentUserId;
    private static DeleteCommentListener deleteCommentListener;
    private boolean showDeleteButton;
    private static EditCommentListener editCommentListener;

    public CommentAdapter(ArrayList<CommentRequest> commentItems, boolean showDeleteButton) {
        this.commentItems = commentItems;
        this.showDeleteButton = showDeleteButton;
    }

    public CommentAdapter(ArrayList<CommentRequest> commentItems, DeleteCommentListener deleteCommentListener, boolean showDeleteButton, EditCommentListener editCommentListener) {
        this.commentItems = commentItems;
        this.deleteCommentListener = deleteCommentListener;
        this.showDeleteButton = showDeleteButton;
        this.editCommentListener = editCommentListener;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_storycard_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
      CommentRequest comment = commentItems.get(position);
        ImageUtils.loadImage(comment.getUserProfile(), holder.userImgView, holder.itemView.getContext());
        holder.commentTextView.setText(comment.getCommentText());

        String relativeTime = TimeUtils.toRelativeTimeFromDb(comment.getCreatedAt());
        holder.createdAtTextView.setText(relativeTime);

        if(showDeleteButton){

            currentUserId = SharedPreferencesHelper.getUserId(holder.itemView.getContext());
            if(comment.getUserId() == currentUserId){
                holder.deleteCommentButton.setVisibility(View.VISIBLE);
                holder.editCommentButton.setVisibility(View.VISIBLE);
            }else {
                holder.deleteCommentButton.setVisibility(View.GONE);
                holder.editCommentButton.setVisibility(View.GONE);
            }

        }else {
            holder.deleteCommentButton.setVisibility(View.GONE);
            holder.editCommentButton.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return commentItems.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView userImgView;
        TextView commentTextView;
        TextView createdAtTextView;
        ImageView deleteCommentButton;
        ImageView editCommentButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userImgView = itemView.findViewById(R.id.imageView41);
            commentTextView = itemView.findViewById(R.id.textView37);
            createdAtTextView = itemView.findViewById(R.id.textView38);
            deleteCommentButton = itemView.findViewById(R.id.imageView43);
            editCommentButton = itemView.findViewById(R.id.imageView34);

            deleteCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deleteCommentListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            deleteCommentListener.onDeleteClick(position);
                        }
                    }
                }
            });

            editCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editCommentListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            int commentId = commentItems.get(position).getCommentId();
                            String commentText = commentItems.get(position).getCommentText();
                            editCommentListener.onEditClick(position, commentId, commentText);
                        }
                    }
                }
            });
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setCommentsData(List<CommentRequest> newCommentsData){
        commentItems.clear();
        commentItems.addAll(newCommentsData);
        notifyDataSetChanged();
    }
}
