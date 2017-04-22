package com.mobilization2017.translate;

import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.support.v7.preference.PreferenceManager;

import com.mobilization2017.models.database.LanguageDB;

/**
 * Created by Pelevin Igor on 04.04.2017.
 */

public class ChangeLanguageVM extends BaseObservable {

    private TranslateFragment translateFragment;

    private String originalFull;
    private String originalShort;
    private String destFull;
    private String destShort;

    public ChangeLanguageVM(TranslateFragment translateFragment, LanguageDB original,
                            LanguageDB destination, String currentLocale) {
        this.translateFragment = translateFragment;
        originalShort = original.getCode();
        destShort = destination.getCode();
        originalFull = currentLocale.equals("ru") ? original.getNameRu() : original.getNameEn();
        destFull = currentLocale.equals("ru") ? destination.getNameRu() : destination.getNameEn();
    }

    public void setOriginal(LanguageDB original, String currentLocale){
        originalShort = original.getCode();
        originalFull = currentLocale.equals("ru") ? original.getNameRu() : original.getNameEn();
        notifyChange();

        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString("originalCode", originalShort);
        editor.apply();
    }

    public void setDestination(LanguageDB destination, String currentLocale){
        destShort = destination.getCode();
        destFull = currentLocale.equals("ru") ? destination.getNameRu() : destination.getNameEn();
        notifyChange();

        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString("destCode", destShort);
        editor.apply();
    }

    public void swapLanguages(boolean withInverse) {
        originalFull = returnFirst(destFull, destFull = originalFull);
        originalShort = returnFirst(destShort, destShort = originalShort);
        notifyChange();

        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString("destCode", destShort);
        editor.putString("originalCode", originalShort);
        editor.apply();

        if (withInverse) {
            translateFragment.invertTranslation();
        }
    }

    private String returnFirst(String s1, String s2) {
        return s1;
    }

    public String getOriginalFull() {
        return originalFull;
    }

    public void setOriginalFull(String originalFull) {
        this.originalFull = originalFull;
    }

    public String getOriginalShort() {
        return originalShort;
    }

    public void setOriginalShort(String originalShort) {
        this.originalShort = originalShort;
    }

    public String getDestFull() {
        return destFull;
    }

    public void setDestFull(String destFull) {
        this.destFull = destFull;
    }

    public String getDestShort() {
        return destShort;
    }

    public void setDestShort(String destShort) {
        this.destShort = destShort;
    }

    public String getPairShort() {
        return originalShort + "-" + destShort;
    }

    private SharedPreferences getPrefs(){
        return PreferenceManager.getDefaultSharedPreferences(translateFragment.getActivity());
    }
}
