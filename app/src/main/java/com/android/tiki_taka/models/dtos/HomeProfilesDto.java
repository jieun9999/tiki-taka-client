package com.android.tiki_taka.models.dtos;

public class HomeProfilesDto {
    private UserProfileDto userProfile;
    private PartnerProfileDto partnerProfile;

    // UserProfile 객체를 반환하는 메서드
    public UserProfileDto getUserProfile() {
        return userProfile;
    }

    // UserProfile 객체를 설정하는 메서드
    public void setUserProfile(UserProfileDto userProfileDTO) {
        this.userProfile = userProfileDTO;
    }

    // PartnerProfile 객체를 반환하는 메서드
    public PartnerProfileDto getPartnerProfile() {
        return partnerProfile;
    }

    // PartnerProfile 객체를 설정하는 메서드
    public void setPartnerProfile(PartnerProfileDto partnerProfile) {
        this.partnerProfile = partnerProfile;
    }

}
