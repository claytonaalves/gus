package com.kaora.anunciosapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.models.Publicacao;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovaPublicacaoActivity extends AppCompatActivity {

    private static final String TAG = NovaPublicacaoActivity.class.getSimpleName(); // LogCat tag

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "AdsImages"; // Directory name to store captured images and videos
    public static final int MEDIA_TYPE_IMAGE = 1;

    private TextView tvNomeAnunciante;
    private EditText etTitulo;
    private EditText etDescricao;
    private EditText etValidoAte;
    private DatePickerDialog datePicker;
    private SimpleDateFormat dateFormat;
    private ImageView imagem;
    private Uri fileUri;

    private Bitmap bitmap;
    ByteArrayOutputStream byteArrayOutputStream;

    private MyDatabaseHelper database = MyDatabaseHelper.getInstance(this);
    private PerfilAnunciante perfilSelecionado;

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
        perfilSelecionado = database.selecionaPerfil(guidAnunciante);

        tvNomeAnunciante.setText(perfilSelecionado.nomeFantasia);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        preparaDialogData();

        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    /* Store the file url as it will be null after returning from camera app */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(Intent.createChooser(intent, "Selecione a foto"), CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imagem.setImageBitmap(bitmap);
                imagem.getLayoutParams().height = bitmap.getHeight();
                imagem.getLayoutParams().width = bitmap.getWidth();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void publicaAnuncio(View view) {
        Publicacao publicacao = new Publicacao();
        publicacao.titulo = etTitulo.getText().toString();
        publicacao.descricao = etDescricao.getText().toString();
        publicacao.setDataValidade(extraiData(etValidoAte.getText().toString()));
        publicacao.guidAnunciante = perfilSelecionado.guidAnunciante;
        publicacao.idCategoria = perfilSelecionado.idCategoria;
        database.salvaPublicacao(publicacao);
        publicaAnuncioRemotamente(publicacao);
    }

    private void publicaAnuncioRemotamente(Publicacao publicacao) {
        final String guidPublicacao = publicacao.guidPublicacao;
        ApiRestAdapter api = ApiRestAdapter.getInstance();
        api.publicaPublicacao(publicacao, new Callback<Publicacao>() {
            @Override
            public void onResponse(Call<Publicacao> call, Response<Publicacao> response) {
                uploadImageToServer();
                database.marcaAnuncioComoPublicado(guidPublicacao);
//                Toast.makeText(NovaPublicacaoActivity.this, "Anúncio publicado!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Publicacao> call, Throwable t) {
                Toast.makeText(NovaPublicacaoActivity.this, "Erro ao publicar", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fechaActivity() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    NovaPublicacaoActivity.this.finish();
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

    // ====================================
    // Helper methods

    /* Creating file uri to store image/video */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /* Returning image/video */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    // ====================================

    public void uploadImageToServer() {
        byte[] byteArray;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(NovaPublicacaoActivity.this, "Publicando anúncio", "Aguarde...", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                progressDialog.dismiss();
                Toast.makeText(NovaPublicacaoActivity.this, string1, Toast.LENGTH_LONG).show();
                fechaActivity();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put("image_tag", "image name");
                HashMapParams.put("image_data", ConvertImage);
                String FinalData = imageProcessClass.ImageHttpRequest(ApiRestAdapter.HOST + "/api/v1/publicacoes/fotos", HashMapParams);
                return FinalData;
            }
        }

        AsyncTaskUploadClass task = new AsyncTaskUploadClass();
        task.execute();
    }

    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(requestURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(bufferedWriterDataFN(PData));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                int RC = httpURLConnection.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReader.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            StringBuilder stringBuilder = new StringBuilder();
            boolean check = true;
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilder.toString();
        }

    }

}
