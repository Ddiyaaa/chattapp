package com.example.chattgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    useradapter adapter;
    FirebaseDatabase database;
    ImageView imglogout;
    ImageView settingBut,camBut;

    ArrayList<users> userarraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        database = FirebaseDatabase.getInstance();

            auth = FirebaseAuth.getInstance();
             DatabaseReference reference =database.getReference().child("user");





        camBut = findViewById(R.id.camBut);
             settingBut = findViewById(R.id.settingBut);

            userarraylist = new ArrayList<>();

            reference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        users users = dataSnapshot.getValue(users.class);
                        userarraylist.add(users);

                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "Database error: " + error.getMessage());
                }
            });
        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new useradapter(MainActivity.this,userarraylist);
        mainUserRecyclerView.setAdapter(adapter);
            imglogout = findViewById(R.id.logoutimg);

            imglogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(MainActivity.this,R.style.dialoge);
                    dialog.setContentView(R.layout.dialog_layout);
                    Button no,yes;
                    yes = dialog.findViewById(R.id.yesbtn);
                    no= dialog.findViewById(R.id.nobtn);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(MainActivity.this,login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

settingBut.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, setting.class);
        startActivity(intent);
    }
});
camBut.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,10);
    }
});

            if (auth.getCurrentUser() == null) {
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);

            }




    }
}
