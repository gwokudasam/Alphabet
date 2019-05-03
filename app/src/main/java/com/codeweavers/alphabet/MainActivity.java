package com.codeweavers.alphabet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    Intent intent;
    public static final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnCapture = findViewById(R.id.btn_capture);
        final Button btnUpload = findViewById(R.id.btn_upload);
        imageView = findViewById(R.id.imageView);

        enableRuntimePermission();

        btnCapture.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
    }


    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == 0x06 && resultCode == RESULT_OK) {
            final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            saveToDevice(bitmapToString(bitmap), getApplicationContext());
        } else {
            imageView.setImageBitmap(null);
        }
    }

    private void enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
            Toast.makeText(MainActivity.this, "CAMERA permission allows to Access Camera app", LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int rc, final String[] per, final int[] pResult) {
        if (rc == REQUEST_PERMISSION_CODE) {
            if (pResult.length > 0 && pResult[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted, Application can now access Camera.", LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission Canceled, Application cannot access Camera.", LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_capture:
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0x06);
                break;
            case R.id.btn_upload:
                Toast.makeText(MainActivity.this, "Uploading", LENGTH_SHORT).show();
                break;
        }
    }

    private void saveToDevice(final String imageString, final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("IMAGE", imageString);
        edit.apply();
        Toast.makeText(context, "Image saved in Shared Preferences", LENGTH_SHORT).show();
    }

    private String getImageString(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("IMAGE", "NULL");
    }

    private String bitmapToString(final Bitmap bm) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
