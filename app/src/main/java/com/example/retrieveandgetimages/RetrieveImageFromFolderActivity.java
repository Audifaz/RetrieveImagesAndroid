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

import com.bumptech.glide.Glide;

import java.io.File;

public class RetrieveImageFromFolderActivity extends AppCompatActivity {
    ImageView retrievedImage;
    int index = 0;
    int size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_image_from_folder);
        retrievedImage = findViewById(R.id.imageView2);
        //getImage();
        Uri imgUri = getImage(index);
        //Glide.with(this).load("https://picsum.photos/700").into(retrievedImage);
        //Glide.with(this).load(imgUri).into(retrievedImage)
        retrievedImage.setImageURI(imgUri);
    }

    public Uri getImage(int index){
        Uri collection;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else{
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.RELATIVE_PATH
        };
        String selection = MediaStore.Images.Media.RELATIVE_PATH +
                " like ?";
        String[] selectionArgs = new String[] {
                "%"+ "TestFolder" + "%"
};
        ContentResolver resolver = this.getContentResolver();
        //Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null, null, null, null );
        Cursor cursor = resolver.query(
                collection,projection,selection,selectionArgs,null
        );
        if(cursor != null){
            //Log.d("Sec_Act", "Cursor is not null, size is: " + cursor.getCount());
            size = cursor.getCount();
            cursor.moveToPosition(index);
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("Sec_Act", "getImage: " + imageUri.getPath());
            return imageUri;
        }
    return null;
    }

    public void nextImage(View view) {
        if(index==(size-1)){
            index=0;
        }else{
            index++;
        }
        Uri imgUri = getImage(index);
        //Glide.with(this).load(imgUri).into(retrievedImage);
        retrievedImage.setImageURI(imgUri);
    }
}