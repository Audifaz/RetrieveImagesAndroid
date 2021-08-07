package com.example.retrieveandgetimages;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ImageHelper {
    public static List<Uri> getPhotos(Activity activity){
        List<Uri> imageCollection= new ArrayList<>();
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
                "%"+ "TestFolder" + File.separator+ "1" + "%"
        };
        ContentResolver resolver = activity.getContentResolver();
        //Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null, null, null, null );
        Cursor cursor = resolver.query(
                collection,projection,selection,selectionArgs,null
        );
        if(cursor != null){
            //Log.d("Sec_Act", "Cursor is not null, size is: " + cursor.getCount());
            int size = cursor.getCount();
            if(size!=0)
            {
                for(int i = 0;i<size;i++)
                {
                    cursor.moveToPosition(i);
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    imageCollection.add(imageUri);
                    Log.d("Sec_Act", "getImage: " + imageUri.getPath());
                }
                return imageCollection;
            }else{
                Toast.makeText(activity, "Image Folder Empty", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    public static void deletePhotos(Activity activity){
        Uri collection;
        int size;

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
        ContentResolver resolver = activity.getContentResolver();
        //Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null, null, null, null );
        Cursor cursor = resolver.query(
                collection,projection,selection,selectionArgs,null
        );
        if(cursor != null){
            size = cursor.getCount();
            if(size!=0)
            {
                for(int i = 0;i<size;i++)
                {
                    cursor.moveToPosition(i);
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    Log.d("Sec_Act", "getImage: " + imageUri.getPath());
                    resolver.delete(imageUri, null, null);
                }
                Toast.makeText(activity, "Images Deleted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, "Image Folder Empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
