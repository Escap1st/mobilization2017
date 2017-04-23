package com.mobilization2017.translate;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;
import com.mobilization2017.main.MainActivity;

/**
 * Created by Pelevin Igor on 28.03.2017.
 */
public class TranslateVM extends BaseObservable {

    private MainActivity mainActivity;
    private TranslateFragment translateFragment;

    private boolean isInputEmpty;
    private boolean isDownloading;
    private boolean isError;
    private boolean isFavorite;
    private boolean isSyncMode;
    private String translation;
    private String original;
    private String pos;
    private String errorText;

    public TranslateVM(TranslateFragment translateFragment, MainActivity mainActivity,
                       boolean isSyncMode) {
        this.translateFragment = translateFragment;
        this.mainActivity = mainActivity;
        this.isSyncMode = isSyncMode;
        this.isInputEmpty = true;
        this.isDownloading = false;
        this.isFavorite = false;
        this.isError = false;
    }

    public TextView.OnEditorActionListener getOnEditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    translateFragment.translateText(v.getText().toString(), false, true);
                    mainActivity.hideKeyboard();
                    return true;
                }

                return false;
            }
        };
    }

    public TextWatcher getInputTextWatcher() {
        return new TextWatcher() {
            private String initialState;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                initialState = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                boolean checkDiffWithoutTrim = isShortDiff(charSequence.toString(), start, before, count);
                if (isSyncMode || TextUtils.isEmpty(charSequence) || !checkDiffWithoutTrim) {
                    String currentState = charSequence.toString().trim();
                    if (isShortDiff(currentState, start, before, count)) {
                        translateFragment.translateText(currentState, true, false);
                    } else {
                        translateFragment.translateText(currentState, false, !checkDiffWithoutTrim);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            //вроде бы неплохая попытка проверки на то, что изменение сделано на 1 символ и вручную
            private boolean isShortDiff(String currentState, int start, int before, int count) {
                if (Math.abs(count - before) == 1) {
                    return currentState.length() > initialState.length() ?
                            checkShortDiff(currentState, initialState, start) :
                            checkShortDiff(initialState, currentState, start);
                }

                return false;
            }

            private boolean checkShortDiff(String biggerString, String smallerString, int start) {
                String prefix = biggerString.substring(0, start);
                String edited = biggerString.substring(start, biggerString.length());
                return !edited.isEmpty() && ((prefix + deleteCharAtPosition(edited, 0)).equals(smallerString) ||
                        (prefix + deleteCharAtPosition(edited, edited.length() - 1)).equals(smallerString));
            }

            private String deleteCharAtPosition(String text, int place) {
                StringBuilder sb = new StringBuilder(text);
                String result = sb.deleteCharAt(place).toString();
                return result;
            }
        };
    }

    @BindingAdapter("bind:colorTint")
    public static void setColorTint(ImageView view, int color) {
        view.setColorFilter(color);
    }

    public void setDictionaryResult(String original, String translation, String pos) {
        this.original = original;
        this.translation = translation;
        this.pos = pos;
        notifyChange();
    }

    @Bindable
    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
        notifyPropertyChanged(BR.downloading);
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
        this.pos = "";
        this.original = "";
        translateFragment.clearSynonyms();
        notifyChange();
    }

    @Bindable
    public boolean isInputEmpty() {
        return isInputEmpty;
    }

    public void setInputEmpty(boolean inputEmpty) {
        isInputEmpty = inputEmpty;
        notifyPropertyChanged(BR.inputEmpty);
    }

    @Bindable
    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
        notifyPropertyChanged(BR.original);
    }

    @Bindable
    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
        notifyPropertyChanged(BR.pos);
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
    public boolean isSyncMode() {
        return isSyncMode;
    }

    public void setSyncMode(boolean syncMode) {
        isSyncMode = syncMode;
        notifyPropertyChanged(BR.syncMode);
    }

    @Bindable
    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
        notifyPropertyChanged(BR.error);
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
        isError = !TextUtils.isEmpty(errorText);
        notifyChange();
    }
}
