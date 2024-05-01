package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.Adapters.FIrebaseBookmarkAdapter;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class BookmarkFragment extends Fragment {
    RecyclerView bookmarkrc;
    FIrebaseBookmarkAdapter fIrebaseBookmarkAdapter;
    ImageView backbtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookmark_layout,container,false);
        bookmarkrc = view.findViewById(R.id.bookmark_rc);
        backbtn = view.findViewById(R.id.bookmark_backbtn);

        bookmarkrc.setLayoutManager(new GridLayoutManager(getContext(),2));

        FirebaseRecyclerOptions<IdModel> options =
                new FirebaseRecyclerOptions.Builder<IdModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bookmark"), IdModel.class)
                        .build();
        ViewCompat.setNestedScrollingEnabled(bookmarkrc, false);
         fIrebaseBookmarkAdapter = new FIrebaseBookmarkAdapter(options);
         bookmarkrc.setAdapter(fIrebaseBookmarkAdapter);



         backbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FragmentManager manager = getParentFragmentManager();
                 FragmentTransaction transaction = manager.beginTransaction();
                 transaction.replace(R.id.explore_frame,new ProfileFragment());
                 transaction.commit();
             }
         });
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        fIrebaseBookmarkAdapter.startListening();

    }
    @Override
    public void onStop() {
        super.onStop();
        fIrebaseBookmarkAdapter.stopListening();
    }
}
