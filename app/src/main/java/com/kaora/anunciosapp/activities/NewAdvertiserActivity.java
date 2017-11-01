package com.kaora.anunciosapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.PublicationCategory;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.anunciosapp.rest.MediaUploadService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAdvertiserActivity extends AppCompatActivity {

    private static final int IMG_REQUEST = 1;

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
    private ArrayAdapter<PublicationCategory> categoriaAdapter;
    final private List<Cidade> cidades = new ArrayList<>();
    final private List<PublicationCategory> publicationCategories = new ArrayList<>();
    private Uri mediaFileUri;
    private final Advertiser advertiser = new Advertiser();
    private ProgressDialog progressDialog;

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
        etCelular.setText(getCellphoneNumber());
        etEmail = (EditText) findViewById(R.id.etEmail);
        etEndereco = (EditText) findViewById(R.id.etEndereco);
        etNumero = (EditText) findViewById(R.id.etNumero);
        etBairro = (EditText) findViewById(R.id.etBairro);
        criaSpinnerCidades();
        criaSpinnerCategorias();
    }

    private String getCellphoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String cellphoneNumber = telephonyManager.getLine1Number();
        if (cellphoneNumber == null) {
            cellphoneNumber = "";
        }
        return cellphoneNumber;
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
        categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, publicationCategories);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias = (Spinner) findViewById(R.id.spCategoria);
        spCategorias.setAdapter(categoriaAdapter);
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
        webservice.obtemCategorias(idCidade, new Callback<List<PublicationCategory>>() {
            @Override
            public void onResponse(Call<List<PublicationCategory>> call, Response<List<PublicationCategory>> response) {
                atualizaSpinnerCategorias(response.body());
            }

            @Override
            public void onFailure(Call<List<PublicationCategory>> call, Throwable t) {

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

    private void atualizaSpinnerCategorias(List<PublicationCategory> publicationCategories) {
        this.publicationCategories.clear();
        for (PublicationCategory publicationCategory : publicationCategories) {
            this.publicationCategories.add(publicationCategory);
        }
        categoriaAdapter.notifyDataSetChanged();
    }

    public void iniciaActivitySelecaoImagem(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            mediaFileUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mediaFileUri);
                imagem.setImageBitmap(bitmap);
                imagem.getLayoutParams().height = bitmap.getHeight();
                imagem.getLayoutParams().width = bitmap.getWidth();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAdvertiserProfile(View view) {
        advertiser.tradingName = etNome.getText().toString();
        advertiser.phoneNumber = etTelefone.getText().toString();
        advertiser.cellphone = etCelular.getText().toString();
        advertiser.email = etEmail.getText().toString();
        advertiser.streetName = etEndereco.getText().toString();
        advertiser.addressNumber = etNumero.getText().toString();
        advertiser.neighbourhood = etBairro.getText().toString();
        advertiser.cityId = ((Cidade) spCidades.getSelectedItem()).idCidade;
        advertiser.categoryId = ((PublicationCategory) spCategorias.getSelectedItem()).idCategoria;

        postCurrentUserProfile();
        database.saveAdvertiserProfile(advertiser);
        mostraActivityNovaPublicacao(advertiser.advertiserGuid);
    }

    private void postCurrentUserProfile() {
        progressDialog = ProgressDialog.show(NewAdvertiserActivity.this, "Postando Perfil", "Aguarde...", false, false);
        // if no media file were selected...
        if (mediaFileUri != null) {
            progressDialog.setMessage("Enviando imagens...");
            MediaUploadService mediaUpload = new MediaUploadService(NewAdvertiserActivity.this);
            mediaUpload.upload(mediaFileUri, new MediaTransferResponseHandler(), MediaUploadService.ADVERTISER_IMAGE_UPLOAD);
        } else {
            sendAdvertiserProfileToWebservice(advertiser);
        }
    }

    private void mostraActivityNovaPublicacao(String guidAnunciante) {
        Intent intent = new Intent(this, NewPublicationActivity.class);
        intent.putExtra("guid_anunciante", guidAnunciante);
        startActivity(intent);
    }

    private void fechaActivity() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    NewAdvertiserActivity.this.finish();
                } catch (Exception e) {

                }
            }
        };
        thread.start();
    }

    private class MediaTransferResponseHandler extends MediaUploadService.MediaSentEvent implements Callback<ResponseBody> {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            advertiser.imageFile = mediaFileName;
            sendAdvertiserProfileToWebservice(advertiser);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressDialog.dismiss();
            Toast.makeText(NewAdvertiserActivity.this, "Erro ao enviar Imagem", Toast.LENGTH_LONG).show();
        }
    }

    private void sendAdvertiserProfileToWebservice(Advertiser advertiser) {
        ApiRestAdapter api = ApiRestAdapter.getInstance();
        api.postAdvertiserProfile(advertiser, new Callback<Advertiser>() {
            @Override
            public void onResponse(Call<Advertiser> call, Response<Advertiser> response) {
                Advertiser advertiser = response.body();
                advertiser.published = true;
                database.saveAdvertiserProfile(advertiser);
                progressDialog.dismiss();
                fechaActivity();
            }

            @Override
            public void onFailure(Call<Advertiser> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(NewAdvertiserActivity.this, "Erro ao publicar perfil", Toast.LENGTH_LONG).show();
            }
        });
    }

}
