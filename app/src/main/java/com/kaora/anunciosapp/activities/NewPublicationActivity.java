package com.kaora.anunciosapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.anunciosapp.rest.MediaUploadService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPublicationActivity extends AppCompatActivity {

    private static final int IMG_REQUEST = 1;

    private TextView tvNomeAnunciante;
    private EditText etTitulo;
    private EditText etDescricao;
    private EditText etValidoAte;
    private DatePickerDialog datePicker;
    private SimpleDateFormat dateFormat;
    private ImageView imagem;
    private Uri mediaFileUri;

    private ProgressDialog progressDialog;
    private MyDatabaseHelper database = MyDatabaseHelper.getInstance(this);
    private Advertiser advertiser;
    private Publication publication;
    private ApiRestAdapter webservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_publicacao);

        tvNomeAnunciante = (TextView) findViewById(R.id.tvNomeAnunciante);
        etTitulo = (EditText) findViewById(R.id.etTitulo);
        imagem = (ImageView) findViewById(R.id.fotoPublicacao);
        etDescricao = (EditText) findViewById(R.id.etDescricao);
        etValidoAte = (EditText) findViewById(R.id.etValidoAte);
        etValidoAte.setInputType(InputType.TYPE_CLASS_DATETIME);
        etValidoAte.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                datePicker.show();
                return false;
            }
        });

        Intent intent = getIntent();
        String guidAnunciante = intent.getStringExtra("guid_anunciante");
        advertiser = database.getProfileByGuid(guidAnunciante);

        tvNomeAnunciante.setText(advertiser.tradingName);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        preparaDialogData();
    }

    /* Store the file url as it will be null after returning from camera app */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation changes
        outState.putParcelable("file_uri", mediaFileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        mediaFileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void preparaDialogData() {
        Calendar calendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar dataInformada = Calendar.getInstance();
                dataInformada.set(year, month, dayOfMonth);
                etValidoAte.setText(dateFormat.format(dataInformada.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void iniciaActivitySelecaoImagem(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecione a foto"), IMG_REQUEST);
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

    public void postPublication(View view) {
        progressDialog = ProgressDialog.show(NewPublicationActivity.this, "Postando Publicação", "Aguarde...", false, false);
        publication = new Publication();
        populateWithActivityData(publication);
        database.savePublication(publication);
        postCurrentPublication();
    }

    private void populateWithActivityData(Publication publication) {
        publication.title = etTitulo.getText().toString();
        publication.description = etDescricao.getText().toString();
        publication.setDueDate(extraiData(etValidoAte.getText().toString()));
        publication.advertiserGuid = advertiser.advertiserGuid;
        publication.category_id = advertiser.categoryId;
    }

    /* Publish media first
       After complete, MediaTransferResponseHandler will dispatch publication publishing */
    private void postCurrentPublication() {
        if (mediaFileUri != null) {
            progressDialog.setMessage("Enviando imagens...");
            MediaUploadService mediaUpload = new MediaUploadService(NewPublicationActivity.this);
            mediaUpload.upload(mediaFileUri, new MediaTransferResponseHandler(), MediaUploadService.PUBLICATION_IMAGE_UPLOAD);
        } else {
            sendPublicationToWebservice(publication);
        }
    }

    private class MediaTransferResponseHandler extends MediaUploadService.MediaSentEvent implements Callback<ResponseBody> {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            publication.imageFile = mediaFileName;
            sendPublicationToWebservice(publication);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressDialog.dismiss();
            Toast.makeText(NewPublicationActivity.this, "Erro ao enviar Imagem", Toast.LENGTH_LONG).show();
        }
    }

    private void sendPublicationToWebservice(Publication publication) {
        progressDialog.setMessage("Postando Publicação...");
        webservice = ApiRestAdapter.getInstance();
        webservice.publicaPublicacao(publication, new Callback<Publication>() {
            @Override
            public void onResponse(Call<Publication> call, Response<Publication> response) {
                Publication publication = response.body();
                database.savePublication(publication);
                progressDialog.dismiss();
                fechaActivity();
            }

            @Override
            public void onFailure(Call<Publication> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(NewPublicationActivity.this, "Erro ao postar Publicação", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fechaActivity() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    NewPublicationActivity.this.finish();
                } catch (Exception e) {

                }
            }
        };
        thread.start();
    }

    private Date extraiData(String data) {
        try {
            return dateFormat.parse(etValidoAte.getText().toString());
        } catch (ParseException e) {
            Date hoje = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(hoje);
            c.add(Calendar.DATE, 7); // dar uma validade de 7 dias
            return c.getTime();
        }
    }

}
