package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.autoslider.Adapters.CategoryAdapter;
import com.example.autoslider.Models.CategoryModel;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFragment extends Fragment {
    ViewPager2 exploreViewpager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_category, container, false);
        exploreViewpager = view.findViewById(R.id.viewpagerex);

        List<CategoryModel> exploreModels  = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setName(categorySnapshot.child("name").getValue(String.class));
                    categoryModel.setImageUrl(categorySnapshot.child("imageUrl").getValue(String.class));

                    // Handle the nested posts object
                    Map<String, IdModel> posts = new HashMap<>();
                    for (DataSnapshot postSnapshot : categorySnapshot.child("posts").getChildren()) {
                        String postId = postSnapshot.child("postId").getValue(String.class);
                        IdModel idModel = new IdModel();
                        idModel.setPostId(postId);
                        posts.put(postSnapshot.getKey(), idModel);
                    }
                    categoryModel.setPosts(posts);

                    exploreModels.add(categoryModel);
                }
                exploreViewpager.setAdapter(new CategoryAdapter(exploreModels, getParentFragmentManager()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
        exploreViewpager.setClipToPadding(false);
        exploreViewpager.setClipChildren(false);
        exploreViewpager.setOffscreenPageLimit(3);
        exploreViewpager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer com = new CompositePageTransformer();
        com.addTransformer(new MarginPageTransformer(40));
        com.addTransformer(new ViewPager2.PageTransformer(){
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs((position));
                page.setScaleY(0.75f + r* 0.25f);
            }
        });
        exploreViewpager.setPageTransformer(com);
        return view;
    }
}