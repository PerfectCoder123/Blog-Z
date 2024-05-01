package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.autoslider.Adapters.ExploreFirebaseVideoAdapter;
import com.example.autoslider.Models.ExploreVideoModel;
import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ExploreVideoFragment extends Fragment {
    ViewPager2 viewPager2;
    private int currentApiVersion;
    ExploreFirebaseVideoAdapter firebaseVideoAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        ScreenSizeUtilityClass.fullScreen(getActivity());

        View view = inflater.inflate(R.layout.explore_video_layout,container,false);
        viewPager2  = view.findViewById(R.id.explore_layout_viewpager);


        FirebaseRecyclerOptions<ExploreVideoModel> options1 =
                new FirebaseRecyclerOptions.Builder<ExploreVideoModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("minies"), ExploreVideoModel.class)
                        .build();
        ViewCompat.setNestedScrollingEnabled(viewPager2, false);
        firebaseVideoAdapter = new ExploreFirebaseVideoAdapter(options1);
        viewPager2.setAdapter(firebaseVideoAdapter);


        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseVideoAdapter.startListening();

    }
    @Override
    public void onStop() {
        super.onStop();
        firebaseVideoAdapter.stopListening();

    }
}
