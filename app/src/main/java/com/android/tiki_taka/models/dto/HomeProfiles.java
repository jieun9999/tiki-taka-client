package com.android.tiki_taka.models.dto;

public class HomeProfiles {
    private UserProfile userProfile;
    private PartnerProfile partnerProfile;

    // UserProfile 객체를 반환하는 메서드
    public UserProfile getUserProfile() {
        return userProfile;
    }

    // UserProfile 객체를 설정하는 메서드
    public void setUserProfile(UserProfile userProfileDTO) {
        this.userProfile = userProfileDTO;
    }

    // PartnerProfile 객체를 반환하는 메서드
    public PartnerProfile getPartnerProfile() {
        return partnerProfile;
    }

    // PartnerProfile 객체를 설정하는 메서드
    public void setPartnerProfile(PartnerProfile partnerProfile) {
        this.partnerProfile = partnerProfile;
    }

}
