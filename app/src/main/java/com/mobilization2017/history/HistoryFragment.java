package com.mobilization2017.history;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mobilization2017.R;
import com.mobilization2017.databinding.FragmentHistoryBinding;
import com.mobilization2017.databinding.ToolbarHistoryBinding;
import com.mobilization2017.main.MainActivity;
import com.mobilization2017.models.database.HistoryItemDB;

/**
 * Created by Pelevin Igor on 29.03.2017.
 */

public class HistoryFragment extends Fragment {

    private final String TAG = "TranslateFragment";
    public final int TYPE_HISTORY = 0,
            TYPE_FAVORITES = 1;

    private MainActivity parentActivity;
    private FragmentHistoryBinding fragmentHistoryBinding;
    private ToolbarHistoryBinding toolbarHistoryBinding;
    private HistoryVM historyVM;
    private HistoryListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentHistoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        historyVM = new HistoryVM(this);
        fragmentHistoryBinding.setHistoryVM(historyVM);

        toolbarHistoryBinding = fragmentHistoryBinding.historyToolbar;
        toolbarHistoryBinding.setHistoryFragment(this);

        parentActivity = (MainActivity) getActivity();
        parentActivity.setSupportActionBar((Toolbar) toolbarHistoryBinding.getRoot());
        setHasOptionsMenu(true); //единственный фрагмент с меню, поэтому можно прописать это в onCreate

        return fragmentHistoryBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mAdapter.showDeleteItemsDialog(); //переписать если добавятся действия
        return super.onOptionsItemSelected(item);
    }

    public void initRecyclerView() {
        fragmentHistoryBinding.historyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        fragmentHistoryBinding.historyRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                fragmentHistoryBinding.historyRecyclerView.getContext(),
                layoutManager.getOrientation());
        fragmentHistoryBinding.historyRecyclerView.addItemDecoration(dividerItemDecoration);

        setRecyclerView(TYPE_HISTORY);
    }

    //смена типа на историю/избранное
    public void setRecyclerView(int type) {
        historyVM.setHistory(type == TYPE_HISTORY);
        mAdapter = new HistoryListAdapter(this, type == TYPE_HISTORY ?
                HistoryItemDB.getAll() : HistoryItemDB.getFavorites(),
                fragmentHistoryBinding.historySearchView.getQuery().toString());
        fragmentHistoryBinding.historyRecyclerView.setAdapter(mAdapter);
    }

    public void refreshAdapter() {
        mAdapter.changeItems(historyVM.isHistory() ? HistoryItemDB.getAll() : HistoryItemDB.getFavorites(),
                fragmentHistoryBinding.historySearchView.getQuery().toString());
    }

    //поиск
    public void filterList(String text) {
        mAdapter.getFilter().filter(text);
    }

    //очистка выбранных лонг тапом элементов
    public boolean clearListSelections() {
        if (mAdapter.getSelectedItemsCount() != 0) {
            mAdapter.clearSelectedItems();
            return true;
        }

        return false;
    }

    public void setListEmpty(boolean listEmpty) {
        historyVM.setListEmpty(listEmpty);
    }

    public TabLayout.OnTabSelectedListener getOnTabSelectedListener() {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setRecyclerView(tab.getPosition() == 0 ? TYPE_HISTORY : TYPE_FAVORITES);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    public MainActivity getParentActivity() {
        return parentActivity;
    }
}
