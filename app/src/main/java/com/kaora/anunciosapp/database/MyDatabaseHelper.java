package com.kaora.anunciosapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kaora.anunciosapp.models.Anunciante;
import com.kaora.anunciosapp.models.Categoria;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

//    private static MyDatabaseHelper instance = null;
//    private SQLiteDatabase database;

    public static final String DATABASE_NAME = "anuncios_database";

    private static final int DATABASE_VERSION = 1;

    private static final String TABELA_CATEGORIA = "" +
            "CREATE TABLE categoria ( " +
            "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "descricao TEXT NOT NULL, " +
            "imagem TEXT)";

    private static final String TABELA_PREFERENCIA = "" +
            "CREATE TABLE preferencia ( " +
            "idcategoria INTEGER, " +
            "FOREIGN KEY (idcategoria) REFERENCES categoria(_id))";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    public MyDatabaseHelper getInstance(Context context) {
//        if (instance == null) {
//            instance = new MyDatabaseHelper(context);
//        }
//        return instance;
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABELA_CATEGORIA);
        db.execSQL(TABELA_PREFERENCIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void atualizaCategorias(List<Categoria> categorias) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM categoria");
        for (Categoria categoria : categorias) {
            ContentValues values = new ContentValues();
            values.put("_id", categoria._id);
            values.put("descricao", categoria.descricao);
            database.insert("categoria", null, values);
        }
    }

    public void salvaPreferencias(List<Categoria> categorias) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM preferencia");
        for (Categoria categoria : categorias) {
            ContentValues values = new ContentValues();
            values.put("idcategoria", categoria._id);
            database.insert("preferencia", null, values);
        }
    }

    public List<Categoria> categoriasPreferidas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT c._id, c.descricao FROM categoria c JOIN preferencia p ON (c._id=p.idcategoria)", null);
        List<Categoria> categorias = new ArrayList<>();
        while (cursor.moveToNext()) {
            categorias.add(new Categoria(cursor.getInt(0), cursor.getString(1)));
        }
        cursor.close();
        return categorias;
    }

    public List<Categoria> todasCategorias() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categoria", null);
        List<Categoria> categorias = new ArrayList<>();
        while (cursor.moveToNext()) {
            categorias.add(new Categoria(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex("descricao"))));
        }
        cursor.close();
        return categorias;
    }

    public List<Anunciante> anunciantesPorCategoria(int idCategoria) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.query("anunciante")
        return null;
    }
}
