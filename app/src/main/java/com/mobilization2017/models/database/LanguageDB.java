package com.mobilization2017.models.database;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;

/**
 * Created by Pelevin Igor on 15.04.2017.
 */

public class LanguageDB extends RealmObject {

    private static volatile Realm realm;

    private String nameRu;
    private String nameEn;
    private String code;

    public LanguageDB(String nameRu, String code) {
        this.nameRu = nameRu;
        this.code = code;
    }

    public LanguageDB(String nameRu, String nameEn, String code) {
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.code = code;
    }

    public LanguageDB() {

    }

    public static List<LanguageDB> getAll() {
        return getRealm().copyFromRealm(getRealm().where(LanguageDB.class).findAll());
    }

    public static CharSequence[] getNames(String locale) {
        List<LanguageDB> tempLanguages = getRealm()
                .copyFromRealm(getRealm()
                        .where(LanguageDB.class)
                        .findAll()
                        .sort(locale.equals("ru") ? "nameRu" : "nameEn", Sort.ASCENDING));
        CharSequence[] result = new CharSequence[tempLanguages.size()];

        for (int i = 0; i < tempLanguages.size(); i++) {
            result[i] = locale.equals("ru") ? tempLanguages.get(i).getNameRu() :
                    tempLanguages.get(i).getNameEn();
        }

        return result;
    }

    public static LanguageDB getLanguageByName(String name, String locale) {
        return getRealm().copyFromRealm(getRealm().where(LanguageDB.class)
                .equalTo(locale.equals("ru") ? "nameRu" : "nameEn", name)
                .findFirst());
    }

    public static LanguageDB getLanguageByCode(String code) {
        return getRealm().copyFromRealm(getRealm().where(LanguageDB.class)
                .equalTo("code", code)
                .findFirst());
    }

    private RealmQuery<LanguageDB> currentItemQuery() {
        return getRealm().where(LanguageDB.class)
                .equalTo("code", code);
    }

    public void delete() {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                currentItemQuery().findAll().deleteAllFromRealm();
            }
        });
    }

    public static void clear() {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getRealm().where(LanguageDB.class)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public void addToDatabase() {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(LanguageDB.this);
            }
        });
    }

    private static Realm getRealm() {
        synchronized (Realm.class) {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
        }
        return realm;
    }

    public static void setDefaultLanguages() {
        LanguageDB.clear();
        new LanguageDB("Русский", "Russian", "ru").addToDatabase();
        new LanguageDB("Английский", "English", "en").addToDatabase();
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
