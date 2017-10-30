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
import com.kaora.anunciosapp.models.Preferencia;
import com.kaora.anunciosapp.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static MyDatabaseHelper instance = null;

    public static final String DATABASE_NAME = "anuncios_database";

    private static final int DATABASE_VERSION = 1;

    private static final String CATEGORY_TABLE = "" +
            "CREATE TABLE publication_category ( " +
            "category_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "city_id INTEGER NOT NULL, " +
            "name TEXT NOT NULL, " +
            "advertiser_count INTEGER, " +
            "image_file TEXT)";

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

    private static final String PREFERENCES_TABLE = "" +
            "CREATE TABLE preferencia ( " +
            "id_categoria INTEGER, " +
            "description TEXT NOT NULL, " +
            "atualizada INTEGER)";

    private static final String ADVERTISER_TABLE = "" +
            "CREATE TABLE advertiser ( " +
            "guid_anunciante TEXT NOT NULL PRIMARY KEY, " +
            "nome_fantasia TEXT, " +
            "telefone TEXT, " +
            "endereco TEXT, " +
            "id_categoria INTEGER)";

    // Armazena os perfis de advertiser criados no aparelho
    private static final String ADVERTISER_PROFILE_TABLE = "" +
            "CREATE TABLE perfil_anunciante ( " +
            "guid_anunciante TEXT NOT NULL PRIMARY KEY, " +
            "nome_fantasia TEXT, " +
            "telefone TEXT, " +
            "celular TEXT, " +
            "email TEXT, " +
            "logradouro TEXT, " +
            "numero TEXT, " +
            "bairro TEXT, " +
            "id_cidade INTEGER, " +
            "id_categoria INTEGER, " +
            "publicado INTEGER )";

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
        db.execSQL(ADVERTISER_PROFILE_TABLE);
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

    public void saveAdvertiserProfile(Advertiser advertiserProfile) {
        int rowsAffected = updateAdvertiserProfile(advertiserProfile);
        if (rowsAffected == 0) {
            insertAdvertiserProfile(advertiserProfile);
        }
    }

    private int updateAdvertiserProfile(Advertiser advertiserProfile) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = createAdvertiserProfileContentValues(advertiserProfile);
        return db.update("perfil_anunciante", values, "guid_anunciante=?", new String[] {advertiserProfile.guidAnunciante});
    }

    private void insertAdvertiserProfile(Advertiser advertiserProfile) {
        advertiserProfile.guidAnunciante = UUID.randomUUID().toString();
        ContentValues values = createAdvertiserProfileContentValues(advertiserProfile);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("perfil_anunciante", null, values);
    }

    @NonNull
    private ContentValues createAdvertiserProfileContentValues(Advertiser advertiserProfile) {
        ContentValues values = new ContentValues();
        values.put("guid_anunciante", advertiserProfile.guidAnunciante);
        values.put("nome_fantasia", advertiserProfile.nomeFantasia);
        values.put("telefone", advertiserProfile.telefone);
        values.put("celular", advertiserProfile.celular);
        values.put("email", advertiserProfile.email);
        values.put("logradouro", advertiserProfile.logradouro);
        values.put("numero", advertiserProfile.numero);
        values.put("bairro", advertiserProfile.bairro);
        values.put("id_cidade", advertiserProfile.idCidade);
        values.put("id_categoria", advertiserProfile.idCategoria);
        values.put("publicado", (advertiserProfile.published ? 1 : 0));
        return values;
    }

    public List<Advertiser> allProfiles() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM perfil_anunciante", null);
        List<Advertiser> perfis = new ArrayList<>();
        while (cursor.moveToNext()) {
            Advertiser perfil = new Advertiser();
            perfil.guidAnunciante = cursor.getString(cursor.getColumnIndex("guid_anunciante"));
            perfil.nomeFantasia = cursor.getString(cursor.getColumnIndex("nome_fantasia"));
            perfis.add(perfil);
        }
        cursor.close();
        return perfis;
    }

    public Advertiser getProfileByGuid(String guidAnunciante) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT guid_anunciante, nome_fantasia, id_categoria FROM perfil_anunciante WHERE guid_anunciante='" + guidAnunciante + "'", null);
        cursor.moveToNext();
        Advertiser perfil = new Advertiser();
        perfil.guidAnunciante = cursor.getString(0);
        perfil.nomeFantasia = cursor.getString(1);
        perfil.idCategoria = cursor.getInt(2);
        cursor.close();
        return perfil;
    }

    // ========================================================================
    // Preferences
    // ========================================================================

    public void salvaPreferencia(Preferencia preferencia) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM preferencia WHERE id_categoria=" + preferencia.idCategoria);
        if (preferencia.selecionanda) {
            ContentValues values = new ContentValues();
            values.put("id_categoria", preferencia.idCategoria);
            values.put("description", preferencia.descricao);
            values.put("atualizada", 0);
            database.insert("preferencia", null, values);
        }
    }

    public boolean preferenciasDefinidas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM preferencia", null);
        cursor.moveToFirst();
        boolean result = cursor.getInt(0) > 0;
        cursor.close();
        return result;
    }

    public List<Preferencia> preferenciasSelecionadasPorCidade(int idCidade) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_categoria, description FROM preferencia", null);
        List<Preferencia> preferencias = new ArrayList<>();
        while (cursor.moveToNext()) {
            Preferencia preferencia = new Preferencia(cursor.getInt(0), cursor.getString(1));
            preferencia.selecionanda = true;
            preferencias.add(preferencia);
        }
        cursor.close();
        return preferencias;
    }

    public List<Preferencia> preferenciasDesatualizadas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_categoria, description FROM preferencia WHERE atualizada=0", null);
        List<Preferencia> preferencias = new ArrayList<>();
        while (cursor.moveToNext()) {
            Preferencia preferencia = new Preferencia(cursor.getInt(0), cursor.getString(1));
            preferencia.selecionanda = true;
            preferencias.add(preferencia);
        }
        cursor.close();
        return preferencias;
    }

    public void marcaPreferenciasComoAtualizadas() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE preferencia SET atualizada=1");
    }

    public List<Preferencia> peferenciasSelecionadas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_categoria, description FROM preferencia", null);
        List<Preferencia> preferencias = new ArrayList<>();
        while (cursor.moveToNext()) {
            Preferencia preferencia = new Preferencia(cursor.getInt(0), cursor.getString(1));
            preferencia.selecionanda = true;
            preferencias.add(preferencia);
        }
        cursor.close();
        return preferencias;
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
