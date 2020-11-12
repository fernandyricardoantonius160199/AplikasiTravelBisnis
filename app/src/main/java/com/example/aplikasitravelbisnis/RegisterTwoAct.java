package com.example.aplikasitravelbisnis;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.sql.DatabaseMetaData;

public class RegisterTwoAct extends AppCompatActivity {
    Button btn_continue,btn_add_photo;
    ImageView pic_photo_register_user;
    ImageButton btn_back;
    EditText nama_lengkap, bio;

    Uri photo_location;
    Integer photo_max = 1;

    DatabaseReference reference;
    StorageReference storage;

    String USERNAME_KEY = "usernamekey";
    String username_key ="";
    String username_key_new ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);

        getUsernameLocal();
        pic_photo_register_user = findViewById(R.id.pic_photo_register_user);
        btn_add_photo = findViewById(R.id.upload_photo);
        btn_continue = findViewById(R.id.btn_continue);

        nama_lengkap = findViewById(R.id.nama_lengkap);
        bio = findViewById(R.id.bio);
        btn_back = findViewById(R.id.back);

        btn_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPhoto();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtoprev = new Intent(RegisterTwoAct.this, RegisterAct.class);
                startActivity(backtoprev);
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(username_key_new);
                storage = FirebaseStorage.getInstance().getReference().child("Photousers")
                        .child(username_key_new);

                //validasi untuk file (apakah ada?)
                if (photo_location != null) {
                    StorageReference storageReference1 =
                            storage.child(System.currentTimeMillis() + "." +
                                    getFileExtension(photo_location));
                    storageReference1.putFile(photo_location)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    String uri_photo = taskSnapshot.getDownloadUrl().toString();
                                    reference.getRef().child("url_photo_profile").setValue(uri_photo);
                                    reference.getRef().child("nama_lengkap").setValue(nama_lengkap.getText().toString());
                                    reference.getRef().child("bio").setValue(bio.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    Intent gotosuccess = new Intent(RegisterTwoAct.this, LoginAct.class);
                                    startActivity(gotosuccess);
                                }
                            });
                }
            }
        });
    }
            String getFileExtension(Uri uri) {
                ContentResolver contentResolver = getContentResolver();
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
            }

            public void findPhoto() {
                Intent pic = new Intent();
                pic.setType("image/*");
                pic.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(pic, photo_max);
            }

            private void getUsernameLocal() {
                SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                username_key_new = sharedPreferences.getString(username_key, "");
            }

            //kita membutuhkan library picaso untuk menampilkan gambar menambahkan di gradle app
            //membuat activity baru dengan cara Generate, (klik kanan, generate)

            @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode,data);
                if (requestCode == photo_max && resultCode == RESULT_OK && data.getData() != null) {
                    photo_location = data.getData();
                    //sebelum menggunakan Picasso kita tambahkan dulu scrip string getfile extension diatas function find photo
                    //kita rubah versi picasso menjadi 2.5.2 yg digunakan untuk mengupload file baru kedalam firebase di gradle app
                    Picasso.with(this).load(photo_location).centerCrop().fit().into(pic_photo_register_user);
                }
//silahkan dicoba untuk melihat dan dapat mengambil d=gambar dan menampilkan gaambar di imageview
            }
        }