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
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 10 ;
    private static final int MY_PERMISSIONS_REQUEST_READ = 9;
    ImageView mImageview;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageview=findViewById(R.id.imageView);
        queue = Volley.newRequestQueue(this);
        checkReadStoragePerm(this);
        checkWriteStoragePerm(this);
    }

    public boolean checkReadStoragePerm(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Read Permission")
                        .setMessage("Allow us")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
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
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ);
            Toast.makeText(this, "Read External Storage enabled", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public boolean checkWriteStoragePerm(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Read Permission")
                        .setMessage("Allow us")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
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
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE);
            Toast.makeText(this, "Write External Storage enabled", Toast.LENGTH_SHORT).show();
            return true;
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

    private void saveImageToGallery(Bitmap bitmap) {
        OutputStream fos;
        try{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_1.jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ File.separator+"TestFolder");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, fos);
                Objects.requireNonNull(fos);
            }else{
                String ImagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                File image = new File(ImagesDir,"Image_1.jpg" );
                fos = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, fos);
            }
            Toast.makeText(this,"Image Saved", Toast.LENGTH_SHORT).show();
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

    public void deleteImage(View view) {
        Uri collection;
        int size;
        int index=0;
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
                Toast.makeText(this, "Images Deleted", Toast.LENGTH_SHORT).show();
                //return imageUri;
            }else{
                Toast.makeText(this, "Image Folder Empty", Toast.LENGTH_SHORT).show();
            }
        }
        //return null;
    }
}