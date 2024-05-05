package com.example.autoslider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.Models.TrendingFirebaseModel;
import com.example.autoslider.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TrendingAdapter extends FirebaseRecyclerAdapter<TrendingFirebaseModel, TrendingAdapter.holder> {

    Context context ;
    public TrendingAdapter(@NonNull FirebaseRecyclerOptions<TrendingFirebaseModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull TrendingFirebaseModel model) {
        holder.username.setText(model.getUsername());
        Picasso.get().load(model.getPostImage()).into(holder.postImage);
        Picasso.get().load(model.getProfileImage()).into(holder.profileImage);
        TrendingFirebaseModel curmodel = model;
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> stories = new ArrayList<>();
                MyStory currentStory = null;
                try {
                        currentStory = new MyStory(
                            curmodel.getPostImage(),
                                new SimpleDateFormat("dd-M-yyyy hh:mm:ss").parse(curmodel.getDate()),"#nature"
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                stories.add(currentStory);
                new StoryView.Builder( ((AppCompatActivity) context).getSupportFragmentManager())
                        .setStoriesList(stories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(curmodel.getUsername()) // Default is Hidden
                        .setSubtitleText("Damascus") // Default is Hidden
                        .setTitleLogoUrl(curmodel.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }
                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.demiuserlayoput,parent,false);
            return  new holder(view);
    }

    class holder extends RecyclerView.ViewHolder{
        ImageView profileImage;
        ImageView postImage;
        TextView username;
        public holder(@NonNull View itemView) {
            super(itemView);
           context =  this.itemView.getContext();
            profileImage = itemView.findViewById(R.id.story_layout_profileimage);
            username = (TextView) itemView.findViewById(R.id.story_layout_username);
            postImage =(ImageView) itemView.findViewById(R.id.story_layout_postimage);
        }
    }
}
