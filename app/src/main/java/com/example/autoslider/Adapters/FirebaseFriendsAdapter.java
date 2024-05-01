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
import com.example.autoslider.Models.FriendsModel;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FirebaseFriendsAdapter extends FirebaseRecyclerAdapter<FriendsModel,FirebaseFriendsAdapter.holder> {
    Context context;
    public FirebaseFriendsAdapter(@NonNull FirebaseRecyclerOptions<FriendsModel> options) {
        super(options);


    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull FriendsModel model) {

        FirebaseDatabase.getInstance().getReference().child("users").child(model.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUserReal user = snapshot.getValue(ModelUserReal.class);
                Picasso.get().load(user.getProfileImage()).into(holder.friends_image);
                holder.friendName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.friends_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("key",model.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
                Toast.makeText(v.getContext(), model.getUserId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.profile_friends_layout, parent, false);
        return new holder(view);
    }

    class holder extends RecyclerView.ViewHolder {
        ImageView friends_image;
        TextView friendName;

        public holder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            friends_image = itemView.findViewById(R.id.friends_image_holder);
            friendName = itemView.findViewById(R.id.profile_singlelayout_name);
        }
    }
}