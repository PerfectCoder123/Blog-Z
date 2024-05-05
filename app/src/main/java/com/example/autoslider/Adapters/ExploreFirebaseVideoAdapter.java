package com.example.autoslider.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.MainActivity;
import com.example.autoslider.Models.ExploreVideoModel;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExploreFirebaseVideoAdapter extends FirebaseRecyclerAdapter<ExploreVideoModel,ExploreFirebaseVideoAdapter.holder> {
    FirebaseAuth auth;
    Context context;

    public ExploreFirebaseVideoAdapter(@NonNull FirebaseRecyclerOptions<ExploreVideoModel> options) {
        super(options);
        auth  = FirebaseAuth.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull ExploreVideoModel model) {
        ArrayList<IdModel> likeList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("minies").child(model.getVideoId()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             for( DataSnapshot snap : snapshot.getChildren()){
                 IdModel id = (IdModel)snap.getValue(IdModel.class);
                 likeList.add(id);
                 if(id.getPostId().equals(auth.getCurrentUser().getUid())) holder.like.setImageResource(R.drawable.bluelike);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = -1;
                if( (pos = isLiked(likeList)) != -1) {
                    holder.like.setImageResource(R.drawable.like);
                    FirebaseDatabase.getInstance().getReference("minies").child(model.getVideoId()).child("likes").child(auth.getCurrentUser().getUid()).setValue(null);
                    int like = Integer.parseInt(holder.likecount.getText().toString()) - 1;
                    holder.likecount.setText(like +"" );
                    FirebaseDatabase.getInstance().getReference("minies").child(model.getVideoId()).child("likeCount").setValue(like + "");
                    likeList.remove(pos);
                }
                else {
                    holder.like.setImageResource(R.drawable.bluelike);
                    int like = Integer.parseInt(holder.likecount.getText().toString()) + 1;
                    holder.likecount.setText(like +"" );
                    FirebaseDatabase.getInstance().getReference("minies").child(model.getVideoId()).child("likeCount").setValue(like + "");
                    IdModel idModel = new IdModel(auth.getCurrentUser().getUid());
                    likeList.add(idModel);

                    FirebaseDatabase.getInstance().getReference("minies").child(model.getVideoId()).child("likes").child(auth.getCurrentUser().getUid()).setValue(idModel);
                }

            }
        });
        holder.like.setImageResource(R.drawable.like);
        holder.description.setText(model.getDescription());
        holder.title.setText(model.getTitle());
        holder.videoView.setVideoPath(model.getVideoUrl());
        holder.likecount.setText(model.getLikeCount());
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                holder.bar.setVisibility(View.INVISIBLE);
                mp.start();
            }
        });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.videoView.isPlaying()) {
                    holder.videoView.pause();
                    holder.playbtn.setImageResource(R.drawable.pause);
                    holder.cardView.setVisibility(View.VISIBLE);

                }
                else {
                    holder.videoView.start();
                    holder.playbtn.setImageResource(R.drawable.play);
                    holder.cardView.setVisibility(View.VISIBLE);
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.cardView.setVisibility(View.INVISIBLE);
                    }
                }, 1500);

            }
        });

        holder.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });
    }

    public int isLiked(ArrayList<IdModel> likeList){
        for(int i= 0 ;i< likeList.size();i++) if(likeList.get(i).getPostId().equals(auth.getUid())) return i;
        return -1;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_single_row_layout,parent,false);
        return  new holder(view);
    }

    public  class  holder extends RecyclerView.ViewHolder{
        VideoView videoView;
        TextView title,description,likecount;
        ProgressBar bar;
        ImageView playbtn;
        CardView cardView;
        ImageView like,backbtn;
        ConstraintLayout view;
        public holder(@NonNull View itemView) {
            super(itemView);
            likecount = itemView.findViewById(R.id.explore_singlerow_likecount);
            like = itemView.findViewById(R.id.explore_singlerow_likebtn);
            videoView = itemView.findViewById(R.id.explore_videoview);
            title = itemView.findViewById(R.id.explore_video_title);
            description =itemView.findViewById(R.id.explore_video_desc);
            bar  =itemView.findViewById(R.id.explore_bar);
            view = itemView.findViewById(R.id.ecplore_single_row_view);
            playbtn = itemView.findViewById(R.id.explore_video_playbtn);
            cardView = itemView.findViewById(R.id.explore_video_cplaybtn);
            backbtn = itemView.findViewById(R.id.explore_single_row_backbtn);
            context = itemView.getContext();
        }
    }
}
