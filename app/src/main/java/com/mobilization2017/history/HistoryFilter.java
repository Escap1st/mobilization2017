package com.mobilization2017.history;

import android.widget.Filter;

import com.mobilization2017.models.database.HistoryItemDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pelevin Igor on 09.04.2017.
 */

public class HistoryFilter extends Filter {

    private final HistoryListAdapter adapter;
    private final List<HistoryItemDB> originalList, filteredList;

    public HistoryFilter(HistoryListAdapter adapter, List<HistoryItemDB> originalList) {
        super();
        this.adapter = adapter;
        this.originalList = originalList;
        this.filteredList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();

            for (final HistoryItemDB historyItem : originalList) {
                if (historyItem.getOriginal().toLowerCase().contains(filterPattern)) {
                    filteredList.add(historyItem);
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.publishFilteredItems((List<HistoryItemDB>) results.values);
    }

    public List<HistoryItemDB> getOriginalList() {
        return originalList;
    }
}