package com.example.chattgpt;

import static com.squareup.picasso.Picasso.*;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class useradapter extends RecyclerView.Adapter<useradapter.viewholder> {
    MainActivity mainActivity;
    ArrayList<users> userarraylist;

    public useradapter(MainActivity mainActivity, ArrayList<users> userarraylist) {
        this.mainActivity = mainActivity;
        this.userarraylist = userarraylist;
    }

    @NonNull
    @Override
    public useradapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item,parent,false);

        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull useradapter.viewholder holder, int position) {
   users users = userarraylist.get(position);
   holder.username.setText(users.userName);
   holder.userstatus.setText(users.status);
   Picasso.get().load(users.profilepic).into(holder.userimg);

   holder.itemView.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           Intent intent = new Intent(mainActivity, chatWin.class);
           intent.putExtra("nameeee",users.getUserName());
           intent.putExtra("reciverImg",users.getProfilepic());
           intent.putExtra("uid",users.getUserId());
           mainActivity.startActivity(intent);
       }
   });



    }

    @Override
    public int getItemCount() {
        return userarraylist.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView username;
        TextView userstatus;
        CircleImageView userimg;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
            userimg = itemView.findViewById(R.id.userimg);
        }
    }
}
