package com.kaora.anunciosapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kaora.anunciosapp.models.Anunciante;
import com.kaora.anunciosapp.models.Anuncio;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.models.Preferencia;
import com.kaora.anunciosapp.models.Publicacao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static MyDatabaseHelper instance = null;

    public static final String DATABASE_NAME = "anuncios_database";

    private static final int DATABASE_VERSION = 1;

    private static final String TABELA_CATEGORIA = "" +
            "CREATE TABLE categoria ( " +
            "id_categoria INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "id_cidade INTEGER NOT NULL, " +
            "descricao TEXT NOT NULL, " +
            "qtde_anunciantes INTEGER, " +
            "imagem TEXT)";

    private static final String TABELA_PREFERENCIA = "" +
            "CREATE TABLE preferencia ( " +
            "id_categoria INTEGER, " +
            "descricao TEXT NOT NULL)";

    private static final String TABELA_ANUNCIANTE = "" +
            "CREATE TABLE anunciante ( " +
            "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "nome_fantasia TEXT, " +
            "telefone TEXT, " +
            "endereco TEXT, " +
            "id_categoria INTEGER)";

    private static final String TABELA_PERFIL_ANUNCIANTE = "" +
            "CREATE TABLE perfil_anunciante ( " +
            "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "guid TEXT, " +
            "nome TEXT, " +
            "telefone TEXT, " +
            "celular TEXT, " +
            "email TEXT, " +
            "endereco TEXT, " +
            "numero TEXT, " +
            "estado TEXT, " +
            "cidade TEXT, " +
            "bairro TEXT, " +
            "id_categoria INTEGER, " +
            "publicado INTEGER )";

    private static final String TABELA_PUBLICACOES = "" +
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
        db.execSQL(TABELA_CATEGORIA);
        db.execSQL(TABELA_PREFERENCIA);
        db.execSQL(TABELA_ANUNCIANTE);
        db.execSQL(TABELA_PERFIL_ANUNCIANTE);
        db.execSQL(TABELA_PUBLICACOES);
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
            values.put("qtde_anunciantes", categoria.qtdeAnunciantes);
            values.put("imagem", categoria.imagem);
            database.insert("categoria", null, values);
        }
    }

    public List<Categoria> todasCategorias() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categoria", null);
        List<Categoria> categorias = new ArrayList<>();
        while (cursor.moveToNext()) {
            categorias.add(
                    new Categoria(
                            cursor.getInt(cursor.getColumnIndex("_id")),
                            cursor.getString(cursor.getColumnIndex("descricao")),
                            cursor.getInt(cursor.getColumnIndex("qtde_anunciantes")),
                            cursor.getString(cursor.getColumnIndex("imagem"))
                    )
            );
        }
        cursor.close();
        return categorias;
    }

    public void salvaAnuncio(Anuncio anuncio) {
        anuncio.guid = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put("guid", anuncio.guid);
        values.put("titulo", anuncio.titulo);
        values.put("descricao", anuncio.descricao);
        values.put("id_categoria", anuncio.idCategoria);
        values.put("valido_ate", anuncio.dataFormatada());
        SQLiteDatabase db = getWritableDatabase();
        anuncio._id = db.insert("anuncio", null, values);
    }

    public void marcaAnuncioComoPublicado(long idAnuncio) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE anuncio SET publicado=1 WHERE _id=" + idAnuncio);
    }

    // ========================================================================
    // Perfis
    // ========================================================================

    public void salvaPerfil(PerfilAnunciante perfil) {
        perfil.guid = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put("guid", perfil.guid);
        values.put("nome", perfil.nome);
        values.put("telefone", perfil.telefone);
        values.put("celular", perfil.celular);
        values.put("email", perfil.email);
        values.put("endereco", perfil.endereco);
        values.put("numero", perfil.numero);
        values.put("bairro", perfil.bairro);
        values.put("cidade", perfil.cidade);
        values.put("estado", perfil.estado);
        values.put("id_categoria", perfil.idCategoria);
        SQLiteDatabase db = getWritableDatabase();
        perfil._id = db.insert("perfil_anunciante", null, values);
    }

    public List<PerfilAnunciante> todosPerfis() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM perfil_anunciante", null);
        List<PerfilAnunciante> perfis = new ArrayList<>();
        while (cursor.moveToNext()) {
            PerfilAnunciante perfil = new PerfilAnunciante();
            perfil._id = cursor.getInt(cursor.getColumnIndex("_id"));
            perfil.nome = cursor.getString(cursor.getColumnIndex("nome"));
//            public String telefone;
//            public String celular;
//            public String email;
//            public String endereco;
//            public String estado;
//            public String cidade;
//            public String bairro;
//            public int idcategoria;
            perfis.add(perfil);
        }
        cursor.close();
        return perfis;
    }

    public PerfilAnunciante selecionaPerfil() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, guid FROM perfil_anunciante LIMIT 1", null);
        cursor.moveToNext();
        PerfilAnunciante perfil = new PerfilAnunciante();
        perfil._id = cursor.getInt(0);
        perfil.guid = cursor.getString(1);
        return perfil;
    }

    public PerfilAnunciante selecionaPerfil(long idPerfil) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, nome, id_categoria, guid FROM perfil_anunciante WHERE _id=" + idPerfil, null);
        cursor.moveToNext();
        PerfilAnunciante perfil = new PerfilAnunciante();
        perfil._id = cursor.getInt(0);
        perfil.nome = cursor.getString(1);
        perfil.idCategoria = cursor.getLong(2);
        perfil.guid = cursor.getString(3);
        return perfil;
    }

    public void marcaPerfilComoPublicado(long idPerfil) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE perfil_anunciante SET publicado=1 WHERE _id=" + idPerfil);
    }

    // ========================================================================
    // Preferências
    // ========================================================================

    public void salvaPreferencia(Preferencia preferencia) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM preferencia WHERE id_categoria=" + preferencia.idCategoria);
        if (preferencia.selecionanda) {
            ContentValues values = new ContentValues();
            values.put("id_categoria", preferencia.idCategoria);
            values.put("descricao", preferencia.descricao);
            database.insert("preferencia", null, values);
        }
    }

    public boolean preferenciasDefinidas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM preferencia", null);
        cursor.moveToFirst();
        return cursor.getInt(0) > 0;
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
    // Publicações
    // ========================================================================

    // Retorna a lista de publicações não arquivadas
    public List<Publicacao> publicacoesSalvas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM publicacao WHERE arquivado=0", null);
        List<Publicacao> publicacoes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Publicacao publicacao = new Publicacao();
            publicacao.guidPublicacao = cursor.getString(cursor.getColumnIndex("guid_publicacao"));
            publicacao.guidAnunciante = cursor.getString(cursor.getColumnIndex("guid_anunciante"));
            publicacao.idCategoria = cursor.getInt(cursor.getColumnIndex("id_categoria"));
            publicacao.titulo = cursor.getString(cursor.getColumnIndex("titulo"));
            publicacao.descricao = cursor.getString(cursor.getColumnIndex("descricao"));
            publicacao.dataPublicacao = cursor.getLong(cursor.getColumnIndex("data_publicacao"));
            publicacao.dataValidade = cursor.getLong(cursor.getColumnIndex("data_validade"));
            publicacao.imagem = cursor.getString(cursor.getColumnIndex("imagem"));
            publicacoes.add(publicacao);
        }
        cursor.close();
        return publicacoes;
    }

    public void arquivaPublicacao(Publicacao publicacao) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE publicacao SET arquivado=1 WHERE guid_publicacao='" + publicacao.guidPublicacao + "'");
    }

    public void salvaPublicacoes(List<Publicacao> publicacoes) {
        SQLiteDatabase db = getWritableDatabase();
        for (Publicacao publicacao : publicacoes) {
            ContentValues values = new ContentValues();
            values.put("guid_publicacao", publicacao.guidPublicacao);
            values.put("guid_anunciante", publicacao.guidAnunciante);
            values.put("id_categoria", publicacao.idCategoria);
            values.put("titulo", publicacao.titulo);
            values.put("descricao", publicacao.descricao);
            values.put("data_publicacao", publicacao.dataPublicacao);
            values.put("data_validade", publicacao.dataValidade);
            values.put("imagem", publicacao.imagem);
            db.insert("publicacao", null, values);
        }
    }
}
