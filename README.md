---->grade thêm các thư viện:
 implementation("com.karumi:dexter:6.2.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
---->Line 60: trong MainActivity:  Retrofit retrofit = RetrofitClient.getClient("http://10.0.2.2:8081/API/");
---->thay bằng link đến 000webhost
---->Line 11: APiService: @POST("image.php") // thay bằng linh đến file trên host
---->trong file image.php(lấy luôn file này làm file insert cũng được.Line 19  $target_dir = "upload/"; đổi upload thành tên file lưu trữ ảnh trên host.
---->trong file mainfest: thêm :  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
----> trong db alter table để id của bảng image tự tăng






--------
Chức năng cuối:
Chọn Ảnh từ thiết bị
hiển thị và upload lên server.
chưa thể sửa xóa
