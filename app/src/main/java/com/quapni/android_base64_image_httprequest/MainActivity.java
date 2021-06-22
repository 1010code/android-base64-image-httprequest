package com.quapni.android_base64_image_httprequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final int GALLERY_REQUEST_CODE = 105;
    ImageView selectedImage;
    Button galleryBtn;
    public TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedImage = findViewById(R.id.displayImageView);
        galleryBtn = findViewById(R.id.galleryBtn);
        tvResult = (TextView) findViewById(R.id.textView);

        // Open Gallery
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Gallery
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                try {
                    //encode image to base64 string
                    // URI to Bitmap conversion
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                    // Bitmap to Base64 String conversion
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 76, outputStream);
                    byte[] byteArray = outputStream.toByteArray();
                    String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    postData("https://5a279f5c15af.ngrok.io/scaler",encodedString);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                selectedImage.setImageURI(contentUri);
            }

        }
    }
    private void postData(String url, String encodedString){
        long startTime = System.nanoTime();
        /**建立連線*/
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        /**設置傳送所需夾帶的內容*/
        FormBody formBody = new FormBody.Builder()
                .add("image", encodedString)
                .build();
        /**設置傳送需求*/
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        /**設置回傳*/
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                /**如果傳送過程有發生錯誤*/
                Log.e("error",e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                // updates the UI onto the main thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tvResult.setText("POST回傳：" + response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                long endTime = System.nanoTime();
                long duration = (endTime - startTime);
                Log.e("Execute Time ",duration/ 1000000000+" sec");
            }
        });
    }
}