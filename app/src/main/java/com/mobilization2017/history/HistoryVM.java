package com.mobilization2017.history;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;

import com.mobilization2017.BR;
import com.mobilization2017.R;

/**
 * Created by Pelevin Igor on 08.04.2017.
 */

public class HistoryVM extends BaseObservable {

    private HistoryFragment historyFragment;

    private boolean isHistory;
    private boolean isListEmpty;
    private boolean isSearchEmpty;
    private String errorText;

    public HistoryVM(HistoryFragment historyFragment) {
        this.historyFragment = historyFragment;
        this.isHistory = true;
        this.isSearchEmpty = true;
        this.isListEmpty = true;
    }

    public void onSearchViewClick(View v) {
        SearchView searchView = (SearchView) v;
        if (searchView.isIconified()) {
            searchView.setIconified(false);
        }
    }

    public SearchView.OnQueryTextListener getOnQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                historyFragment.filterList(newText);
                setSearchEmpty(TextUtils.isEmpty(newText));
                return true;
            }
        };
    }

    @Bindable
    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
        notifyPropertyChanged(BR.history);
    }

    @Bindable
    public boolean isListEmpty() {
        return isListEmpty;
    }

    public void setListEmpty(boolean listEmpty) {
        isListEmpty = listEmpty;
        Context context = historyFragment.getActivity();
        if (isSearchEmpty) {
            errorText = isHistory ? context.getString(R.string.history_empty) :
                    context.getString(R.string.favorites_empty);
        } else {
            errorText = context.getString(R.string.history_unsuccesfull_search);
        }

        notifyChange();
    }

    @Bindable
    public boolean isSearchEmpty() {
        return isSearchEmpty;
    }

    public void setSearchEmpty(boolean searchEmpty) {
        isSearchEmpty = searchEmpty;
        notifyPropertyChanged(BR.searchEmpty);
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
