package com.kaora.anunciosapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.models.PublicationCategory;
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

    private ImageView profileImage;
    private EditText tradingNameEditText;
    private EditText phoneNumberEditText;
    private EditText cellphoneEditText;
    private EditText emailEditText;
    private EditText streetNameEditText;
    private EditText addressNumberEditText;
    private EditText neighbourhoodEditText;
    private Spinner categorySpinner;
    private Spinner citySpinner;

    private MyDatabaseHelper database;
    private ApiRestAdapter webservice;
    private ArrayAdapter<Cidade> cityAdapter;
    private ArrayAdapter<PublicationCategory> categoryAdapter;
    final private List<Cidade> cities = new ArrayList<>();
    final private List<PublicationCategory> categories = new ArrayList<>();
    private Uri mediaFileUri;
    private ProgressDialog progress;

    private Advertiser advertiser;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertiser_profile);
        database = MyDatabaseHelper.getInstance(this);
        webservice = ApiRestAdapter.getInstance();

        initializeInterface();

        Intent intent = getIntent();
        if (intent.hasExtra("advertiser")) {
            advertiser = (Advertiser) intent.getSerializableExtra("advertiser");
            editMode = true;
            setTitle("Editando Perfil");
        } else {
            advertiser = new Advertiser();
            advertiser.localProfile = true;
            advertiser.cellphone = getCellphoneNumber();
            editMode = false;
        }

        getCitiesFromWebService();
        updateInterfaceData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeInterface() {
        profileImage = (ImageView) findViewById(R.id.imagem);
        tradingNameEditText = (EditText) findViewById(R.id.etNome);
        phoneNumberEditText = (EditText) findViewById(R.id.etTelefone);
        cellphoneEditText = (EditText) findViewById(R.id.etCelular);
        emailEditText = (EditText) findViewById(R.id.etEmail);
        streetNameEditText = (EditText) findViewById(R.id.etEndereco);
        addressNumberEditText = (EditText) findViewById(R.id.etNumero);
        neighbourhoodEditText = (EditText) findViewById(R.id.etBairro);
        createCitySpinner();
        createCategorySpinner();
    }

    private void updateInterfaceData() {
        tradingNameEditText.setText(advertiser.tradingName);
        phoneNumberEditText.setText(advertiser.phoneNumber);
        cellphoneEditText.setText(advertiser.cellphone);
        emailEditText.setText(advertiser.email);
        streetNameEditText.setText(advertiser.streetName);
        addressNumberEditText.setText(advertiser.addressNumber);
        neighbourhoodEditText.setText(advertiser.neighbourhood);

        if (!(advertiser.imageFile == null) && (!advertiser.imageFile.equals(""))) {
            SimpleDraweeView imagem = (SimpleDraweeView) findViewById(R.id.imagem);
            imagem.setImageURI(ApiRestAdapter.ADVERTISERS_IMAGE_PATH + advertiser.imageFile);
        }
    }

    private int getCityIndexFromId(int cityId) {
        if (cities.size() == 0) return 0;
        Cidade city;
        for (int i = 0; i < cities.size(); i++) {
            city = cities.get(i);
            if (city.idCidade == cityId) {
                return i;
            }
        }
        return 0;
    }

    private int getCategoryIndexFromId(int categoryId) {
        if (categories.size() == 0) return 0;
        PublicationCategory category;
        for (int i = 0; i < categories.size(); i++) {
            category = categories.get(i);
            if (category.idCategoria == categoryId) {
                return i;
            }
        }
        return 0;
    }

    private void loadDataFromInterface() {
        advertiser.tradingName = tradingNameEditText.getText().toString();
        advertiser.phoneNumber = phoneNumberEditText.getText().toString();
        advertiser.cellphone = cellphoneEditText.getText().toString();
        advertiser.email = emailEditText.getText().toString();
        advertiser.streetName = streetNameEditText.getText().toString();
        advertiser.addressNumber = addressNumberEditText.getText().toString();
        advertiser.neighbourhood = neighbourhoodEditText.getText().toString();
        advertiser.cityId = ((Cidade) citySpinner.getSelectedItem()).idCidade;
        advertiser.categoryId = ((PublicationCategory) categorySpinner.getSelectedItem()).idCategoria;
    }

    private String getCellphoneNumber() {
        /* Marshmallow changes the way permissions are granted. Review this later */
        if (Build.VERSION.SDK_INT > 23) {
            return "";
        }
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
        citySpinner = (Spinner) findViewById(R.id.spCidade);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        categorySpinner = (Spinner) findViewById(R.id.spCategoria);
        categorySpinner.setAdapter(categoryAdapter);
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
        citySpinner.setSelection(getCityIndexFromId(advertiser.cityId));
    }

    private void updateCategorySpinner(List<PublicationCategory> publicationCategories) {
        this.categories.clear();
        for (PublicationCategory publicationCategory : publicationCategories) {
            this.categories.add(publicationCategory);
        }
        categoryAdapter.notifyDataSetChanged();
        categorySpinner.setSelection(getCategoryIndexFromId(advertiser.categoryId));
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
                profileImage.setImageBitmap(bitmap);
                profileImage.getLayoutParams().height = bitmap.getHeight();
                profileImage.getLayoutParams().width = bitmap.getWidth();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAdvertiserProfile(View view) {
        progress = ProgressDialog.show(AdvertiserProfileActivity.this,
                "Atualizando Perfil", "Aguarde...", false, false);
        loadDataFromInterface();
        postCurrentUserProfile();
        database.saveAdvertiser(advertiser);
        if (editMode) {
            closeThisActivity();
        } else {
            startNewPublicationActivity(advertiser.advertiserGuid);
        }
    }

    private void postCurrentUserProfile() {
        // if no media file were selected...
        if (mediaFileUri != null) {
            progress.setMessage("Enviando imagens...");
            MediaUploadService mediaUpload = new MediaUploadService(AdvertiserProfileActivity.this, new MediaTransferResponseHandler(), MediaUploadService.ADVERTISER_IMAGE_UPLOAD);
            mediaUpload.upload(mediaFileUri);
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
                    progress.dismiss();
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

    private void sendAdvertiserProfileToWebservice(final Advertiser advertiser) {
        ApiRestAdapter api = ApiRestAdapter.getInstance();
        api.postAdvertiserProfile(advertiser, new Callback<Advertiser>() {
            @Override
            public void onResponse(Call<Advertiser> call, Response<Advertiser> response) {
                advertiser.published = true;
                database.saveAdvertiser(advertiser);
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
