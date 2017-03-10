package com.kaora.anunciosapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kaora.anunciosapp.models.Categoria;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static MyDatabaseHelper instance = null;
    private SQLiteDatabase database;

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
            values.put("_id", categoria.idCategoria);
            values.put("descricao", categoria.descricao);
            database.insert("categoria", null, values);
        }
    }

    public void salvaPreferencias(List<Categoria> categorias) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM preferencia");
        for (Categoria categoria : categorias) {
            ContentValues values = new ContentValues();
            values.put("idcategoria", categoria.idCategoria);
            database.insert("preferencia", null, values);
        }
    }

    public List<Categoria> categoriasPreferidas() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = new String[]{"_id", "descricao"};
        Cursor cursor = db.query("categoria", projection, null, null, null, null, null);
        List<Categoria> todas = new ArrayList<>();
        while (cursor.moveToNext()) {
            Categoria categoria = new Categoria(cursor.getInt(0), cursor.getString(1));
            todas.add(categoria);
        }
        cursor.close();
        return todas;
    }

}
