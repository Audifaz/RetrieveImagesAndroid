package com.example.retrieveandgetimages;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class ImageHelper {
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 10 ;
    private static final int MY_PERMISSIONS_REQUEST_READ = 9;
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

    public static void saveImageToGallery(Bitmap bitmap,Activity activity, int index, String subDir) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = activity.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_"+ Integer.toString(index) + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "TestFolder" + File.separator + subDir);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);
            } else {
                String ImagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + "TestFolder";
                File image = new File(ImagesDir, "Image_"+ Integer.toString(index) + ".jpg");
                //fos = new FileOutputStream(image);
                FileOutputStream out = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            Toast.makeText(activity, "Image Saved", Toast.LENGTH_SHORT).show();
        } catch (Error | FileNotFoundException e) {
            Toast.makeText(activity, "Image not Saved", Toast.LENGTH_SHORT).show();
            checkWriteStoragePerm(activity);
            checkReadStoragePerm(activity);
        }
    }

    public static boolean checkReadStoragePerm(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(activity)
                        .setTitle("Read Permission")
                        .setMessage("Allow us")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ);
                            }
                        })
                        .create()
                        .show();
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }else{
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ);
            Toast.makeText(activity, "Read External Storage enabled", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static boolean checkWriteStoragePerm(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(activity)
                        .setTitle("Write Permission")
                        .setMessage("Allow us")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE);
                            }
                        })
                        .create()
                        .show();
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }else{
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE);
            Toast.makeText(activity, "Write External Storage enabled", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

}
