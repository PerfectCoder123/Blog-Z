package com.example.autoslider.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.MainBlogReading;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.Models.ModelPost;
import com.example.autoslider.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FIrebaseBookmarkAdapter extends FirebaseRecyclerAdapter<IdModel,FIrebaseBookmarkAdapter.holder> {

    public FIrebaseBookmarkAdapter(@NonNull FirebaseRecyclerOptions<IdModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull IdModel model) {
        FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPost modelPost = snapshot.getValue(ModelPost.class);
                Picasso.get().load(modelPost.getPostImage()).into(holder.postimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainBlogReading.class);
                intent.putExtra("postId",model.getPostId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_single_row_element,parent,false);
        return new holder(view);
    }

    public class holder extends RecyclerView.ViewHolder {
     ImageView postimage;
     public holder(@NonNull View itemView) {
         super(itemView);
         postimage = itemView.findViewById(R.id.bookmark_singlerow_roundedimage);
     }
 }
}
