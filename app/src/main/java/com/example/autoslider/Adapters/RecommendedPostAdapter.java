package com.example.autoslider.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.autoslider.MainActivity;
import com.example.autoslider.MainBlogReading;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.Models.ModelPost;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;


public class RecommendedPostAdapter extends RecyclerView.Adapter<RecommendedPostAdapter.holder> {
    Context context;
    List<IdModel> bookmarkList;
    List<ModelPost>modelPosts;

    public RecommendedPostAdapter(Context context, List<ModelPost> modelPosts) {
        this.context = context;
        this.modelPosts = modelPosts;
        bookmarkList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bookmark").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    IdModel id = snap.getValue(IdModel.class);
                    bookmarkList.add(id);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design,parent,false);
        return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        ModelPost model = modelPosts.get(position);
        FirebaseDatabase.getInstance().getReference().child("users").child(model.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUserReal user = snapshot.getValue(ModelUserReal.class);
                holder.name.setText(user.getUsername());
                Picasso.get().load(user.getProfileImage()).into(holder.profile);
                holder.bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(isBookmarked(model.getPostId())) holder.bookmark.setImageResource(R.drawable.bookmarkblack);

        holder.date.setText(model.getDate());
        holder.title.setText(model.getTitlepeek());
        holder.tags.setText(model.getTags());
        holder.location.setText(model.getLocation());
        holder.time.setText(model.getTime());
        Picasso.get().load(model.getPostImage()).placeholder(R.drawable.nature).into(holder.blogimg);
        holder.blogimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainBlogReading.class);
                intent.putExtra("postId",model.getPostId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);

            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("key",model.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isBookmarked(model.getPostId())){
                    holder.bookmark.setImageResource(R.drawable.bookmarkwhite);
                    Toast.makeText(v.getContext(), "removed bookmark", Toast.LENGTH_SHORT).show();
                    for(int i = 0 ; i< bookmarkList.size(); i++){
                        if(bookmarkList.get(i).getPostId().equals(model.getPostId())) bookmarkList.remove(i);
                    }
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("bookmark").child(model.getPostId()).setValue(null);
                }
                else {
                    holder.bookmark.setImageResource(R.drawable.bookmarkblack);
                    Toast.makeText(v.getContext(), "post bookmarked", Toast.LENGTH_SHORT).show();
                    IdModel id = new IdModel(model.getPostId());
                    bookmarkList.add(id);
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("bookmark").child(model.getPostId()).setValue(id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelPosts.size();
    }

    public boolean isBookmarked(String id){
        for(IdModel idmodel : bookmarkList){
            if(idmodel.getPostId().equals(id)) return true;
        }
        return false;
    }

    class holder extends RecyclerView.ViewHolder{
        ImageView profile,blogimg,bookmark;
        TextView title,date,time,name,bio,tags,location;
        public holder(@NonNull View itemView) {
            super(itemView);
            tags = itemView.findViewById(R.id.post_design_tags);
            profile = itemView.findViewById(R.id.profile_image4);
            blogimg = itemView.findViewById(R.id.post_image_homepage);
            title = itemView.findViewById(R.id.titlepeek);
            name = itemView.findViewById(R.id.Profile_name_homepage);
            bio = itemView.findViewById(R.id.profile_bio_hoomepage);
            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location_homepage);
            time = itemView.findViewById(R.id.time);
            bookmark = itemView.findViewById(R.id.post_design_bookmark);
        }
    }

}
