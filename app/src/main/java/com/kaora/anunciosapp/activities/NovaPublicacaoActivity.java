package com.kaora.anunciosapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import com.kaora.anunciosapp.rest.ApiRestInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovaPublicacaoActivity extends AppCompatActivity {

    private static final int SELECIONAR_FOTO = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private TextView tvNomeAnunciante;
    private EditText etTitulo;
    private EditText etDescricao;
    private EditText etValidoAte;
    private DatePickerDialog datePicker;
    private SimpleDateFormat dateFormat;
    private ImageView imagem;
    private Uri fileUri;

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

    public void selecionarImagem(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione a foto"), SELECIONAR_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECIONAR_FOTO && resultCode == Activity.RESULT_OK) {
            try {
                Uri selectedImageUri = data.getData();
                uploadFile(selectedImageUri);

                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imagem.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
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
                database.marcaAnuncioComoPublicado(guidPublicacao);
                Toast.makeText(NovaPublicacaoActivity.this, "Anúncio publicado!", Toast.LENGTH_LONG).show();
                fechaActivity();
            }

            @Override
            public void onFailure(Call<Publicacao> call, Throwable t) {
                Toast.makeText(NovaPublicacaoActivity.this, "Erro ao publicar", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadFile(Uri fileUri) {
        ApiRestAdapter webservice = ApiRestAdapter.getInstance();

        File file = new File(getFileName(fileUri));

//        String nomeArquivoFotoPublicado = file.getName();
        String nomeArquivoFotoPublicado = perfilSelecionado.guidAnunciante + "_1.jpg";

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", nomeArquivoFotoPublicado, requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = webservice.postaFotoPublicacao(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public String getFileName(Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
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
