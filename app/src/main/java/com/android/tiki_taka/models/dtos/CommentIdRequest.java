package com.android.tiki_taka.models.dtos;

public class CommentIdRequest {
    private int commentId;

    public CommentIdRequest(int commentId) {
        this.commentId = commentId;
    }

    // Getter and Setter
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
}
