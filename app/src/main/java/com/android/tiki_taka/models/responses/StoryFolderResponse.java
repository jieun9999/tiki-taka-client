package com.android.tiki_taka.models.responses;

import com.android.tiki_taka.models.dtos.StoryFolder;

public class StoryFolderResponse {
    private boolean success;
    private String message;
    private StoryFolder storyFolder;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StoryFolder getStoryFolder() {
        return storyFolder;
    }

    public void setStoryFolder(StoryFolder storyFolder) {
        this.storyFolder = storyFolder;
    }
}
