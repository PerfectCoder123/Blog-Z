package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.Adapters.CategoryViewAdapter;
import com.example.autoslider.Models.CategoryViewModel;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.Models.ModelPost;
import com.example.autoslider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewFragment extends Fragment {
    TextView blogCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View  view = inflater.inflate(R.layout.fragment_category_view, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.category_view_rv);
        ImageView backbtn = view.findViewById(R.id.category_view_back);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        List<CategoryViewModel> list = new ArrayList<>();
        List<String> l = (List<String>) getArguments().get("category_posts");
        blogCount = view.findViewById(R.id.category_view_blog_count);
        blogCount.setText(l.size() + " blogs on " + getArguments().get("category_type"));
        ArrayList<IdModel> bookmarkList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    String key = snap.getKey();
                    if(l.contains(key)){
                        ModelPost post = snap.getValue(ModelPost.class);
                        CategoryViewModel categoryViewModel = new CategoryViewModel(post.getPostImage(), post.getTitlepeek(), post.getDate(), post.getPostId());
                        list.add(categoryViewModel);
                    }
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bookmark").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap : snapshot.getChildren()){
                            IdModel id = snap.getValue(IdModel.class);
                            bookmarkList.add(id);
                        }
                        recyclerView.setAdapter(new CategoryViewAdapter(list , bookmarkList));
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

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.explore_frame, new CategoryFragment());
                transaction.commit();
            }
        });

        return view;
    }
}