package com.mobilization2017.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mobilization2017.R;
import com.mobilization2017.databinding.ActivityMainBinding;
import com.mobilization2017.history.HistoryFragment;
import com.mobilization2017.models.database.LanguageDB;
import com.mobilization2017.settings.SettingsFragment;
import com.mobilization2017.translate.TranslateFragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pelevin Igor on 29.03.2017.
 */

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public final int PAGE_TRANSLATE = 0,
            PAGE_HISTORY = 1,
            PAGE_SETTINGS = 2;

    private ActivityMainBinding activityMainBinding;

    private TranslateFragment translateFragment;
    private HistoryFragment historyFragment;
    private SettingsFragment settingsFragment;

    private SharedPreferences settingsPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setMainActivity(this);

        initializePager();

        //при сокрытии кравиатуры добавляем перевод в историю, мб не самое изящное решение
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (!isOpen) {
                    try {
                        translateFragment.addToHistory();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        if (getIntent().hasExtra("currentFragment")) {
            activityMainBinding.mainViewPager.setCurrentItem(getIntent().getIntExtra("currentFragment", PAGE_SETTINGS));
            setBottomMenuSelectedItem(getIntent().getIntExtra("currentFragment", PAGE_SETTINGS));
        }

        getSettingsPrefs().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES &&
                activityMainBinding.mainViewPager.getCurrentItem() == PAGE_TRANSLATE) {
            translateFragment.addToHistory();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals("sync_translate")) {
            translateFragment.changeInputMode(prefs.getBoolean(key, true));
        }

        if (key.equals("language")) {
            setLocale();
        }
    }

    @Override
    public void onBackPressed() {
        if (activityMainBinding.mainViewPager.getCurrentItem() != PAGE_HISTORY ||
                !historyFragment.clearListSelections()) {
            super.onBackPressed();
        }
    }

    public void initializePager() {
        translateFragment = new TranslateFragment();
        historyFragment = new HistoryFragment();
        settingsFragment = new SettingsFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(translateFragment);
        adapter.addFragment(historyFragment);
        adapter.addFragment(settingsFragment);
        activityMainBinding.mainViewPager.setAdapter(adapter);

        activityMainBinding.mainViewPager.setOffscreenPageLimit(2); //чтобы всегда были прогружены
    }

    public ViewPager.OnPageChangeListener getPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideKeyboard(); //прячем при скролле, чтобы не мешалась
            }

            @Override
            public void onPageSelected(int position) {
                setBottomMenuSelectedItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    public BottomNavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setPage(item.getItemId());
                return true;
            }
        };
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    public void setBottomMenuSelectedItem(int position) {
        switch (position) {
            case PAGE_TRANSLATE:
                activityMainBinding.mainBottomNavigationView.setSelectedItemId(R.id.translate_item);
                break;

            case PAGE_HISTORY:
                activityMainBinding.mainBottomNavigationView.setSelectedItemId(R.id.history_item);
                break;

            case PAGE_SETTINGS:
                activityMainBinding.mainBottomNavigationView.setSelectedItemId(R.id.settings_item);
                break;
        }
    }

    public void setPage(int itemId) {
        switch (itemId) {
            case R.id.translate_item:
                activityMainBinding.mainViewPager.setCurrentItem(PAGE_TRANSLATE);
                break;

            case R.id.history_item:
                activityMainBinding.mainViewPager.setCurrentItem(PAGE_HISTORY);
                break;

            case R.id.settings_item:
                activityMainBinding.mainViewPager.setCurrentItem(PAGE_SETTINGS);
                break;
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void translateText(String text, String languages) {
        activityMainBinding.mainViewPager.setCurrentItem(PAGE_TRANSLATE);
        translateFragment.enterInputText(text);

        //устанавливаем языки для переода из истории
        translateFragment.getChangeLanguageVM()
                .setDestination(LanguageDB.getLanguageByCode(languages.substring(3).toLowerCase()),
                        getLocale());
        translateFragment.getChangeLanguageVM()
                .setOriginal(LanguageDB.getLanguageByCode(languages.substring(0, 2).toLowerCase()),
                        getLocale());
    }

    public void refreshHistoryList() {
        historyFragment.refreshAdapter();
    }

    public SharedPreferences getSettingsPrefs() {
        if (settingsPrefs == null) {
            settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        }
        return settingsPrefs;
    }

    public String getLocale() {
        return getSettingsPrefs().getString("language", "ru");
    }

    //изменяем язык и перезапускаем активити, в будущем добавить сохранение данных, сейчас теряются
    private void setLocale() {
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        String languageCode = getLocale();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(languageCode));
        } else {
            conf.locale = new Locale(languageCode);
        }
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        refresh.putExtra("currentFragment", activityMainBinding.mainViewPager.getCurrentItem());
        startActivity(refresh);
        MainActivity.this.finish();
    }

}
