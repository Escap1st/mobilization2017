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
public class DictionaryRoot implements Parcelable {

    @JsonField(name = "def")
    private List<DictionaryItem> definition;

    public DictionaryRoot() {

    }

    public List<DictionaryItem> getDefinition() {
        return definition;
    }

    public void setDefinition(List<DictionaryItem> definition) {
        this.definition = definition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(definition);
    }

    public static final Parcelable.Creator<DictionaryRoot> CREATOR =
            new Parcelable.Creator<DictionaryRoot>() {

                public DictionaryRoot createFromParcel(Parcel in) {
                    return new DictionaryRoot(in);
                }

                public DictionaryRoot[] newArray(int size) {
                    return new DictionaryRoot[size];
                }
            };

    private DictionaryRoot(Parcel parcel) {
        parcel.readTypedList(definition, DictionaryItem.CREATOR);
    }
}
