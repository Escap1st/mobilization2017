package com.mobilization2017.models.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pelevin Igor on 20.03.2017.
 */

@JsonObject
public class SupportedLanguages implements Parcelable {

    @JsonField(name = "langs")
    private HashMap<String, String> languages;

    public SupportedLanguages() {

    }

    public HashMap<String, String> getLanguages() {
        return languages;
    }

    public void setLanguages(HashMap<String, String> languages) {
        this.languages = languages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(languages.size());
        for (Map.Entry<String, String> entry : languages.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
    }

    public static final Parcelable.Creator<SupportedLanguages> CREATOR =
            new Parcelable.Creator<SupportedLanguages>() {

                public SupportedLanguages createFromParcel(Parcel in) {
                    return new SupportedLanguages(in);
                }

                public SupportedLanguages[] newArray(int size) {
                    return new SupportedLanguages[size];
                }
            };

    private SupportedLanguages(Parcel parcel) {
        languages = new HashMap<>();
        int size = parcel.readInt();
        for (int i = 0; i < size; i++) {
            String key = parcel.readString();
            String value = parcel.readString();
            languages.put(key, value);
        }
    }
}
