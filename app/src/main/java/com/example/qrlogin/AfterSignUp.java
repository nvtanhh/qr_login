package com.example.qrlogin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qrlogin.services.DBService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AfterSignUp extends AppCompatActivity implements View.OnClickListener {

    ImageView imageQRcode;
    View download;
    Button gobackBtn;
    Bitmap bitmap;

    private static final int STORAGE_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_sign_up);
        initView();

    }

    private void initView() {
        setImageQRCode();
        download = findViewById(R.id.save_qr_code);
        download.setOnClickListener(this);
        gobackBtn = findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(this);
        download.setOnClickListener(this);
    }

    private void setImageQRCode() {
        String code = (String) getIntent().getSerializableExtra("code");
        imageQRcode = findViewById(R.id.img_qr_code);
        QRCodeWriter writer = new QRCodeWriter();
        try {
            assert code != null;
            BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imageQRcode.setImageBitmap(bitmap);
            System.out.println("Change image done");
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_back:
                startActivity(new Intent(AfterSignUp.this, MainActivity.class));
                break;
            case R.id.save_qr_code:
                saveImageToDevice();
                break;
        }
    }

    private void saveImageToDevice() {
        verifyStoragePermissions(this);
        if (checkPermission()) {
            System.out.println("start save to device");
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath(), "QRLOGIN");
            if (!dir.exists())
                dir.mkdirs();
            System.out.println("dir:  " + dir.getAbsolutePath() + "==>" + dir.exists());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
            String date = simpleDateFormat.format(new Date());
            String name = "qrcode_" + date + ".jpeg";
            File file = new File(dir, name);

            System.out.println(file.getAbsolutePath());

            if (!file.exists()) {
                Log.d("path", file.toString());
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    System.out.println("Save image successfully!");
                    Toast.makeText(AfterSignUp.this, "Save image successfully!", Toast.LENGTH_LONG).show();
                    fos.flush();
                    fos.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            refreshGallery(dir);
        }
    }
//
//    private void requestPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }
//    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(
                AfterSignUp.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    private void refreshGallery(File file) {
        Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        i.setData(Uri.fromFile(file));
        sendBroadcast(i);
    }
}