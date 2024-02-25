package com.android.tiki_taka.utils;

import android.util.Pair;

import com.android.tiki_taka.models.dtos.StoryCard;

public class LikesUtils {
    // 두명의 유저id 중 숫자가 작은 쪽이 user_a_likes, 큰쪽이 user_b_likes 자동으로 할당돼
    public static Pair<Integer, Integer> getLikesFor2Users(StoryCard storyCard, int currentUserId, int partnerId){
        int myLikes;
        int partnerLikes;
        if(currentUserId < partnerId){
            myLikes = storyCard.getUserALikes(); // 현재 사용자가 숫자가 작은 쪽인 경우
            partnerLikes = storyCard.getUserBLikes(); // 현재 사용자의 파트너는 숫자가 큰 쪽
        }else {
            myLikes = storyCard.getUserBLikes(); // 현재 사용자가 숫자가 큰 쪽인 경우
            partnerLikes = storyCard.getUserALikes(); // 현재 사용자의 파트너는 숫자가 작은 쪽
        }
        return new Pair<>(myLikes, partnerLikes);
    }

}
