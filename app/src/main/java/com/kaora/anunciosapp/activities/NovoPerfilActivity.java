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
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    Spinner spCategorias;
    Spinner spCidades;

    private MyDatabaseHelper database;
    private ApiRestAdapter webservice;
    private ArrayAdapter<Cidade> cidadeAdapter;
    private ArrayAdapter<Categoria> categoriaAdapter;
    final private List<Cidade> cidades = new ArrayList<>();
    final private List<Categoria> categorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_perfil);
        database = MyDatabaseHelper.getInstance(this);
        webservice = ApiRestAdapter.getInstance();
        inicializaInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtemListaDeCidadesDoWebservice();
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
        criaSpinnerCidades();
        criaSpinnerCategorias();
    }

    private void criaSpinnerCidades() {
        cidadeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cidades);
        cidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCidades = (Spinner) findViewById(R.id.spCidade);
        spCidades.setAdapter(cidadeAdapter);
        spCidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                obtemListaDeCategoriasDoWebservice(cidades.get(position).idCidade);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void criaSpinnerCategorias() {
        categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias = (Spinner) findViewById(R.id.spCategoria);
        spCategorias.setAdapter(categoriaAdapter);
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
    }

    private void obtemListaDeCidadesDoWebservice() {
        webservice.obtemCidades(new Callback<List<Cidade>>() {
            @Override
            public void onResponse(Call<List<Cidade>> call, Response<List<Cidade>> response) {
                atualizaSpinnerCidades(response.body());
            }

            @Override
            public void onFailure(Call<List<Cidade>> call, Throwable t) {

            }
        });
    }

    private void obtemListaDeCategoriasDoWebservice(int idCidade) {
        webservice.obtemCategorias(idCidade, new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                atualizaSpinnerCategorias(response.body());
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {

            }
        });
    }

    private void atualizaSpinnerCidades(List<Cidade> cidades) {
        this.cidades.clear();
        for (Cidade cidade : cidades) {
            this.cidades.add(cidade);
        }
        cidadeAdapter.notifyDataSetChanged();
    }

    private void atualizaSpinnerCategorias(List<Categoria> categorias) {
        this.categorias.clear();
        for (Categoria categoria : categorias) {
            this.categorias.add(categoria);
        }
        categoriaAdapter.notifyDataSetChanged();
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
        perfil.nomeFantasia = etNome.getText().toString();
        perfil.telefone = etTelefone.getText().toString();
        perfil.celular = etCelular.getText().toString();
        perfil.email = etEmail.getText().toString();
        perfil.endereco = etEndereco.getText().toString();
        perfil.numero = etNumero.getText().toString();
        perfil.bairro = etBairro.getText().toString();
        perfil.idCidade = ((Cidade) spCidades.getSelectedItem()).idCidade;
        perfil.idCategoria = ((Categoria) spCategorias.getSelectedItem()).idCategoria;

        database.salvaPerfil(perfil);
        publicaPerfilAnunciante(perfil);
        mostraActivityNovaPublicacao(perfil.guidAnunciante);
    }

    private void publicaPerfilAnunciante(PerfilAnunciante perfil) {
        final String guidAnunciante = perfil.guidAnunciante;
        ApiRestAdapter api = ApiRestAdapter.getInstance();
        api.publicaAnunciante(perfil, new Callback<PerfilAnunciante>() {
            @Override
            public void onResponse(Call<PerfilAnunciante> call, Response<PerfilAnunciante> response) {
                database.marcaPerfilComoPublicado(guidAnunciante);
                Toast.makeText(NovoPerfilActivity.this, "Perfil publicado!", Toast.LENGTH_LONG).show();
                fechaActivity();
            }

            @Override
            public void onFailure(Call<PerfilAnunciante> call, Throwable t) {
                Toast.makeText(NovoPerfilActivity.this, "Erro ao publicar perfil", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostraActivityNovaPublicacao(String guidAnunciante) {
        Intent intent = new Intent(this, NovaPublicacaoActivity.class);
        intent.putExtra("guid_anunciante", guidAnunciante);
        startActivity(intent);
    }

    private void fechaActivity() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    NovoPerfilActivity.this.finish();
                } catch (Exception e) {

                }
            }
        };
        thread.start();
    }

}
