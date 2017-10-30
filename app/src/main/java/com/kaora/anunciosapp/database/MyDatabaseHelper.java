package com.kaora.anunciosapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.PublicationCategory;
import com.kaora.anunciosapp.models.Preferencia;
import com.kaora.anunciosapp.models.Publicacao;
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

    private static final String PREFERENCES_TABLE = "" +
            "CREATE TABLE preferencia ( " +
            "id_categoria INTEGER, " +
            "descricao TEXT NOT NULL, " +
            "atualizada INTEGER)";

    private static final String ADVERTISER_TABLE = "" +
            "CREATE TABLE anunciante ( " +
            "guid_anunciante TEXT NOT NULL PRIMARY KEY, " +
            "nome_fantasia TEXT, " +
            "telefone TEXT, " +
            "endereco TEXT, " +
            "id_categoria INTEGER)";

    // Armazena os perfis de anunciante criados no aparelho
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

    private static final String PUBLICATIONS_TABLE = "" +
            "CREATE TABLE publicacao ( " +
            "guid_publicacao TEXT NOT NULL PRIMARY KEY, " +
            "guid_anunciante TEXT NOT NULL, " +
            "id_categoria INTEGER NOT NULL, " +
            "titulo TEXT, " +
            "descricao TEXT, " +
            "data_publicacao INTEGER NOT NULL, " +
            "data_validade INTEGER NOT NULL, " +
            "imagem TEXT, " +
            "arquivado INTEGER NOT NULL DEFAULT 0, " +
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
            values.put("descricao", preferencia.descricao);
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
        Cursor cursor = db.rawQuery("SELECT id_categoria, descricao FROM preferencia", null);
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
        Cursor cursor = db.rawQuery("SELECT id_categoria, descricao FROM preferencia WHERE atualizada=0", null);
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
        Cursor cursor = db.rawQuery("SELECT id_categoria, descricao FROM preferencia", null);
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

    public void savePublications(List<Publicacao> publicacoes) {
        for (Publicacao publicacao : publicacoes)
            savePublication(publicacao);
    }

    public void savePublication(Publicacao publication) {
        if (publication.guidPublicacao.equals("")) {
            publication.guidPublicacao = UUID.randomUUID().toString();
        }
        int rowsAffected = updatePublication(publication);
        if (rowsAffected == 0) {
            insertPublication(publication);
        }
    }

    private void insertPublication(Publicacao publication) {
        publication.guidPublicacao = UUID.randomUUID().toString();
        ContentValues values = createPublicationContentValues(publication);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("publicacao", null, values);
    }

    private int updatePublication(Publicacao publication) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = createPublicationContentValues(publication);
        return db.update("publicacao", values, "guid_publicacao=?", new String[] {publication.guidPublicacao});
    }

    private ContentValues createPublicationContentValues(Publicacao publication) {
        ContentValues values = new ContentValues();
        values.put("guid_publicacao", publication.guidPublicacao);
        values.put("guid_anunciante", publication.guidAnunciante);
        values.put("titulo", publication.titulo);
        values.put("descricao", publication.descricao);
        values.put("id_categoria", publication.idCategoria);
        values.put("data_publicacao", DateUtils.dateToString(publication.dataPublicacao));
        values.put("data_validade", DateUtils.dateToString(publication.dataValidade));
        values.put("imagem", publication.imagem);
        values.put("publicado", (publication.published ? 1 : 0));
        return values;
    }

    // Retorna a lista de publicações não arquivadas
    public List<Publicacao> publicacoesSalvas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM publicacao WHERE arquivado=0 ORDER BY data_publicacao", null);
        List<Publicacao> publicacoes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Publicacao publicacao = extraiPublicacaoDoCursor(cursor);
            publicacoes.add(publicacao);
        }
        cursor.close();
        return publicacoes;
    }

    public void arquivaPublicacao(Publicacao publicacao) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE publicacao SET arquivado=1 WHERE guid_publicacao='" + publicacao.guidPublicacao + "'");
    }

    public Publicacao obtemPublicacao(String guidPublicacao) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM publicacao WHERE guid_publicacao='" + guidPublicacao + "'", null);
        cursor.moveToNext();
        return extraiPublicacaoDoCursor(cursor);
    }

    @NonNull
    private Publicacao extraiPublicacaoDoCursor(Cursor cursor) {
        Publicacao publicacao = new Publicacao();
        publicacao.guidPublicacao = cursor.getString(cursor.getColumnIndex("guid_publicacao"));
        publicacao.guidAnunciante = cursor.getString(cursor.getColumnIndex("guid_anunciante"));
        publicacao.idCategoria = cursor.getInt(cursor.getColumnIndex("id_categoria"));
        publicacao.titulo = cursor.getString(cursor.getColumnIndex("titulo"));
        publicacao.descricao = cursor.getString(cursor.getColumnIndex("descricao"));
        publicacao.dataPublicacao = DateUtils.stringToDate(cursor.getString(cursor.getColumnIndex("data_publicacao")));
        publicacao.dataValidade = DateUtils.stringToDate(cursor.getString(cursor.getColumnIndex("data_validade")));
        publicacao.imagem = cursor.getString(cursor.getColumnIndex("imagem"));
        return publicacao;
    }

    public void removePublicacoesVencidas() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM publicacao WHERE data_validade<'" + DateUtils.dateToString(new Date()) + "'");
    }
}
