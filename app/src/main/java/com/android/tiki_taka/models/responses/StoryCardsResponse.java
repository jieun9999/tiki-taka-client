package com.android.tiki_taka.models.responses;

import com.android.tiki_taka.models.dtos.StoryCardDto;

import java.util.List;

public class StoryCardsResponse {
    private boolean success;
    private String message;
    private List<StoryCardDto> storyCards;


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

    public List<StoryCardDto> getStoryCards() {
        return storyCards;
    }

    public void setStoryCards(List<StoryCardDto> storyCards) {
        this.storyCards = storyCards;
    }
}
