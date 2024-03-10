package com.example.chattgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {

    String reciverimg, reciverUid, reciverName, SenderUID;
    CircleImageView profile;
    EditText textmsg;
    public static String senderImg;
    public static String reciverIImg;
    CardView sendbtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    RecyclerView mmessangesAdpter;
    ArrayList<msgModelclass> messagessArrayList;
    messagesAdapter messagesAdapter;
    TextView reciverNName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        messagessArrayList = new ArrayList<>();

        mmessangesAdpter = findViewById(R.id.msgadapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mmessangesAdpter.setLayoutManager(linearLayoutManager);

        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        sendbtn = findViewById(R.id.sendbtn);
        textmsg = findViewById(R.id.textmsg);
        profile = findViewById(R.id.profileimgg);
        reciverNName = findViewById(R.id.recivername);

        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(reciverName);

        SenderUID = firebaseAuth.getUid();

        if (SenderUID != null) {
            String senderRoom = SenderUID + reciverUid;
            String reciverRoom = reciverUid + SenderUID;

            DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");

            chatreference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messagessArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                        messagessArrayList.add(messages);
                    }

                    // Sort messages by timestamp
                    Collections.sort(messagessArrayList, (o1, o2) -> Long.compare(o1.getTimeStamp(), o2.getTimeStamp()));

                    // Initialize or update adapter
                    if (messagesAdapter == null) {
                        messagesAdapter = new messagesAdapter(chatWin.this, messagessArrayList);
                        mmessangesAdpter.setAdapter(messagesAdapter);
                    } else {
                        messagesAdapter.notifyDataSetChanged();
                    }

                    // Scroll to the last message
                    mmessangesAdpter.scrollToPosition(messagesAdapter.getItemCount() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled event
                }
            });

            DatabaseReference reference = database.getReference().child("user").child(SenderUID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderImg = snapshot.child("profilepic").getValue(String.class);
                    reciverIImg = reciverimg;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled event
                }
            });

            sendbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = textmsg.getText().toString();
                    if (message.isEmpty()) {
                        Toast.makeText(chatWin.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    textmsg.setText("");
                    Date date = new Date();
                    msgModelclass messagess = new msgModelclass(message, SenderUID, date.getTime());

                    // Create a reference to the sender's room
                    DatabaseReference senderReference = database.getReference().child("chats").child(senderRoom).child("messages").push();

                    // Set the message in the sender's room
                    senderReference.setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Message sent successfully to the sender's room
                                // Now, send the message to the receiver's room
                                DatabaseReference receiverReference = database.getReference().child("chats").child(reciverRoom).child("messages").push();
                                receiverReference.setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Update messages locally and notify adapter
                                            messagessArrayList.add(messagess);
                                            messagesAdapter.notifyDataSetChanged();
                                        } else {
                                            // Handle message sending failure to receiver
                                            Toast.makeText(chatWin.this, "Failed to send message to receiver", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Handle message sending failure to sender
                                Toast.makeText(chatWin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}
