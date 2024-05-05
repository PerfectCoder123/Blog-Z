package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.autoslider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileFragment extends Fragment {

    TextView username,bio,save;
    ImageView change;
    DatabaseReference database;
    ImageView backbtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_profile,container,false);
        database =  FirebaseDatabase.getInstance().getReference();
        username = view.findViewById(R.id.edit_layout_username);
        bio = view.findViewById(R.id.edit_layout_bio);
        save = view.findViewById(R.id.edit_layout_savebtn);
        change = view.findViewById(R.id.edit_layout_change);
        backbtn = view.findViewById(R.id.editprofile_backbtn);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.explore_frame, new IconFragement());
                transaction.commit();
            }
        });

      save.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(bio.getText().toString().equals("") || username.getText().toString().equals(""))
                  Toast.makeText(getContext(), "field missing", Toast.LENGTH_SHORT).show();
              else{
                  database.child("users").child(FirebaseAuth.getInstance().getUid()).child("bio").setValue(bio.getText().toString());
                  database.child("users").child(FirebaseAuth.getInstance().getUid()).child("username").setValue(username.getText().toString());
                  FragmentManager manager = getParentFragmentManager();
                  FragmentTransaction transaction = manager.beginTransaction();
                  transaction.replace(R.id.explore_frame, new ProfileFragment());
                  transaction.commit();
              }
          }
      });

      backbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              FragmentManager manager = getParentFragmentManager();
              FragmentTransaction transaction = manager.beginTransaction();
              transaction.replace(R.id.explore_frame,new ProfileFragment());
              transaction.commit();
          }
      });

        return  view;
    }
}
