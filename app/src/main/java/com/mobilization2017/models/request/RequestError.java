package com.mobilization2017.models.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Pelevin Igor on 21.03.2017.
 */
@JsonObject
public class RequestError {

    @JsonField(name = "code")
    private int errorCode;

    @JsonField
    private String message;

    public RequestError() {

    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
