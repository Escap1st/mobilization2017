package com.mobilization2017.models.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by Pelevin Igor on 02.04.2017.
 */

@JsonObject
public class DictionaryValue implements Parcelable {

    @JsonField
    private String text;

    @JsonField(name = "tr")
    private List<DictionaryValue> translation;

    public DictionaryValue() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<DictionaryValue> getTranslation() {
        return translation;
    }

    public void setTranslation(List<DictionaryValue> translation) {
        this.translation = translation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeTypedList(translation);
    }

    public static final Parcelable.Creator<DictionaryValue> CREATOR =
            new Parcelable.Creator<DictionaryValue>() {

                public DictionaryValue createFromParcel(Parcel in) {
                    return new DictionaryValue(in);
                }

                public DictionaryValue[] newArray(int size) {
                    return new DictionaryValue[size];
                }
            };

    private DictionaryValue(Parcel parcel) {
        text = parcel.readString();
        parcel.readTypedList(translation, DictionaryValue.CREATOR);
    }
}
