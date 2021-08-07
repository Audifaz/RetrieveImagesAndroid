package com.example.retrieveandgetimages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;


public class RetrieveImageFromFolderActivity extends AppCompatActivity {
    ImageView retrievedImage;
    int index = 0;
    int size;
    List<Uri> imgUris;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_image_from_folder);
        retrievedImage = findViewById(R.id.imageView2);
        getImages();
    }

    public void nextImage(View view) {
        if(imgUris!=null)
        {
            retrievedImage.setImageURI(imgUris.get(index));
            if (index == (size - 1)) {
                index = 0;
            } else {
                index++;
            }
        }
    }

    public void getImages(){
        imgUris = ImageHelper.getPhotos(this);
        if(imgUris != null)
            size=imgUris.size();
    }
}