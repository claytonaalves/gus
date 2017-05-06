package com.kaora.anunciosapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.PerfilAnunciante;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class NovoPerfilActivity extends AppCompatActivity {

    private static final int SELECIONAR_FOTO = 1;

    ImageView imagem;
    EditText etNome;
    EditText etTelefone;
    EditText etCelular;
    EditText etEmail;
    EditText etEndereco;
    EditText etNumero;
    EditText etBairro;
    EditText etCidade;
    Spinner spCategorias;
    Spinner spEstados;

    MyDatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_perfil);
        database = MyDatabaseHelper.getInstance(this);
        inicializaInterface();
    }

    private void inicializaInterface() {
        imagem = (ImageView) findViewById(R.id.imagem);
        etNome = (EditText) findViewById(R.id.etNome);
        etTelefone = (EditText) findViewById(R.id.etTelefone);
        etCelular = (EditText) findViewById(R.id.etCelular);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etEndereco = (EditText) findViewById(R.id.etEndereco);
        etNumero = (EditText) findViewById(R.id.etNumero);
        etBairro = (EditText) findViewById(R.id.etBairro);
        etCidade = (EditText) findViewById(R.id.etCidade);

        List<Categoria> categorias = database.todasCategorias();

        ArrayAdapter<Categoria> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estados, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCategorias = (Spinner) findViewById(R.id.spCategoria);
        spCategorias.setAdapter(adapterCategorias);

        spCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Categoria c = (Categoria) spCategorias.getSelectedItem();
                Toast.makeText(NovoPerfilActivity.this, c.descricao, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spEstados = (Spinner) findViewById(R.id.spEstado);
        spEstados.setAdapter(adapter);
    }

    public void selecionarImagem(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECIONAR_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECIONAR_FOTO && resultCode == Activity.RESULT_OK) {
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imagem.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void salvaPerfil(View view) {
        PerfilAnunciante perfil = new PerfilAnunciante();
        perfil.nome = etNome.getText().toString();
        perfil.telefone = etTelefone.getText().toString();
        perfil.celular = etCelular.getText().toString();
        perfil.email = etEmail.getText().toString();
        perfil.endereco = etEndereco.getText().toString();
        perfil.numero = etNumero.getText().toString();
        perfil.bairro = etBairro.getText().toString();
        perfil.cidade = etCidade.getText().toString();
        perfil.estado = spEstados.getSelectedItem().toString().substring(0, 2);
        perfil.idCategoria = ((Categoria) spCategorias.getSelectedItem())._id;

        database.salvaPerfil(perfil);
        mostraActivityNovoAnuncio(perfil._id);

        finish();
    }

    private void mostraActivityNovoAnuncio(long idPerfil) {
        Intent intent = new Intent(this, NovoAnuncioActivity.class);
        intent.putExtra("idPerfil", idPerfil);
        startActivity(intent);
    }

}
