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
import com.example.autoslider.Adapters.SearchedFirebaseUserAdapter;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class SearchedUserFragment extends Fragment {
    String keyword;
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    SearchedFirebaseUserAdapter searchedFirebaseUserAdapter;
    FirebaseRecyclerOptions<ModelUserReal> options;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScreenSizeUtilityClass.fullWhiteScreen(getActivity());
        View view = inflater.inflate(R.layout.search_layout_search_result,container,false);
        shimmerFrameLayout = view.findViewById(R.id.search_result_shimmer);
        shimmerFrameLayout.startShimmer();
        recyclerView = view.findViewById(R.id.search_layout_searchresult_rc);
        keyword = (String) getArguments().getString("keyword");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

         options = new FirebaseRecyclerOptions.Builder<ModelUserReal>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users"), ModelUserReal.class)
                        .build();
        recyclerView.setNestedScrollingEnabled(false);
        searchedFirebaseUserAdapter = new SearchedFirebaseUserAdapter(options,keyword);
        recyclerView.setAdapter(searchedFirebaseUserAdapter);

        shimmerChanger();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        searchedFirebaseUserAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        searchedFirebaseUserAdapter.stopListening();
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
