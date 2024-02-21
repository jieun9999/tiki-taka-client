package com.android.tiki_taka.models.dtos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CommentItem {
    private String commentText;

    public CommentItem(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

}
