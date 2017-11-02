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

public class AdvertiserProfileActivity extends AppCompatActivity {

    private static final int IMG_REQUEST = 1;

    ImageView image;
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
    private ArrayAdapter<Cidade> cityAdapter;
    private ArrayAdapter<PublicationCategory> categoryAdapter;
    final private List<Cidade> cities = new ArrayList<>();
    final private List<PublicationCategory> categories = new ArrayList<>();
    private Uri mediaFileUri;
    private final Advertiser advertiser = new Advertiser();
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertiser_profile);
        database = MyDatabaseHelper.getInstance(this);
        webservice = ApiRestAdapter.getInstance();
        inicializaInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCitiesFromWebService();
    }

    private void inicializaInterface() {
        image = (ImageView) findViewById(R.id.imagem);
        etNome = (EditText) findViewById(R.id.etNome);
        etTelefone = (EditText) findViewById(R.id.etTelefone);
        etCelular = (EditText) findViewById(R.id.etCelular);
        etCelular.setText(getCellphoneNumber());
        etEmail = (EditText) findViewById(R.id.etEmail);
        etEndereco = (EditText) findViewById(R.id.etEndereco);
        etNumero = (EditText) findViewById(R.id.etNumero);
        etBairro = (EditText) findViewById(R.id.etBairro);
        createCitySpinner();
        createCategorySpinner();
    }

    private String getCellphoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String cellphoneNumber = telephonyManager.getLine1Number();
        if (cellphoneNumber == null) {
            cellphoneNumber = "";
        }
        return cellphoneNumber;
    }

    private void createCitySpinner() {
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCidades = (Spinner) findViewById(R.id.spCidade);
        spCidades.setAdapter(cityAdapter);
        spCidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCategoriesFromWebService(cities.get(position).idCidade);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias = (Spinner) findViewById(R.id.spCategoria);
        spCategorias.setAdapter(categoryAdapter);
    }

    private void getCitiesFromWebService() {
        webservice.obtemCidades(new Callback<List<Cidade>>() {

            @Override
            public void onResponse(Call<List<Cidade>> call, Response<List<Cidade>> response) {
                updateCitySpinner(response.body());
            }
            @Override
            public void onFailure(Call<List<Cidade>> call, Throwable t) {

            }
        });
    }

    private void getCategoriesFromWebService(int idCidade) {
        webservice.obtemCategorias(idCidade, new Callback<List<PublicationCategory>>() {
            @Override
            public void onResponse(Call<List<PublicationCategory>> call, Response<List<PublicationCategory>> response) {
                updateCategorySpinner(response.body());
            }

            @Override
            public void onFailure(Call<List<PublicationCategory>> call, Throwable t) {

            }
        });
    }

    private void updateCitySpinner(List<Cidade> cidades) {
        this.cities.clear();
        for (Cidade cidade : cidades) {
            this.cities.add(cidade);
        }
        cityAdapter.notifyDataSetChanged();
    }

    private void updateCategorySpinner(List<PublicationCategory> publicationCategories) {
        this.categories.clear();
        for (PublicationCategory publicationCategory : publicationCategories) {
            this.categories.add(publicationCategory);
        }
        categoryAdapter.notifyDataSetChanged();
    }

    public void startImageSelectionActivity(View view) {
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
                image.setImageBitmap(bitmap);
                image.getLayoutParams().height = bitmap.getHeight();
                image.getLayoutParams().width = bitmap.getWidth();
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
        startNewPublicationActivity(advertiser.advertiserGuid);
    }

    private void postCurrentUserProfile() {
        progress = ProgressDialog.show(AdvertiserProfileActivity.this, "Postando Perfil", "Aguarde...", false, false);
        // if no media file were selected...
        if (mediaFileUri != null) {
            progress.setMessage("Enviando imagens...");
            MediaUploadService mediaUpload = new MediaUploadService(AdvertiserProfileActivity.this);
            mediaUpload.upload(mediaFileUri, new MediaTransferResponseHandler(), MediaUploadService.ADVERTISER_IMAGE_UPLOAD);
        } else {
            sendAdvertiserProfileToWebservice(advertiser);
        }
    }

    private void startNewPublicationActivity(String guidAnunciante) {
        Intent intent = new Intent(this, NewPublicationActivity.class);
        intent.putExtra("guid_anunciante", guidAnunciante);
        startActivity(intent);
    }

    private void closeThisActivity() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    AdvertiserProfileActivity.this.finish();
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
            progress.dismiss();
            Toast.makeText(AdvertiserProfileActivity.this, "Erro ao enviar Imagem", Toast.LENGTH_LONG).show();
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
                progress.dismiss();
                closeThisActivity();
            }

            @Override
            public void onFailure(Call<Advertiser> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(AdvertiserProfileActivity.this, "Erro ao publicar perfil", Toast.LENGTH_LONG).show();
            }
        });
    }

}
