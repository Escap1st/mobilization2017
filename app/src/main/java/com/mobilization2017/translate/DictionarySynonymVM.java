package com.mobilization2017.translate;

import android.databinding.BaseObservable;
import android.text.SpannableString;

/**
 * Created by Pelevin Igor on 02.04.2017.
 */

public class DictionarySynonymVM extends BaseObservable {

    private TranslateFragment translateFragment;

    private String number;
    private SpannableString original;
    private String translate;

    public DictionarySynonymVM(TranslateFragment translateFragment, int number, SpannableString original, String translate) {
        this.translateFragment = translateFragment;
        this.number = String.valueOf(number);
        this.original = original;
        this.translate = translate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public SpannableString getOriginal() {
        return original;
    }

    public void setOriginal(SpannableString original) {
        this.original = original;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
