package com.mobilization2017.models.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

/**
 * Created by Pelevin Igor on 20.03.2017.
 */

@JsonObject
public class PossibleLanguage implements Parcelable {

    @JsonField(name = "lang")
    private String languageCode;

    private String languageName;

    public PossibleLanguage() {

    }

    @OnJsonParseComplete
    public void onParseComplete() {
        // TODO: 20.03.2017 получить имя языка
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(languageCode);
        parcel.writeString(languageName);
    }

    public static final Parcelable.Creator<PossibleLanguage> CREATOR =
            new Parcelable.Creator<PossibleLanguage>() {

                public PossibleLanguage createFromParcel(Parcel in) {
                    return new PossibleLanguage(in);
                }

                public PossibleLanguage[] newArray(int size) {
                    return new PossibleLanguage[size];
                }
            };

    private PossibleLanguage(Parcel parcel) {
        languageCode = parcel.readString();
        languageName = parcel.readString();
    }
}
