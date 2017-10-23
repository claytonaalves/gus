package com.kaora.anunciosapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovaPublicacaoActivity extends AppCompatActivity {

    private static final String TAG = NovaPublicacaoActivity.class.getSimpleName(); // LogCat tag

    private static final int IMG_REQUEST = 1;
    private static final String IMAGE_DIRECTORY_NAME = "AdsImages"; // Directory name to store captured images and videos
    public static final int MEDIA_TYPE_IMAGE = 1;

    private TextView tvNomeAnunciante;
    private EditText etTitulo;
    private EditText etDescricao;
    private EditText etValidoAte;
    private DatePickerDialog datePicker;
    private SimpleDateFormat dateFormat;
    private ImageView imagem;
    private Uri mediaFileUri;
    private Bitmap bitmap;

    private ProgressDialog progressDialog;
    private MyDatabaseHelper database = MyDatabaseHelper.getInstance(this);
    private PerfilAnunciante perfilSelecionado;
    private Publicacao publicacao;
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
        perfilSelecionado = database.selecionaPerfil(guidAnunciante);

        tvNomeAnunciante.setText(perfilSelecionado.nomeFantasia);

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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mediaFileUri);
                imagem.setImageBitmap(bitmap);
                imagem.getLayoutParams().height = bitmap.getHeight();
                imagem.getLayoutParams().width = bitmap.getWidth();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void postaPublicacao(View view) {
        progressDialog = ProgressDialog.show(NovaPublicacaoActivity.this, "Postando Publicação", "Aguarde...", false, false);

        publicacao = new Publicacao();
        publicacao.titulo = etTitulo.getText().toString();
        publicacao.descricao = etDescricao.getText().toString();
        publicacao.setDataValidade(extraiData(etValidoAte.getText().toString()));
        publicacao.guidAnunciante = perfilSelecionado.guidAnunciante;
        publicacao.idCategoria = perfilSelecionado.idCategoria;
        database.salvaPublicacao(publicacao);
        postaPublicacaoRemotamente(publicacao);
    }

    private void postaPublicacaoRemotamente(Publicacao publicacao) {
        webservice = ApiRestAdapter.getInstance();
        webservice.publicaPublicacao(publicacao, new Callback<Publicacao>() {
            @Override
            public void onResponse(Call<Publicacao> call, Response<Publicacao> response) {
                postaMidiaRemotamente(mediaFileUri);
            }

            @Override
            public void onFailure(Call<Publicacao> call, Throwable t) {
                Toast.makeText(NovaPublicacaoActivity.this, "Erro ao publicar", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void postaMidiaRemotamente(Uri fileUri) {
        progressDialog.setMessage("Enviando imagens...");
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (in == null) return;
        byte[] buf;
        try {
            buf = new byte[in.available()];
            while (in.read(buf) != -1);

            String extension = getMimeType(this, fileUri);
            String nomeArquivo = UUID.randomUUID().toString() + "." + extension;

            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), buf);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", nomeArquivo, requestFile);

            webservice = ApiRestAdapter.getInstance();
            Call<ResponseBody> call = webservice.postaFotoPublicacao(requestFile, body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    database.marcaComoPublicado(publicacao);
                    progressDialog.dismiss();
                    fechaActivity();
                    Log.v("Upload", "success");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("Upload de midia", t.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
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

}
