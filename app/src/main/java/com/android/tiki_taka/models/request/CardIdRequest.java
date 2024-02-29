package com.android.tiki_taka.models.request;

public class CardIdRequest {

    private int cardId;

    public CardIdRequest(int cardId) {
        this.cardId = cardId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
