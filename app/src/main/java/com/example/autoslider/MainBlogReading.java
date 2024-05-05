package com.example.autoslider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.Models.ModelPost;
import com.example.autoslider.Models.ModelUserReal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainBlogReading extends AppCompatActivity {
     TextView description,title,date,profilename,location,viewCount;
     ImageView authorimage,postimage,bookmark;
     ArrayList<IdModel> bookmarkList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.bloglayout);

        title = findViewById(R.id.bloglayout_title);
        description = findViewById(R.id.description);
        date = findViewById(R.id.bloglayout_date);
        viewCount = findViewById(R.id.bloglayout_view);
        authorimage = findViewById(R.id.profileimagemain);
        postimage = findViewById(R.id.bloglayout_blogimage);
        profilename = findViewById(R.id.bloglayout_profilename);
        location= findViewById(R.id.bloglayout_location);
        bookmark = findViewById(R.id.bloglayout_bookmark);
        bookmarkList = new ArrayList<>();

        IdModel idModel = new IdModel(getIntent().getStringExtra("postId"));
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("history").child(idModel.getPostId()).setValue(idModel);

        FirebaseDatabase.getInstance().getReference().child("posts").child(getIntent().getStringExtra("postId")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPost modelPost = snapshot.getValue(ModelPost.class);
                location.setText(modelPost.getLocation());
                description.setText(modelPost.getDescription());
                date.setText(modelPost.getDate());
                title.setText(modelPost.getTitlepeek());
                FirebaseDatabase.getInstance().getReference().child("posts").child(modelPost.getPostId()).child("views").setValue(modelPost.getViews() + 1);
                viewCount.setText(modelPost.getViews() + 1 + "");
                Glide.with(getApplicationContext()).load(modelPost.getPostImage()).into(postimage);
                FirebaseDatabase.getInstance().getReference().child("users").child(modelPost.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUserReal user = snapshot.getValue(ModelUserReal.class);
                        profilename.setText(user.getUsername());
                        Glide.with(getApplicationContext()).load(user.getProfileImage()).into(authorimage);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bookmark").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    IdModel id = snap.getValue(IdModel.class);
                    bookmarkList.add(id);
                    if(id.getPostId().equals(getIntent().getStringExtra("postId"))) bookmark.setImageResource(R.drawable.bookmarkblack);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {

           if(isBookmarked(getIntent().getStringExtra("postId"))){
               bookmark.setImageResource(R.drawable.bookmarkwhite);
               Toast.makeText(v.getContext(), "removed bookmark", Toast.LENGTH_SHORT).show();
               for(int i = 0 ; i< bookmarkList.size(); i++){
                   if(bookmarkList.get(i).getPostId().equals(getIntent().getStringExtra("postId"))) bookmarkList.remove(i);
               }
               FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("bookmark").child(getIntent().getStringExtra("postId")).setValue(null);
           }
           else {
               bookmark.setImageResource(R.drawable.bookmarkblack);
               Toast.makeText(v.getContext(), "post bookmarked", Toast.LENGTH_SHORT).show();
               IdModel id = new IdModel(getIntent().getStringExtra("postId"));
               bookmarkList.add(id);
               FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("bookmark").child(getIntent().getStringExtra("postId")).setValue(id);
           }
       }
   });
    }

    public boolean isBookmarked(String id){
        for(IdModel idmodel : bookmarkList){
            if(idmodel.getPostId().equals(id)) return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

}