package com.mobilization2017.history;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.mobilization2017.R;
import com.mobilization2017.databinding.ListItemHistoryBinding;
import com.mobilization2017.models.database.HistoryItemDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pelevin Igor on 09.04.2017.
 */

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryItemViewHolder>
        implements Filterable {

    private HistoryFragment historyFragment;
    private List<HistoryItemDB> filteredHistoryItems = new ArrayList<>();
    private List<Integer> selectedItems = new ArrayList<>();
    private HistoryFilter historyFilter;

    public HistoryListAdapter(HistoryFragment historyFragment, List<HistoryItemDB> historyItems,
                              String filterText) {
        this.historyFragment = historyFragment;
        initFilter(historyItems).filter(filterText);
    }

    //замена итемов при изменении на фрагменте перевода
    public void changeItems(List<HistoryItemDB> historyItems, String filterText) {
        if (!((HistoryFilter) getFilter()).getOriginalList().equals(historyItems)) {
            initFilter(historyItems).filter(filterText);
        }
    }

    //отображение отфильтрованных элементов
    public void publishFilteredItems(List<HistoryItemDB> historyItems) {
        this.selectedItems.clear();
        this.filteredHistoryItems.clear();
        this.filteredHistoryItems.addAll(historyItems);
        historyFragment.setListEmpty(historyItems.size() == 0);
        notifyDataSetChanged();
    }

    public class HistoryItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        ListItemHistoryBinding binding;

        public HistoryItemViewHolder(ListItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().setOnLongClickListener(this);
        }

        public void bind(HistoryItemVM historyItemVM) {
            binding.setHistoryItemVM(historyItemVM);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            boolean isSelected = binding.getHistoryItemVM().isSelected();

            if (selectedItems.size() == 0) {
                HistoryItemDB tempItem = filteredHistoryItems.get(getPosition());
                historyFragment.getParentActivity().translateText(tempItem.getOriginal(),
                        tempItem.getLanguages()); //переходим на экран перевода
            } else {
                if (isSelected) {
                    selectedItems.remove((Object) getPosition());
                } else {
                    selectedItems.add(getPosition());
                }
                binding.getHistoryItemVM().setSelected(!isSelected);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            boolean isSelected = binding.getHistoryItemVM().isSelected();
            if (selectedItems.size() == 0 && !isSelected) {
                selectedItems.add(getPosition());
                binding.getHistoryItemVM().setSelected(true);
                return true;
            }

            return false;
        }
    }

    @Override
    public int getItemCount() {
        return filteredHistoryItems.size();
    }

    @Override
    public HistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemHistoryBinding binding = ListItemHistoryBinding.inflate(inflater, parent, false);
        return new HistoryItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(HistoryItemViewHolder holder, int position) {
        HistoryItemDB tempHistoryItemDB = filteredHistoryItems.get(position);
        HistoryItemVM tempHistoryItemVM = new HistoryItemVM(historyFragment,
                tempHistoryItemDB.getOriginal(), tempHistoryItemDB.getTranslation(),
                tempHistoryItemDB.getLanguages(), tempHistoryItemDB.isFavorite(),
                selectedItems.contains(position));
        holder.bind(tempHistoryItemVM);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Filter getFilter() {
        return historyFilter;
    }

    private Filter initFilter(List<HistoryItemDB> historyItems) {
        return historyFilter = new HistoryFilter(this, historyItems);
    }

    public void showDeleteItemsDialog() {
        if (filteredHistoryItems.size() != 0) {
            final boolean deleteAll = selectedItems.size() == 0;
            Context context = historyFragment.getActivity();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(historyFragment.getActivity());
            dialog.setMessage(context.getString(deleteAll ? R.string.history_question_delete_all :
                    R.string.history_question_delete_selected))
                    .setNegativeButton(context.getString(R.string.history_dialog_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                    .setPositiveButton(context.getString(R.string.history_dialog_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteItemsFromDatabase(deleteAll);
                                    dialog.cancel();
                                }
                            })
                    .create()
                    .show();
        }
    }

    private void deleteItemsFromDatabase(boolean deleteAll) {
        for (int i = 0; i < filteredHistoryItems.size(); i++) {
            if (deleteAll || selectedItems.contains(i)) {
                filteredHistoryItems.get(i).delete();
            }
        }

        historyFragment.refreshAdapter();
    }

    public int getSelectedItemsCount(){
        return selectedItems.size();
    }

    public void clearSelectedItems(){
        selectedItems.clear();
        notifyDataSetChanged();
    }
}