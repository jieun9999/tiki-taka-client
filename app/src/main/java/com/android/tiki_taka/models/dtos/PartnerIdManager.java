package com.android.tiki_taka.models.dtos;

public class PartnerIdManager {
    private static int partnerId;

    public static int getPartnerId() {
        return partnerId;
    }

    public static void setPartnerId(int partnerId) {
        PartnerIdManager.partnerId = partnerId;
    }
}
