package com.example.autoslider.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.Adapters.FirebaseFriendsAdapter;
import com.example.autoslider.Adapters.ProfileAllPostAdapter;
import com.example.autoslider.MainActivity;
import com.example.autoslider.Models.FriendsModel;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;
import com.example.autoslider.UserVerification;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
  RecyclerView friend_rv,posts_rv;
  TextView username,bio,trendingcount,postcount,followercount;
  ImageView profileImage,navbar;
  FirebaseAuth auth;
  ImageView updateImage;
  FirebaseDatabase database;
  FirebaseFriendsAdapter firebaseFriendsAdapter;
  ProfileAllPostAdapter profileAllPostAdapter;
  ModelUserReal userReal;
  ProgressBar bar;
  ImageView backbtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScreenSizeUtilityClass.fullWhiteScreen(getActivity());
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        friend_rv = (RecyclerView)view.findViewById(R.id.Friends_rv);
        profileImage = view.findViewById(R.id.profile_layout_profileimage);
        username = view.findViewById(R.id.profile_layout_profile_name);
        bio = view.findViewById(R.id.profile_layout_profile_bio);
        posts_rv = view.findViewById(R.id.profile_layout_rv);
        bar = view.findViewById(R.id.profile_layout_bar);
        backbtn = view.findViewById(R.id.profile_backbtn);
        navbar = view.findViewById(R.id.profile_layout_navbar);
        updateImage = view.findViewById(R.id.profile_layout_imagechange);
        trendingcount = view.findViewById(R.id.profile_layout_profile_trendingcount);
        postcount = view.findViewById(R.id.profile_layout_profile_postcount);
        followercount = view.findViewById(R.id.profile_layout_profile_followercount);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        userReal = new ModelUserReal();
        String id;


        if(getActivity().getIntent().getStringExtra("key") != null && !getActivity().getIntent().getStringExtra("key").equals("profile")) {
            id = getActivity().getIntent().getStringExtra("key");
            getActivity().getIntent().removeExtra("key");
            updateImage.setVisibility(View.GONE);
            navbar.setVisibility(View.GONE);
        }
        else id = auth.getCurrentUser().getUid();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        database.getReference().child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userReal = snapshot.getValue(ModelUserReal.class);
                Picasso.get().load(userReal.getProfileImage()).placeholder(R.drawable.placehoder_profile).into(profileImage);
                postcount.setText(userReal.getPostCount());
                trendingcount.setText(userReal.getTrendingCount());
                followercount.setText(userReal.getFollowerCount());
                username.setText(userReal.getUsername());
                bio.setText(userReal.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friend_rv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        FirebaseRecyclerOptions<FriendsModel> options =
                new FirebaseRecyclerOptions.Builder<FriendsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(id).child("followers"), FriendsModel.class)
                        .build();
        ViewCompat.setNestedScrollingEnabled(friend_rv, false);
        firebaseFriendsAdapter = new FirebaseFriendsAdapter(options);
        friend_rv.setAdapter(firebaseFriendsAdapter);


      

        posts_rv.setLayoutManager(new GridLayoutManager(getContext(),3));

        FirebaseRecyclerOptions<IdModel> options1 =
                new FirebaseRecyclerOptions.Builder<IdModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(id).child("posts"), IdModel.class)
                        .build();
        ViewCompat.setNestedScrollingEnabled(posts_rv, false);
        profileAllPostAdapter = new ProfileAllPostAdapter(options1,getContext());
        posts_rv.setAdapter(profileAllPostAdapter);

        navbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });



        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.explore_frame, new IconFragement());
                transaction.commit();
            }
        });

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseFriendsAdapter.startListening();
        profileAllPostAdapter.startListening();

    }
    @Override
    public void onStop() {
        super.onStop();
        firebaseFriendsAdapter.stopListening();
        profileAllPostAdapter.stopListening();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.navbar_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_bar_edit:
                        transaction.replace(R.id.explore_frame, new EditProfileFragment());
                        transaction.commit();
                        break;
                    case R.id.bookmark:
                        transaction.replace(R.id.explore_frame, new BookmarkFragment());
                        transaction.commit();
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(), UserVerification.class);
                        intent.putExtra("splash", "notnull");
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });

    }

}