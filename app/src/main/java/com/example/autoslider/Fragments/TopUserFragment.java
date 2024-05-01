package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.Adapters.TopUserFirebaseAdapter;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class TopUserFragment extends Fragment {
    RecyclerView recyclerView;
    TopUserFirebaseAdapter topUserFirebaseAdapter;
    ShimmerFrameLayout shimmerFrameLayout;
    FirebaseRecyclerOptions<ModelUserReal> options;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ScreenSizeUtilityClass.fullWhiteScreen(getActivity());
        View view = inflater.inflate(R.layout.search_layout_recommended,container,false);
        shimmerFrameLayout = view.findViewById(R.id.search_shimmer);
        shimmerFrameLayout.startShimmer();

        recyclerView = view.findViewById(R.id.search_remomeded_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

         options = new FirebaseRecyclerOptions.Builder<ModelUserReal>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users"), ModelUserReal.class)
                        .build();
        recyclerView.setNestedScrollingEnabled(false);
        topUserFirebaseAdapter = new TopUserFirebaseAdapter(options,getContext());
        recyclerView.setAdapter(topUserFirebaseAdapter);

        shimmerChanger();

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
      topUserFirebaseAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        topUserFirebaseAdapter.stopListening();
    }
    private void shimmerChanger(){
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                if(options.getSnapshots().size() == 0){
                    handler.postDelayed(this,200);
                }else{
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        },200);

    }
}
