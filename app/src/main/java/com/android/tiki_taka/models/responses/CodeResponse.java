package com.android.tiki_taka.models.responses;

import com.google.gson.annotations.SerializedName;

public class CodeResponse {

    // @SerializedName 애너테이션은 Gson 라이브러리의 일부로, JSON 키 이름과 자바 클래스 필드 이름이 다를 때 사용됨
    // 그래도 혹시 모르니 써주자!
    @SerializedName("invitationCode")
    private String invitationCode;

    @SerializedName("codeDate")
    private String codeDate;

    // 생성자, getter 및 setter 등을 정의
    public CodeResponse(String invitationCode, String codeDate) {
        this.invitationCode = invitationCode;
        this.codeDate = codeDate;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getCodeDate() {
        return codeDate;
    }

    public void setCodeDate(String codeDate) {
        this.codeDate = codeDate;
    }
}
