package com.mobilization2017.models.database;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Pelevin Igor on 05.04.2017.
 */

public class HistoryItemDB extends RealmObject {

    private static volatile Realm realm;

    private long time;
    private String original;
    private String translation;
    private String languages;
    private boolean isFavorite;

    public HistoryItemDB() {

    }

    public HistoryItemDB(String original, String translation, String languages, boolean isFavorite) {
        this.time = new Date().getTime();
        this.original = original;
        this.translation = translation;
        this.languages = languages;
        this.isFavorite = isFavorite;
    }

    private static Realm getRealm() {
        synchronized (Realm.class) {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
        }
        return realm;
    }

    private boolean isAlreadyInHistory() {
        RealmResults<HistoryItemDB> historyResults = currentItemQuery(original, languages).findAll();
        return historyResults.size() > 0;
    }

    public static boolean isAlreadyInHistory(String original, String languages) {
        RealmResults<HistoryItemDB> historyResults = currentItemQuery(original, languages).findAll();
        return historyResults.size() > 0;
    }

    public static void changeFavoriteState(final String original, final String languages) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                HistoryItemDB itemToChange = currentItemQuery(original, languages).findFirst();
                itemToChange.setFavorite(!itemToChange.isFavorite());
            }
        });
    }

    public static boolean isAlreadyInFavorites(String original, String languages) {
        HistoryItemDB itemToChange = currentItemQuery(original, languages).findFirst();
        return itemToChange.isFavorite();
    }

    private RealmQuery<HistoryItemDB> currentItemQuery() {
        return getRealm().where(HistoryItemDB.class)
                .equalTo("original", original)
                .equalTo("languages", languages);
    }

    private static RealmQuery<HistoryItemDB> currentItemQuery(String original, String languages) {
        return getRealm().where(HistoryItemDB.class)
                .equalTo("original", original)
                .equalTo("languages", languages);
    }

    public static List<HistoryItemDB> getAll() {
        return getRealm().copyFromRealm(getRealm().where(HistoryItemDB.class)
                .findAll()
                .sort("time", Sort.DESCENDING));
    }

    public static List<HistoryItemDB> getFavorites() {
        return getRealm().copyFromRealm(getRealm().where(HistoryItemDB.class)
                .equalTo("isFavorite", true)
                .findAll()
                .sort("time", Sort.DESCENDING));
    }

    public boolean addToHistory() {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (!isAlreadyInHistory()) {
                    realm.copyToRealm(HistoryItemDB.this);
                } else {
                    HistoryItemDB itemToChange = currentItemQuery().findFirst();
                    itemToChange.setTime(new Date().getTime());
                    isFavorite = itemToChange.isFavorite;
                }
            }
        });
        return isFavorite;
    }

    public void delete() {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                currentItemQuery().findAll().deleteAllFromRealm();
            }
        });
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
