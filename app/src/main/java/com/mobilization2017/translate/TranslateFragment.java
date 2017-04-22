package com.mobilization2017.translate;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobilization2017.R;
import com.mobilization2017.databinding.FragmentTranslationBinding;
import com.mobilization2017.databinding.LayoutDictionarySynonymBinding;
import com.mobilization2017.databinding.ToolbarTranslationBinding;
import com.mobilization2017.main.MainActivity;
import com.mobilization2017.models.database.HistoryItemDB;
import com.mobilization2017.models.database.LanguageDB;
import com.mobilization2017.models.request.DictionaryItem;
import com.mobilization2017.models.request.DictionaryRoot;
import com.mobilization2017.models.request.DictionaryValue;
import com.mobilization2017.models.request.SupportedLanguages;
import com.mobilization2017.models.request.Translation;
import com.mobilization2017.network.RequestHandler;
import com.mobilization2017.network.TranslateApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pelevin Igor on 29.03.2017.
 */

public class TranslateFragment extends Fragment {

    private final int HANDLER_WHAT = 1,
            REQ_CODE_SPEECH_INPUT = 2,
            TYPE_ORIGINAL_TEXT = 3,
            TYPE_TRANSLATION = 4;

    private final String TAG = "TranslateFragment";

    private MainActivity parentActivity;
    private FragmentTranslationBinding fragmentTranslationBinding;
    private ToolbarTranslationBinding toolbarTranslationBinding;
    private TranslateVM translateVM;
    private ChangeLanguageVM changeLanguageVM;
    private Handler translateHandler;
    private TranslateApi translateApi;
    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTranslationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_translation, container, false);
        parentActivity = (MainActivity) getActivity();
        translateVM = new TranslateVM(this, parentActivity,
                parentActivity.getSettingsPrefs().getBoolean("sync_translate", false));
        fragmentTranslationBinding.setTranslateVM(translateVM);
        fragmentTranslationBinding.setTranslateFragment(this);

        fragmentTranslationBinding.translateInputEditText.setHorizontallyScrolling(false);
        fragmentTranslationBinding.translateInputEditText.setLines(Integer.MAX_VALUE);

        translateApi = TranslateApi.getInstance(parentActivity);
        getLanguages(); // получаем или обновляем список языков

        return fragmentTranslationBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTranslateHandler();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    enterInputText(result.get(0));
                }
                break;
            }
        }
    }

    private void getLanguages() {
        List<LanguageDB> currentLanguages = LanguageDB.getAll();

        if (currentLanguages.size() <= 2) {
            LanguageDB.setDefaultLanguages(); //добавляем английский и русский
        }

        setToolbar();

        final List<LanguageDB> tempLanguageList = new ArrayList<>();
        final RequestHandler enHandler = new RequestHandler(parentActivity) {

            @Override
            public void onSuccess() {
                LanguageDB.clear();
                SupportedLanguages supportedLanguages = getMessage().getParcelable("responseResult");
                HashMap<String, String> langMap = supportedLanguages.getLanguages();
                for (int i = 0; i < tempLanguageList.size(); i++) {
                    LanguageDB tempLang = tempLanguageList.get(i);
                    tempLang.setNameEn(langMap.get(tempLang.getCode()));
                    tempLang.addToDatabase();
                }
            }

            @Override
            public void onFinish() {
                setToolbar();
            }
        };

        RequestHandler ruHandler = new RequestHandler(parentActivity) {

            @Override
            public void onSuccess() {
                SupportedLanguages supportedLanguages = getMessage().getParcelable("responseResult");
                HashMap<String, String> langMap = supportedLanguages.getLanguages();
                Object[] langCodes = langMap.keySet().toArray();
                for (int i = 0; i < langCodes.length; i++) {
                    tempLanguageList.add(new LanguageDB(langMap.get(langCodes[i].toString()),
                            langCodes[i].toString()));
                }
                translateApi.getSupportedLanguages(enHandler, "en");
            }
        };

        translateApi.getSupportedLanguages(ruHandler, "ru");
    }

    private void setToolbar() {
        toolbarTranslationBinding = fragmentTranslationBinding.translateToolbar;
        changeLanguageVM = new ChangeLanguageVM(this,
                LanguageDB.getLanguageByCode(parentActivity.getSettingsPrefs().getString("originalCode", "ru")),
                LanguageDB.getLanguageByCode(parentActivity.getSettingsPrefs().getString("destCode", "en")),
                parentActivity.getLocale());
        toolbarTranslationBinding.setChangeLanguageVM(changeLanguageVM);
        toolbarTranslationBinding.setTranslateFragment(this);
    }

    public void swapLanguages(View v) {
        changeLanguageVM.swapLanguages(true);
    }

    public void selectOriginalLanguage(View v) {
        final String locale = parentActivity.getLocale();
        final CharSequence[] items = LanguageDB.getNames(locale);
        startLanguageSelectionDialog(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int n) {
                changeLanguageVM.setOriginal(LanguageDB.getLanguageByName(items[n].toString(), locale), locale);
                translateText(getInputText(), false, true);
                d.cancel();
            }
        }, Arrays.binarySearch(items, changeLanguageVM.getOriginalFull()));
    }

    public void selectDestinationLanguage(View v) {
        final String locale = parentActivity.getLocale();
        final CharSequence[] items = LanguageDB.getNames(locale);
        startLanguageSelectionDialog(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int n) {
                changeLanguageVM.setDestination(LanguageDB.getLanguageByName(items[n].toString(), locale), locale);
                translateText(getInputText(), false, true);
                d.cancel();
            }
        }, Arrays.binarySearch(items, changeLanguageVM.getDestFull()));
    }

    public void clearInput(View v) {
        addToHistory();
        enterInputText(null);
    }

    public void startVoiceInput(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.translate_voice_input_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(parentActivity,
                    getString(R.string.error_voice_input_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void listenOriginalText(View v) {
        listenText(getInputText(), TYPE_ORIGINAL_TEXT);
    }

    public void listenTranslation(View v) {
        listenText(translateVM.getTranslation(), TYPE_TRANSLATION);
    }

    public void favoritesAdd(View v) {
        if (!translateVM.isInputEmpty()) {
            changeFavoriteState();
            translateVM.setFavorite(!translateVM.isFavorite());
        }
    }

    public void shareTranslation(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getTextToShare());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void startLanguageSelectionDialog(CharSequence[] items, DialogInterface.OnClickListener listener,
                                              int index) {
        parentActivity.hideKeyboard();
        AlertDialog.Builder adb = new AlertDialog.Builder(parentActivity);
        adb.setSingleChoiceItems(items, index, listener)
                .setNegativeButton(parentActivity.getString(R.string.translate_dialog_cancel), null)
                .setTitle(parentActivity.getString(R.string.translate_dialog_title))
                .show();
    }

    public void translateText(String text, boolean withTimeout, boolean addToHistory) {
        translateHandler.removeMessages(HANDLER_WHAT);

        if (!text.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString("text", text);
            bundle.putBoolean("addToHistory", addToHistory);
            Message message = translateHandler.obtainMessage(HANDLER_WHAT);
            message.setData(bundle);
            //задержка чтобы можно было продолжать набирать текст
            translateHandler.sendMessageDelayed(message, withTimeout ? 1000 : 0);
        } else {
            translateVM.setInputEmpty(true);
        }
    }

    private void initTranslateHandler() {
        translateHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Bundle bundle = message.getData();
                getTranslation(bundle.getString("text"), bundle.getBoolean("addToHistory"));
                return true;
            }
        });
    }

    public void getTranslation(final String text, final boolean addToHistory) {
        final RequestHandler translateHandler = new RequestHandler(parentActivity) {
            @Override
            public void onStart() {
                translateVM.setDownloading(true);
            }

            @Override
            public void onSuccess() {
                Translation translation = getMessage().getParcelable("responseResult");
                translateVM.setTranslation(translation.getText().get(0));

                translateVM.setInputEmpty(false);
                translateVM.setDownloading(false);
                translateVM.setError(false);

                if (addToHistory) {
                    addToHistory();
                }
            }

            @Override
            public void onError() {
                translateVM.setDownloading(false);
                translateVM.setErrorText(getMessage().getString("responseStatus"));
            }
        };

        RequestHandler dictionaryHandler = new RequestHandler(parentActivity) {
            @Override
            public void onStart() {
                translateVM.setDownloading(true);
            }

            @Override
            public void onSuccess() {
                clearSynonyms();
                DictionaryRoot dictionaryLookup = getMessage().getParcelable("responseResult");
                if (dictionaryLookup.getDefinition().size() != 0) {
                    parseDictionaryLookup(dictionaryLookup);

                    translateVM.setInputEmpty(false);
                    translateVM.setDownloading(false);
                    translateVM.setError(false);

                    if (addToHistory) {
                        addToHistory();
                    }
                } else {
                    translateApi.translateText(translateHandler.start(), text, changeLanguageVM.getPairShort());
                }
            }

            @Override
            public void onError() {
                translateApi.translateText(translateHandler.start(), text, changeLanguageVM.getPairShort());
            }
        };

        //если в настройках выбраны словарные статьи, сначала обращаеся к словарю
        if (parentActivity.getSettingsPrefs().getBoolean("use_dictionary", false)) {
            translateApi.lookupDictionary(dictionaryHandler.start(), text, changeLanguageVM.getPairShort());
        } else {
            translateApi.translateText(translateHandler.start(), text, changeLanguageVM.getPairShort());
        }
    }

    private void parseDictionaryLookup(DictionaryRoot dictionaryLookup) {
        LayoutInflater inflater = (LayoutInflater) parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int count = 0;
        for (int i = 0; i < dictionaryLookup.getDefinition().size(); i++) {
            DictionaryItem tempDefinition = dictionaryLookup.getDefinition().get(i);

            if (i == 0) {
                DictionaryItem tempTranslation = tempDefinition.getTranslation().get(0);
                translateVM.setDictionaryResult(tempDefinition.getText(), tempTranslation.getText(),
                        tempTranslation.getPos());
            }

            for (int j = 0; j < tempDefinition.getTranslation().size(); j++) {
                DictionaryItem tempTranslation = tempDefinition.getTranslation().get(j);
                LayoutDictionarySynonymBinding tempLayoutDictionarySynonymBinding =
                        DataBindingUtil.inflate(inflater, R.layout.layout_dictionary_synonym, null, false);
                tempLayoutDictionarySynonymBinding.setDictionarySynonymVM(new DictionarySynonymVM(
                        TranslateFragment.this, ++count,
                        joinDictionaryValues(tempTranslation.getSynonyms(), tempTranslation.getText()),
                        joinDictionaryValues(tempTranslation.getMeanings(), "").toString()));
                tempLayoutDictionarySynonymBinding.dictionarySynonymOriginal.setMovementMethod(LinkMovementMethod.getInstance());
                fragmentTranslationBinding.translateSynonymsLinearLayout.addView(tempLayoutDictionarySynonymBinding.getRoot());
            }
        }
    }

    public void listenText(final String text, final int type) {
        if (!text.isEmpty()) {
            textToSpeech = new TextToSpeech(parentActivity, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = textToSpeech.setLanguage(new Locale(type == TYPE_ORIGINAL_TEXT ?
                                changeLanguageVM.getOriginalShort() : changeLanguageVM.getDestShort()));

                        if (result == TextToSpeech.LANG_MISSING_DATA ||
                                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(parentActivity, getString(R.string.error_language_not_supported),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= 21) {
                                textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null);
                            } else {
                                textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    } else {
                        Log.e(TAG, getString(R.string.error_tts_initialization_failed));
                    }
                }
            });
        }
    }

    //получаем spannable, чтобы по клику на синоним переводить его
    private SpannableString joinDictionaryValues(final List<DictionaryValue> valueList, final String initialValue) {
        String tempString = initialValue;

        if (valueList != null) {
            for (int i = 0; i < valueList.size(); i++) {
                tempString += tempString.isEmpty() ? valueList.get(i).getText() :
                        ", " + valueList.get(i).getText();
            }
        }

        SpannableString spannableString = new SpannableString(tempString);

        if (!initialValue.isEmpty()) {
            ClickableSpan initialValueSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    changeLanguageVM.swapLanguages(false);
                    enterInputText(initialValue);
                }
            };
            spannableString.setSpan(initialValueSpan, 0, initialValue.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (valueList != null) {
                int offset = initialValue.length() + 2;
                for (int i = 0; i < valueList.size(); i++) {
                    final String value = valueList.get(i).getText();
                    ClickableSpan tempSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            changeLanguageVM.swapLanguages(false);
                            enterInputText(value);
                        }
                    };

                    spannableString.setSpan(tempSpan, tempString.indexOf(value, offset),
                            tempString.indexOf(value, offset) + value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    offset += value.length() + 2;
                }
            }
        }

        return spannableString;
    }

    public void clearSynonyms() {
        fragmentTranslationBinding.translateSynonymsLinearLayout.removeAllViews();
    }

    public void invertTranslation() {
        if (!TextUtils.isEmpty(translateVM.getTranslation()) && !translateVM.isInputEmpty()) {
            enterInputText(translateVM.getTranslation());
        }
    }

    public void addToHistory() {
        String text = getInputText();
        if (!text.isEmpty() && !TextUtils.isEmpty(translateVM.getTranslation()) && !translateVM.isDownloading()) {
            HistoryItemDB newItem = new HistoryItemDB(text, translateVM.getTranslation(),
                    changeLanguageVM.getPairShort().toUpperCase(), false);

            translateVM.setFavorite(newItem.addToHistory());
            parentActivity.refreshHistoryList();
        }
    }

    public void changeFavoriteState() {
        HistoryItemDB.changeFavoriteState(getInputText(), changeLanguageVM.getPairShort().toUpperCase());
        parentActivity.refreshHistoryList();
    }

    public void changeInputMode(boolean isSync) {
        translateVM.setSyncMode(isSync);
    }

    public String getInputText() {
        return fragmentTranslationBinding.translateInputEditText.getText().toString();
    }

    public void enterInputText(String text) {
        fragmentTranslationBinding.translateInputEditText.setText(text);
    }

    private String getTextToShare() {
        String result = (!translateVM.getOriginal().isEmpty() ? translateVM.getOriginal() :
                getInputText()) + "\n\n" + translateVM.getTranslation();

        if (!TextUtils.isEmpty(translateVM.getPos())) {
            result += " (" + translateVM.getPos() + ")";
        }

        for (int i = 0; i < fragmentTranslationBinding.translateSynonymsLinearLayout.getChildCount(); i++) {
            LayoutDictionarySynonymBinding tempBinding = DataBindingUtil
                    .findBinding(fragmentTranslationBinding.translateSynonymsLinearLayout.getChildAt(i));
            DictionarySynonymVM dictionarySynonymVM = tempBinding.getDictionarySynonymVM();
            result += "\n\n" + dictionarySynonymVM.getNumber() + ". " + dictionarySynonymVM.getOriginal();

            if (!TextUtils.isEmpty(dictionarySynonymVM.getTranslate())) {
                result += "\n" + dictionarySynonymVM.getTranslate();
            }
        }

        return result;
    }

    public ChangeLanguageVM getChangeLanguageVM() {
        return changeLanguageVM;
    }
}
