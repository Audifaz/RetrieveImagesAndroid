package com.example.retrieveandgetimages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ImageView mImageview;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageview=findViewById(R.id.imageView);
        queue = Volley.newRequestQueue(this);
    }

    public void downloadImage(View view) {
        Log.d("TestVolley", "It entered the button");
        String url = "https://picsum.photos/500";
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // handle Bitmap image
                        Log.d("TestVolley", "It entered");
                        mImageview.setImageBitmap(bitmap);
                        Log.d("TestVolley", "It worked");
                        //saveImageToGallery(bitmap);
                    }
                }, 0, 0, null, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        // handle error
                        Log.d("TestVolley", "Error"+ error);
                    }
                });
// Access the RequestQueue through your singleton class.
        queue.add(imgRequest);
    }

    private void saveImageToGallery(Bitmap bitmap) {
        OutputStream fos;
        try{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.P){
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_1.jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ File.separator+"TestFolder");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, fos);
                Objects.requireNonNull(fos);
                Toast.makeText(this,"Image Saved", Toast.LENGTH_SHORT).show();
            }

        }catch (Error | FileNotFoundException e){
            Toast.makeText(this,"Image not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void imageToFolder(View view) {
        Bitmap bitmap = ((BitmapDrawable)mImageview.getDrawable()).getBitmap();
        saveImageToGallery(bitmap);
    }

    public void nextActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RetrieveImageFromFolderActivity.class);
        startActivity(intent);
    }
}