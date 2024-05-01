package com.example.autoslider.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autoslider.Icons;
import com.example.autoslider.MainActivity;
import com.example.autoslider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.holder> {
    ArrayList<Icons> userdata;
    Context context;
    public IconAdapter(ArrayList<Icons> userdata, Context context) {
        this.userdata = userdata;
        this.context = context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.profile_icon_design,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.name.setText(userdata.get(position).name);
        Glide.with(context).load(userdata.get(position).url).into(holder.image);
        Icons icons = userdata.get(position);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profileImage").setValue(icons.url);
               
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("key","profile");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return userdata.size();
    }


    public  class  holder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        public holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.profile_icon_design_name);
            image =  itemView.findViewById(R.id.profile_icon_design_image);

        }
    }

}
