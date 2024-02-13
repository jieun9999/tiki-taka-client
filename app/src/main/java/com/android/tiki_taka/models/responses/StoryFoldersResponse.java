package com.android.tiki_taka.models.responses;

import com.android.tiki_taka.models.dtos.StoryFolder;

import java.util.List;

public class StoryFoldersResponse {
    private boolean success;
    private String message;
    private List<StoryFolder> storyFolders;


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

    public List<StoryFolder> getStoryFolders() {
        return storyFolders;
    }

    public void setStoryFolders(List<StoryFolder> storyFolders) {
        this.storyFolders = storyFolders;
    }
}
