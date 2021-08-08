package com.example.retrieveandgetimages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView mImageview;
    RequestQueue queue;
    int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageview=findViewById(R.id.imageView);
        queue = Volley.newRequestQueue(this);
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ImageHelper.checkReadStoragePerm(this);
            ImageHelper.checkWriteStoragePerm(this);
        }
    }



    public void downloadImage(View view) {
        int size = 200 + (int)(Math.random() * ((600) + 1));
        Log.d("TestVolley", "It entered the button");
        String url = "https://picsum.photos/"+Integer.toString(size);
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

    public void imageToFolder(View view) {
        Bitmap bitmap = ((BitmapDrawable)mImageview.getDrawable()).getBitmap();
        ImageHelper.saveImageToGallery(bitmap, this, index, "1");
        index++;
    }

    public void nextActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RetrieveImageFromFolderActivity.class);
        startActivity(intent);
    }

    public void deleteImage(View view) {
        ImageHelper.deletePhotos(this);
    }
}