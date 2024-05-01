package com.example.autoslider.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
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

public class ProfileAllPostAdapter extends FirebaseRecyclerAdapter<IdModel,ProfileAllPostAdapter.holder> {

    Context context;
    public ProfileAllPostAdapter(@NonNull FirebaseRecyclerOptions<IdModel> options,Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull IdModel model) {

        FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPost post = snapshot.getValue(ModelPost.class);
                Picasso.get().load(post.getPostImage()).into(holder.image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      holder.image.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(context, MainBlogReading.class);
              intent.putExtra("postId",model.getPostId());
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              v.getContext().startActivity(intent);
              Toast.makeText(v.getContext(), model.getPostId(), Toast.LENGTH_SHORT).show();
          }
      });
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.allpost_grid,parent,false);
        context = view.getContext();
        return new holder(view);
    }

    public class holder extends RecyclerView.ViewHolder{
        ImageView image;
        public holder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_all_image);
        }
    }
}
