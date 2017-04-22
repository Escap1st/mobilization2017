package com.mobilization2017.history;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.android.databinding.library.baseAdapters.BR;
import com.mobilization2017.models.database.HistoryItemDB;

/**
 * Created by Pelevin Igor on 27.03.2017.
 */

public class HistoryItemVM extends BaseObservable {

    private HistoryFragment historyFragment;

    private String originalText;
    private String translatedText;
    private String languages;
    private boolean isFavorite;
    private boolean isSelected;

    public HistoryItemVM(HistoryFragment historyFragment, String originalText, String translatedText,
                         String languages, boolean isFavorite, boolean isSelected) {
        this.historyFragment = historyFragment;
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.languages = languages;
        this.isFavorite = isFavorite;
        this.isSelected = isSelected;
    }

    public void favoritesClick(@NonNull final View v) {
        HistoryItemDB.changeFavoriteState(originalText, languages);
        setFavorite(!isFavorite);

        historyFragment.refreshAdapter();
    }

    @BindingAdapter("bind:colorTint")
    public static void setColorTint(ImageView view, int color) {
        view.setColorFilter(color);
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    @Bindable
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
        notifyPropertyChanged(BR.favorite);
    }

    @Bindable
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.selected);
    }
}
