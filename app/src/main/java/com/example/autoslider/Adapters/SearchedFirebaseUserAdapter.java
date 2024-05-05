package com.example.autoslider.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.MainActivity;
import com.example.autoslider.Models.FriendsModel;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedFirebaseUserAdapter extends FirebaseRecyclerAdapter<ModelUserReal,SearchedFirebaseUserAdapter.holder> {
    String keyword;
    ArrayList<FriendsModel> friendsList;

    public SearchedFirebaseUserAdapter(@NonNull FirebaseRecyclerOptions<ModelUserReal> options,String keyword) {
        super(options);
        this.keyword = keyword;

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<FriendsModel> list = new ArrayList<>();
                for(DataSnapshot snap : snapshot.getChildren()){
                    FriendsModel friendsModel = snap.getValue(FriendsModel.class);
                    list.add(friendsModel);
                }
                friendsList = list;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull ModelUserReal model) {

        if( !model.getUsername().toLowerCase().contains(keyword.toLowerCase()) || model.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.userImage.setVisibility(View.GONE);
            holder.bio.setVisibility(View.GONE);
            holder.username.setVisibility(View.GONE);
            holder.followbtn.setVisibility(View.GONE);
            holder.card.setVisibility(View.GONE);
        }
        else{

            Picasso.get().load(model.getProfileImage()).into(holder.userImage);
            holder.bio.setText(model.getBio());
            holder.username.setText(model.getUsername());

            if(isFollower(model.getUserId())) {
                holder.followbtn.setBackgroundColor(Color.BLACK);
                holder.followbtn.setTextColor(Color.WHITE);
                holder.followbtn.setText("followed");
                holder.followbtn.setCompoundDrawables(null, null, null, null);
            }
            else{
                holder.followbtn.setBackgroundResource(R.drawable.btn_background_black);
                holder.followbtn.setText("follow");
                holder.followbtn.setTextColor(Color.BLACK);
                holder.followbtn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.plus,0);
            }

            holder.followbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFollower(model.getUserId())) {
                        holder.followbtn.setBackgroundResource(R.drawable.btn_background_black);
                        holder.followbtn.setText("follow");
                        holder.followbtn.setTextColor(Color.BLACK);
                        holder.followbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus, 0);
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers").child(model.getUserId()).setValue(null);
                        for (int i = 0; i < friendsList.size(); i++) {
                            if (friendsList.get(i).getUserId().equals(model.getUserId())) {
                                friendsList.remove(i);
                            }
                        }
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followerCount").setValue((friendsList.size()) + "");
                    }
                    else{
                        holder.followbtn.setBackgroundColor(Color.BLACK);
                        holder.followbtn.setTextColor(Color.WHITE);
                        holder.followbtn.setText("followed");
                        holder.followbtn.setCompoundDrawables(null, null, null, null);

                        FriendsModel model1 = new FriendsModel();
                        model1.setUserId(model.getUserId());
                        friendsList.add(model1);
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers").child(model.getUserId()).setValue(model1);
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followerCount").setValue((friendsList.size()) + "");
                    }
                }
            });
        }

        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("key",model.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(v.getContext(), model.getUserId(), Toast.LENGTH_SHORT).show();
                v.getContext().startActivity(intent);

            }
        });
    }

    public boolean isFollower(String userId){

        for(int i = 0 ; i< friendsList.size();i++){
            if(friendsList.get(i).getUserId().equals(userId))return true;
        }
        return false;
    }
    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.search_single_layout,parent,false);
        return new holder(view);
    }

    class holder extends RecyclerView.ViewHolder{
        CardView card ;
        CircleImageView userImage;
        TextView username,bio,followbtn;
        public holder(@NonNull View itemView) {
            super(itemView);
            userImage = (CircleImageView) itemView.findViewById(R.id.search_singlerow_userimage);
            username =(TextView) itemView.findViewById(R.id.search_singlerow_username);
            bio = (TextView)itemView.findViewById(R.id.search_singlerow_bio);
            followbtn = (TextView)itemView.findViewById(R.id.search_singlerow_followbtn);
            card = itemView.findViewById(R.id.search_single_layout_cardview);
        }
    }
}
