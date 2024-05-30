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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Api.ApiService;
import Api.RetrofitClient;
import Api.UpLoadResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private List<ImageView> imageViews; // Biến thành viên để lưu danh sách các ImageView
    private ActivityResultLauncher<Intent> resultLauncher;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        // Khởi tạo danh sách ImageView
        imageViews = new ArrayList<>();
        imageViews.add(findViewById(R.id.img1));
        imageViews.add(findViewById(R.id.img2));
        imageViews.add(findViewById(R.id.img3));
        imageViews.add(findViewById(R.id.img4));
        imageViews.add(findViewById(R.id.img5));
        imageViews.add(findViewById(R.id.img6));
        imageViews.add(findViewById(R.id.img7));
        imageViews.add(findViewById(R.id.img8));
        registerRs();

        Retrofit retrofit = RetrofitClient.getClient("http://10.0.2.2:8081/API/");
        apiService = retrofit.create(ApiService.class);

        btn.setOnClickListener(view -> pickImgs());
    }
    private void pickImgs() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        resultLauncher.launch(intent);
    }

    private void registerRs() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult rs) {
                        if (rs.getResultCode() == RESULT_OK && rs.getData() != null) {
                            handleImages(rs.getData());
                        } else {
                            Toast.makeText(MainActivity.this, "No images selected", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }


    // Các phương thức khác ở đây...

    private void handleImages(Intent data) {
        List<Uri> imageUris = new ArrayList<>(); // Tạo danh sách các URI của ảnh để tải lên

        if (data.getClipData() != null) {
            int count = Math.min(data.getClipData().getItemCount(), 8); // Giới hạn số lượng ảnh tối đa là 8
            for (int i = 0; i < count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                imageUris.add(imageUri); // Thêm URI của ảnh vào danh sách
                // Hiển thị ảnh từ URI vào ImageView tương ứng
                imageViews.get(i).setImageURI(imageUri);
            }
        } else if (data.getData() != null) {
            Uri imageUri = data.getData();
            imageUris.add(imageUri); // Thêm URI của ảnh vào danh sách
            // Hiển thị ảnh từ URI vào ImageView đầu tiên
            imageViews.get(0).setImageURI(imageUri);
        }

        // Gọi phương thức uploadImages() để tải lên các ảnh đã chọn
        uploadImages(imageUris);
    }



    private void uploadImages(List<Uri> imageUris) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (i < imageUris.size()) {
                Uri uri = imageUris.get(i);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    File file = new File(getCacheDir(), "image" + (i + 1) + ".jpg");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("image" + (i + 1), file.getName(), requestFile);
                    parts.add(body);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                parts.add(MultipartBody.Part.createFormData("image" + (i + 1), ""));
            }
        }

        RequestBody background = RequestBody.create(MediaType.parse("text/plain"), "background");

        Call<UpLoadResponse> call = apiService.uploadImages(
                background,
                parts.get(0), parts.get(1), parts.get(2), parts.get(3), parts.get(4), parts.get(5), parts.get(6), parts.get(7)
        );
        call.enqueue(new Callback<UpLoadResponse>() {
            @Override
            public void onResponse(Call<UpLoadResponse> call, Response<UpLoadResponse> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    System.out.println(response.toString());
                    Toast.makeText(MainActivity.this, "Upload failed:" + response.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UpLoadResponse> call, Throwable t) {
                System.out.println(t.toString());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
