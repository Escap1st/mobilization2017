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
public class DictionaryItem implements Parcelable {

    @JsonField
    private String text;

    @JsonField
    private String pos;

    @JsonField(name = "tr")
    private List<DictionaryItem> translation;

    @JsonField(name = "syn")
    private List<DictionaryValue> synonyms;

    @JsonField(name = "mean")
    private List<DictionaryValue> meanings;

    @JsonField(name = "ex")
    private List<DictionaryValue> examples;

    public DictionaryItem() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public List<DictionaryValue> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<DictionaryValue> synonyms) {
        this.synonyms = synonyms;
    }

    public List<DictionaryValue> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<DictionaryValue> meanings) {
        this.meanings = meanings;
    }

    public List<DictionaryValue> getExamples() {
        return examples;
    }

    public void setExamples(List<DictionaryValue> examples) {
        this.examples = examples;
    }

    public List<DictionaryItem> getTranslation() {
        return translation;
    }

    public void setTranslation(List<DictionaryItem> translation) {
        this.translation = translation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeString(pos);
        parcel.writeTypedList(translation);
        parcel.writeTypedList(synonyms);
        parcel.writeTypedList(meanings);
        parcel.writeTypedList(examples);
    }

    public static final Parcelable.Creator<DictionaryItem> CREATOR =
            new Parcelable.Creator<DictionaryItem>() {

                public DictionaryItem createFromParcel(Parcel in) {
                    return new DictionaryItem(in);
                }

                public DictionaryItem[] newArray(int size) {
                    return new DictionaryItem[size];
                }
            };

    private DictionaryItem(Parcel parcel) {
        text = parcel.readString();
        pos = parcel.readString();
        parcel.readTypedList(translation, DictionaryItem.CREATOR);
        parcel.readTypedList(synonyms, DictionaryValue.CREATOR);
        parcel.readTypedList(meanings, DictionaryValue.CREATOR);
        parcel.readTypedList(examples, DictionaryValue.CREATOR);
    }
}
