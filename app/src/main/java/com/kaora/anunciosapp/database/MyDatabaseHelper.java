package com.kaora.anunciosapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.Preference;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.models.PublicationCategory;
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
            "description TEXT NOT NULL)";

    private static final String PUBLICATIONS_TABLE = "" +
            "CREATE TABLE publication ( " +
            "publication_guid TEXT NOT NULL PRIMARY KEY, " +
            "advertiser_guid TEXT NOT NULL, " +
            "category_id INTEGER NOT NULL, " +
            "title TEXT, " +
            "description TEXT, " +
            "publication_date INTEGER NOT NULL, " +
            "due_date INTEGER NOT NULL, " +
            "archived INTEGER NOT NULL DEFAULT 0, " +
            "published INTEGER )";

    private static final String PUBLICATION_IMAGES_TABLE = "" +
            "CREATE TABLE publication_image ( " +
            "image_guid TEXT, " +
            "publication_guid TEXT, " +
            "filename TEXT) ";

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
            "image_file TEXT, " +
            "published INTEGER, " +
            "local_profile INTEGER)";

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
        db.execSQL(PUBLICATION_IMAGES_TABLE);
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
    // Advertisers
    // ========================================================================

    public void saveAdvertiser(Advertiser advertiser) {
        advertiser.localProfile = advertiserExists(advertiser);
        int rowsAffected = updateAdvertiser(advertiser);
        if (rowsAffected == 0) {
            insertAdvertiser(advertiser);
        }
    }

    private boolean advertiserExists(Advertiser advertiser) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM advertiser " +
                        "WHERE advertiser_guid='" + advertiser.advertiserGuid + "'", null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    private int updateAdvertiser(Advertiser advertiser) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = createAdvertiserContentValues(advertiser);
        return db.update("advertiser", values, "advertiser_guid=?", new String[]{advertiser.advertiserGuid});
    }

    private void insertAdvertiser(Advertiser advertiser) {
        ContentValues values = createAdvertiserContentValues(advertiser);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("advertiser", null, values);
    }

    @NonNull
    private ContentValues createAdvertiserContentValues(Advertiser advertiserProfile) {
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
        values.put("image_file", advertiserProfile.imageFile);
        values.put("published", (advertiserProfile.published ? 1 : 0));
        values.put("local_profile", (advertiserProfile.localProfile ? 1 : 0));
        return values;
    }

    public List<Advertiser> allAdvertisers() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM advertiser", null);
        List<Advertiser> profiles = new ArrayList<>();
        while (cursor.moveToNext()) {
            Advertiser profile = extractAdvertiserFromCursor(cursor);
            profiles.add(profile);
        }
        cursor.close();
        return profiles;
    }

    public List<Advertiser> allLocalProfiles() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM advertiser WHERE local_profile=1", null);
        List<Advertiser> profiles = new ArrayList<>();
        while (cursor.moveToNext()) {
            Advertiser profile = extractAdvertiserFromCursor(cursor);
            profiles.add(profile);
        }
        cursor.close();
        return profiles;
    }

    public Advertiser getAdvertiserByGuid(String advertiserGuid) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM advertiser " +
                "WHERE advertiser_guid='" + advertiserGuid + "'", null);
        cursor.moveToNext();
        Advertiser advertiser = extractAdvertiserFromCursor(cursor);
        cursor.close();
        return advertiser;
    }

    @NonNull
    private Advertiser extractAdvertiserFromCursor(Cursor cursor) {
        Advertiser advertiser = new Advertiser();
        advertiser.advertiserGuid = cursor.getString(cursor.getColumnIndex("advertiser_guid"));
        advertiser.categoryId = cursor.getInt(cursor.getColumnIndex("category_id"));
        advertiser.tradingName = cursor.getString(cursor.getColumnIndex("trading_name"));
        advertiser.phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));
        advertiser.cellphone = cursor.getString(cursor.getColumnIndex("cellphone"));
        advertiser.email = cursor.getString(cursor.getColumnIndex("email"));
        advertiser.streetName = cursor.getString(cursor.getColumnIndex("street_name"));
        advertiser.addressNumber = cursor.getString(cursor.getColumnIndex("address_number"));
        advertiser.neighbourhood = cursor.getString(cursor.getColumnIndex("neighbourhood"));
        advertiser.cityId = cursor.getInt(cursor.getColumnIndex("city_id"));
        advertiser.imageFile = cursor.getString(cursor.getColumnIndex("image_file"));
        advertiser.localProfile = (cursor.getInt(cursor.getColumnIndex("local_profile")) == 1);
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
        SQLiteDatabase db = getWritableDatabase();

        // First, save the advertiser data
        saveAdvertiser(publication.advertiser);

        int rowsAffected = updatePublication(publication, db);
        if (rowsAffected == 0) {
            insertPublication(publication, db);
        }
        updatePublicationImages(publication, db);
    }

    private void insertPublication(Publication publication, SQLiteDatabase db) {
        ContentValues values = createPublicationContentValues(publication);
        db.insert("publication", null, values);
    }

    private int updatePublication(Publication publication, SQLiteDatabase db) {
        ContentValues values = createPublicationContentValues(publication);
        return db.update("publication", values, "publication_guid=?", new String[]{publication.publicationGuid});
    }

    private void updatePublicationImages(Publication publication, SQLiteDatabase db) {
        // Remove old images
        db.execSQL("DELETE FROM publication_image WHERE publication_guid='" + publication.publicationGuid + "'");

        if (!publication.hasImages()) return;

        // Re-insert images
        for (String filename : publication.images) {
            ContentValues values = new ContentValues();
            values.put("image_guid", UUID.randomUUID().toString());
            values.put("publication_guid", publication.publicationGuid);
            values.put("filename", filename);
            db.insert("publication_image", null, values);
        }
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
        values.put("published", (publication.published ? 1 : 0));
        values.put("archived", (publication.archived ? 1 : 0));
        return values;
    }

    // Returns a list if non archived publications
    public List<Publication> getSavedPublications() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM publication WHERE archived=0 ORDER BY publication_date", null);
        List<Publication> publications = new ArrayList<>();
        while (cursor.moveToNext()) {
            Publication publication = loadPublicationFromCursor(cursor);
            publication.advertiser = getAdvertiserByGuid(publication.advertiserGuid);
            loadImagesForPublication(publication, db);
            publications.add(publication);
        }
        cursor.close();
        return publications;
    }

    @NonNull
    private Publication loadPublicationFromCursor(Cursor cursor) {
        Publication publication = new Publication();
        publication.publicationGuid = cursor.getString(cursor.getColumnIndex("publication_guid"));
        publication.advertiserGuid = cursor.getString(cursor.getColumnIndex("advertiser_guid"));
        publication.category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
        publication.title = cursor.getString(cursor.getColumnIndex("title"));
        publication.description = cursor.getString(cursor.getColumnIndex("description"));
        publication.publicationDate = DateUtils.stringToDate(cursor.getString(cursor.getColumnIndex("publication_date")));
        publication.dueDate = DateUtils.stringToDate(cursor.getString(cursor.getColumnIndex("due_date")));
        return publication;
    }

    private void loadImagesForPublication(Publication publication, SQLiteDatabase db) {
        Cursor cursor = db.query("publication_image",
                new String[]{"filename"},
                "publication_guid=?",
                new String[]{publication.publicationGuid},
                null, null, null, null);
        while (cursor.moveToNext()) {
            publication.images.add(cursor.getString(0));
        }
        cursor.close();
    }

    public void removeOverduePublications() {
        SQLiteDatabase db = getWritableDatabase();
        String date = DateUtils.dateToString(DateUtils.subtractDayFromDate(new Date(), 1));
        db.execSQL("DELETE FROM publication WHERE due_date<'" + date + "'");
    }
}
