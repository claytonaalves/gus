package com.kaora.anunciosapp.rest;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaUploadService {

    public static final int PUBLICATION_IMAGE_UPLOAD = 1;
    public static final int ADVERTISER_IMAGE_UPLOAD = 2;

    private Context context;

    public MediaUploadService(Context context) {
        this.context = context;
    }

    public void upload(Uri fileUri, MediaSentEvent onImageSent, int type) {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream in = null;
        try {
            in = contentResolver.openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (in == null) return;
        byte[] buf;
        try {
            buf = new byte[in.available()];
            while (in.read(buf) != -1);

            String nomeArquivo = generateFileName(fileUri, context);
            onImageSent.mediaFileName = nomeArquivo;

            RequestBody requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(fileUri)), buf);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", nomeArquivo, requestFile);

            ApiRestAdapter webservice = ApiRestAdapter.getInstance();
            Call<ResponseBody> call;
            if (type == PUBLICATION_IMAGE_UPLOAD) {
                call = webservice.postaFotoPublicacao(requestFile, body);
                call.enqueue(onImageSent);
            } else if (type == ADVERTISER_IMAGE_UPLOAD) {
                call = webservice.postAdvertiserImage(requestFile, body);
                call.enqueue(onImageSent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    private String generateFileName(Uri fileUri, Context context) {
        String extension = getMimeType(context, fileUri);
        return UUID.randomUUID().toString() + "." + extension;
    }

    private static String getMimeType(Context context, Uri uri) {
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

    public static class MediaSentEvent implements Callback<ResponseBody> {

        public String mediaFileName;

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    }

}
