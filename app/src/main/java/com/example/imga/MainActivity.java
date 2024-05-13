package com.example.imga;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    Bitmap bitmap;
    Button btn;
    ImageView img;
    String encodedImg;
    ActivityResultLauncher <Intent> resultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         btn = findViewById(R.id.button);
         img = findViewById(R.id.img);
        registerRs();
        btn.setOnClickListener(view -> pickImg());
}
    private void handleImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // Xử lý ảnh tại đây
            // Ví dụ: Thu nhỏ ảnh và hiển thị trong ImageView
            int targetWidth = img.getWidth();
            int targetHeight = img.getHeight();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
            img.setImageBitmap(scaledBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void pickImg() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerRs()
    {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult rs) {
                        try{
                            Uri imguri = rs.getData().getData();
                           handleImage(imguri);

                        }catch (Exception e)
                        {
                            Toast.makeText(MainActivity.this,"No img",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
}

