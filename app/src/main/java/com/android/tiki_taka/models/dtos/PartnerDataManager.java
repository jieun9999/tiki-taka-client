package com.android.tiki_taka.models.dtos;

public class PartnerDataManager {
    private static int partnerId;
    public static String partnerImg;

    public static int getPartnerId() {
        return partnerId;
    }

    public static void setPartnerId(int partnerId) {
        PartnerDataManager.partnerId = partnerId;
    }

    public static String getPartnerImg() {
        return partnerImg;
    }

    public static void setPartnerImg(String partnerImg) {
        PartnerDataManager.partnerImg = partnerImg;
    }
}
