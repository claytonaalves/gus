package com.kaora.anunciosapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.models.PublicationCategory;
import com.kaora.anunciosapp.models.Preference;
import com.kaora.anunciosapp.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static MyDatabaseHelper instance = null;

    public static final String DATABASE_NAME = "publicationsdb";

    private static final int DATABASE_VERSION = 1;

    private static final String CATEGORY_TABLE = "" +
            "CREATE TABLE publication_category ( " +
            "category_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "city_id INTEGER NOT NULL, " +
            "name TEXT NOT NULL, " +
            "advertiser_count INTEGER, " +
            "image_file TEXT)";

    private static final String PREFERENCES_TABLE = "" +
            "CREATE TABLE preference ( " +
            "category_id INTEGER, " +
            "description TEXT NOT NULL, " +
            "updated INTEGER)";

    private static final String PUBLICATIONS_TABLE = "" +
            "CREATE TABLE publication ( " +
            "publication_guid TEXT NOT NULL PRIMARY KEY, " +
            "advertiser_guid TEXT NOT NULL, " +
            "category_id INTEGER NOT NULL, " +
            "title TEXT, " +
            "description TEXT, " +
            "publication_date INTEGER NOT NULL, " +
            "due_date INTEGER NOT NULL, " +
            "image_file TEXT, " +
            "archived INTEGER NOT NULL DEFAULT 0, " +
            "published INTEGER )";

    // Armazena os perfis de advertiser criados no aparelho
    private static final String ADVERTISER_TABLE = "" +
            "CREATE TABLE advertiser ( " +
            "advertiser_guid TEXT NOT NULL PRIMARY KEY, " +
            "trading_name TEXT, " +
            "phone_number TEXT, " +
            "cellphone TEXT, " +
            "email TEXT, " +
            "street_name TEXT, " +
            "address_number TEXT, " +
            "neighbourhood TEXT, " +
            "city_id INTEGER, " +
            "category_id INTEGER, " +
            "published INTEGER )";

    private MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized MyDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MyDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CATEGORY_TABLE);
        db.execSQL(PREFERENCES_TABLE);
        db.execSQL(ADVERTISER_TABLE);
        db.execSQL(PUBLICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // ========================================================================
    // Publication Categories
    // ========================================================================

    public void updateCategories(List<PublicationCategory> publicationCategories) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM publication_category");
        for (PublicationCategory publicationCategory : publicationCategories) {
            ContentValues values = new ContentValues();
            values.put("category_id", publicationCategory.idCategoria);
            values.put("name", publicationCategory.descricao);
            values.put("advertiser_count", publicationCategory.qtdeAnunciantes);
            values.put("image_file", publicationCategory.imagem);
            database.insert("publication_category", null, values);
        }
    }

    // ========================================================================
    // Advertiser Profiles
    // ========================================================================

    public void saveAdvertiserProfile(Advertiser advertiser) {
        int rowsAffected = updateAdvertiserProfile(advertiser);
        if (rowsAffected == 0) {
            insertAdvertiserProfile(advertiser);
        }
    }

    private int updateAdvertiserProfile(Advertiser advertiser) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = createAdvertiserProfileContentValues(advertiser);
        return db.update("advertiser", values, "advertiser_guid=?", new String[] {advertiser.advertiserGuid});
    }

    private void insertAdvertiserProfile(Advertiser advertiser) {
        advertiser.advertiserGuid = UUID.randomUUID().toString();
        ContentValues values = createAdvertiserProfileContentValues(advertiser);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("advertiser", null, values);
    }

    @NonNull
    private ContentValues createAdvertiserProfileContentValues(Advertiser advertiserProfile) {
        ContentValues values = new ContentValues();
        values.put("advertiser_guid", advertiserProfile.advertiserGuid);
        values.put("trading_name", advertiserProfile.tradingName);
        values.put("phone_number", advertiserProfile.phoneNumber);
        values.put("cellphone", advertiserProfile.cellphone);
        values.put("email", advertiserProfile.email);
        values.put("street_name", advertiserProfile.streetName);
        values.put("address_number", advertiserProfile.addressNumber);
        values.put("neighbourhood", advertiserProfile.neighbourhood);
        values.put("city_id", advertiserProfile.cityId);
        values.put("category_id", advertiserProfile.categoryId);
        values.put("published", (advertiserProfile.published ? 1 : 0));
        return values;
    }

    public List<Advertiser> allProfiles() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM advertiser", null);
        List<Advertiser> perfis = new ArrayList<>();
        while (cursor.moveToNext()) {
            Advertiser perfil = new Advertiser();
            perfil.advertiserGuid = cursor.getString(cursor.getColumnIndex("advertiser_guid"));
            perfil.tradingName = cursor.getString(cursor.getColumnIndex("trading_name"));
            perfis.add(perfil);
        }
        cursor.close();
        return perfis;
    }

    public Advertiser getProfileByGuid(String advertiserGuid) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT advertiser_guid, trading_name, category_id " +
                                    "FROM advertiser " +
                                    "WHERE advertiser_guid='" + advertiserGuid + "'", null);
        cursor.moveToNext();
        Advertiser advertiser = new Advertiser();
        advertiser.advertiserGuid = cursor.getString(0);
        advertiser.tradingName = cursor.getString(1);
        advertiser.categoryId = cursor.getInt(2);
        cursor.close();
        return advertiser;
    }

    // ========================================================================
    // Preferences
    // ========================================================================

    public void salvaPreferencia(Preference preference) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM preference WHERE category_id=" + preference.categoryId);
        if (preference.selected) {
            ContentValues values = new ContentValues();
            values.put("category_id", preference.categoryId);
            values.put("description", preference.descricao);
            values.put("updated", 0);
            database.insert("preference", null, values);
        }
    }

    public boolean getPreferences() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM preference", null);
        cursor.moveToFirst();
        boolean result = cursor.getInt(0) > 0;
        cursor.close();
        return result;
    }

    public List<Preference> getPreferencesByCity(int idCidade) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT category_id, description FROM preference", null);
        List<Preference> preferences = new ArrayList<>();
        while (cursor.moveToNext()) {
            Preference preference = new Preference(cursor.getInt(0), cursor.getString(1));
            preference.selected = true;
            preferences.add(preference);
        }
        cursor.close();
        return preferences;
    }

    public List<Preference> getOutdatedPreferences() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT category_id, description FROM preference WHERE updated=0", null);
        List<Preference> preferences = new ArrayList<>();
        while (cursor.moveToNext()) {
            Preference preference = new Preference(cursor.getInt(0), cursor.getString(1));
            preference.selected = true;
            preferences.add(preference);
        }
        cursor.close();
        return preferences;
    }

    public void marcaPreferenciasComoAtualizadas() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE preference SET updated=1");
    }

    public List<Preference> getSelectedPreferences() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT category_id, description FROM preference", null);
        List<Preference> preferences = new ArrayList<>();
        while (cursor.moveToNext()) {
            Preference preference = new Preference(cursor.getInt(0), cursor.getString(1));
            preference.selected = true;
            preferences.add(preference);
        }
        cursor.close();
        return preferences;
    }

    // ========================================================================
    // Publications
    // ========================================================================

    public void savePublications(List<Publication> publications) {
        for (Publication publication : publications)
            savePublication(publication);
    }

    public void savePublication(Publication publication) {
        if (publication.publicationGuid.equals("")) {
            publication.publicationGuid = UUID.randomUUID().toString();
        }
        int rowsAffected = updatePublication(publication);
        if (rowsAffected == 0) {
            insertPublication(publication);
        }
    }

    private void insertPublication(Publication publication) {
        ContentValues values = createPublicationContentValues(publication);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("publication", null, values);
    }

    private int updatePublication(Publication publication) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = createPublicationContentValues(publication);
        return db.update("publication", values, "publication_guid=?", new String[] {publication.publicationGuid});
    }

    private ContentValues createPublicationContentValues(Publication publication) {
        ContentValues values = new ContentValues();
        values.put("publication_guid", publication.publicationGuid);
        values.put("advertiser_guid", publication.advertiserGuid);
        values.put("title", publication.title);
        values.put("description", publication.description);
        values.put("category_id", publication.category_id);
        values.put("publication_date", DateUtils.dateToString(publication.publicationDate));
        values.put("due_date", DateUtils.dateToString(publication.dueDate));
        values.put("image_file", publication.imageFile);
        values.put("published", (publication.published ? 1 : 0));
        values.put("archived", (publication.archived ? 1 : 0));
        return values;
    }

    // Retorna a lista de publicações não arquivadas
    public List<Publication> getSavedPublications() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM publication WHERE archived=0 ORDER BY publication_date", null);
        List<Publication> publications = new ArrayList<>();
        while (cursor.moveToNext()) {
            Publication publication = getPublicationFromCursor(cursor);
            publications.add(publication);
        }
        cursor.close();
        return publications;
    }

//    public Publication obtemPublicacao(String guidPublicacao) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM publicacao WHERE guid_publicacao='" + guidPublicacao + "'", null);
//        cursor.moveToNext();
//        return getPublicationFromCursor(cursor);
//    }

    @NonNull
    private Publication getPublicationFromCursor(Cursor cursor) {
        Publication publication = new Publication();
        publication.publicationGuid = cursor.getString(cursor.getColumnIndex("publication_guid"));
        publication.advertiserGuid = cursor.getString(cursor.getColumnIndex("advertiser_guid"));
        publication.category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
        publication.title = cursor.getString(cursor.getColumnIndex("title"));
        publication.description = cursor.getString(cursor.getColumnIndex("description"));
        publication.publicationDate = DateUtils.stringToDate(cursor.getString(cursor.getColumnIndex("publication_date")));
        publication.dueDate = DateUtils.stringToDate(cursor.getString(cursor.getColumnIndex("due_date")));
        publication.imageFile = cursor.getString(cursor.getColumnIndex("image_file"));
        return publication;
    }

    public void removeOverduePublications() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM publication WHERE due_date<'" + DateUtils.dateToString(new Date()) + "'");
    }
}
