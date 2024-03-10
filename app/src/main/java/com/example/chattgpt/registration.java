package com.example.chattgpt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {
    Button signupbutton;
    TextView loginbut;
    EditText rgrepassword, rgpassword, rgemail, rgusername;
    CircleImageView rgprofileImg;
    FirebaseAuth auth;
    Uri imageURI;
    String imageuri;
    FirebaseStorage storage;
    FirebaseDatabase database;
    android.app.ProgressDialog progressDialog;

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Establishing ur account");
        progressDialog.setCancelable(false);

        loginbut = findViewById(R.id.loginbut);
        signupbutton = findViewById(R.id.signupbutton);
        rgrepassword = findViewById(R.id.rgrepassword);
        rgpassword = findViewById(R.id.rgpassword);
        rgemail = findViewById(R.id.rgemail);
        rgusername = findViewById(R.id.rgusername);
        rgprofileImg = findViewById(R.id.profilerg0);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registration.this, login.class);
                startActivity(intent);
                finish();
            }
        });

        rgprofileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);

            }
        });
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = rgusername.getText().toString();
                String email = rgemail.getText().toString();
                String password = rgpassword.getText().toString();
                String cpassword = rgrepassword.getText().toString();
                String status = "Hey I am using this application";


                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                        || TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)) {
                    progressDialog.dismiss();
                    Toast.makeText(registration.this, "Please enter valid information", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailpattern)) {
                    progressDialog.dismiss();
                    rgemail.setError("Type valid email here");
                } else if (password.length() < 6) {
                    progressDialog.dismiss();
                    rgpassword.setError("password must be 6 character");
                } else if (!password.equals(cpassword)) {
                    progressDialog.dismiss();
                    rgpassword.setError("the password doent match");
                } else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("upload").child(id);
                                if (imageURI != null) {
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri = uri.toString();

                                                        users users = new users(id, name, email, password,  imageuri,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.show();
                                                                    Intent intent = new Intent(registration.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(registration.this, "Error in creating user", Toast.LENGTH_SHORT).show();
                                                                }


                                                            }
                                                        });
                                                    }
                                                });
                                            }

                                        }
                                    });
                                } else {
                                    String status = "Hey I am Using this application";
                                    imageuri = "https://firebasestorage.googleapis.com/v0/b/chattgpt-94ef0.appspot.com/o/IMG-20220102-WA0006.jpg?alt=media&token=7e6f5c62-3358-4e1a-8b98-5309f0eeb1fe";
                                    users users = new users(id, name, email, password, imageuri, status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(registration.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(registration.this, "Error in creating user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                imageURI = data.getData();
                rgprofileImg.setImageURI(imageURI);
                }

            }
        }
    }
