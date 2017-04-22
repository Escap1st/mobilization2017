package com.mobilization2017.models.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

import java.util.ArrayList;

/**
 * Created by Pelevin Igor on 20.03.2017.
 */

@JsonObject
public class Translation implements Parcelable {

    @JsonField(name = "lang")
    private String languages;

    @JsonField
    private ArrayList<String> text;

    public Translation() {

    }

    private String originalLanguageCode;

    private String originalLanguageName;

    private String resultLanguageCode;

    private String resultLanguageName;

    @OnJsonParseComplete
    public void onParseComplete() {
        try {
            originalLanguageCode = languages.substring(0, 2);
            resultLanguageCode = languages.substring(3, 5);
            // TODO: 20.03.2017 получить имена языков
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public ArrayList<String> getText() {
        return text;
    }

    public void setText(ArrayList<String> text) {
        this.text = text;
    }

    public String getOriginalLanguageCode() {
        return originalLanguageCode;
    }

    public String getOriginalLanguageName() {
        return originalLanguageName;
    }

    public String getResultLanguageCode() {
        return resultLanguageCode;
    }

    public String getResultLanguageName() {
        return resultLanguageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(languages);
        parcel.writeString(originalLanguageCode);
        parcel.writeString(originalLanguageName);
        parcel.writeString(resultLanguageCode);
        parcel.writeString(resultLanguageName);
        parcel.writeStringList(text);
    }

    public static final Parcelable.Creator<Translation> CREATOR =
            new Parcelable.Creator<Translation>() {

                public Translation createFromParcel(Parcel in) {
                    return new Translation(in);
                }

                public Translation[] newArray(int size) {
                    return new Translation[size];
                }
            };

    private Translation(Parcel parcel) {
        languages = parcel.readString();
        originalLanguageCode = parcel.readString();
        originalLanguageName = parcel.readString();
        resultLanguageCode = parcel.readString();
        resultLanguageName = parcel.readString();
        text = parcel.createStringArrayList();
    }
}
