package com.example.autoslider.Fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.Adapters.FIrebasePostAdapter;
import com.example.autoslider.Adapters.RecommendedPostAdapter;
import com.example.autoslider.Adapters.SliderAdapter;
import com.example.autoslider.Adapters.TrendingAdapter;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.Models.ModelPost;
import com.example.autoslider.Models.SliderData;
import com.example.autoslider.Models.TrendingFirebaseModel;
import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private SliderAdapter adapter;
    private ArrayList<SliderData> sliderDataArrayList;
    private SliderView sliderView;
    RecyclerView recyclerView, recpost;
    FIrebasePostAdapter fIrebasePostAdapter;
    TrendingAdapter trendingAdapter;
    FirebaseDatabase database;
    ShimmerFrameLayout shimmerFrameLayout;
    CardView cardView;
    RecyclerView storyrc, blogrc;
    FirebaseRecyclerOptions<TrendingFirebaseModel> options1;
    FirebaseRecyclerOptions<ModelPost> options;
    ImageView backbtn, categorybtn;
    Activity myactivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myactivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ScreenSizeUtilityClass.fullWhiteScreen(myactivity);

        View view = inflater.inflate(R.layout.home, container, false);
        sliderDataArrayList = new ArrayList<>();
        cardView = view.findViewById(R.id.cardView);
        blogrc = view.findViewById(R.id.postres);
        storyrc = view.findViewById(R.id.recyclerView);
        backbtn = view.findViewById(R.id.home_backbtn);
        shimmerFrameLayout= view.findViewById(R.id.shimmerview);
        shimmerFrameLayout.startShimmer();
        categorybtn = view.findViewById(R.id.category_btn);
        sliderView =view.findViewById(R.id.imageSlider);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recpost = (RecyclerView)view.findViewById(R.id.postres) ;
        database  = FirebaseDatabase.getInstance();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myactivity.finishAffinity();
            }
        });
        categorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.explore_frame, new CategoryFragment());
                transaction.commit();
            }
        });

        fetchTrending();
        loadImages();

        // Perform database request asynchronously
        new DatabaseRequestTask().execute();

        return view;
    }

    // AsyncTask to perform database request
    private class DatabaseRequestTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            // Perform the database request in the background

            database.getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/recommendation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        recommendFetch();
                        Log.d("recommend", "Key exists in the database");
                    } else {
                        postFetch();
                        Log.d("recommend", "Key does not exist in the database");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle potential errors
                    System.out.println("Error occurred while checking key existence: " + databaseError.getMessage());
                }
            });

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Database request completed

            }
        }
    }

    private void recommendFetch() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MILLISECONDS)
                .build();

        database.getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/recommendation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> ids = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    IdModel idModel = snap.getValue(IdModel.class);
                    ids.add(idModel.getPostId());
                }

                JsonArray jsonArray = new JsonArray();
                for (String id : ids) {
                    jsonArray.add(id);
                }

                // Create a request body with the JSON array
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

                // Create a request to the recommend endpoint
                Request request = new Request.Builder()
                        .url("http://192.168.1.100:8080/recommend")
                        .post(requestBody)
                        .build();

                // Make the HTTP request asynchronously
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("modelpost", "onFailure: database failed" + e.toString() + call.toString());
                        myactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postFetch();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            // Get the JSON response body as a string
                            String responseBody = response.body().string();
                            // Create a Gson instance
                            Gson gson = new Gson();
                            // Convert the JSON response to a list of ModelPost objects
                            List<ModelPost> modelPostList = gson.fromJson(responseBody, new TypeToken<List<ModelPost>>() {}.getType());
                            // Use the modelPostList as needed
                            Log.d("modelpostlist", "onResponse: " + modelPostList.toArray());
                            myactivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recpost.setLayoutManager(new LinearLayoutManager(getContext()));
                                    RecommendedPostAdapter recommendedPostAdapter = new RecommendedPostAdapter(getContext(), modelPostList);
                                    recpost.setAdapter(recommendedPostAdapter);
                                    ViewCompat.setNestedScrollingEnabled(recpost, false);
                                    shimmerChanger();
                                }
                            });
                        } else {
                            // Handle the request failure
                            Log.d("modelpost", "onFailure: database failed");
                            myactivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    postFetch();
                                }
                            });

                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postFetch() {
        recpost.setLayoutManager(new LinearLayoutManager(getContext()));

        Query postsQuery = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("views").limitToFirst(10);

        postsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    options = new FirebaseRecyclerOptions.Builder<ModelPost>()
                            .setQuery(postsQuery, ModelPost.class)
                            .build();

                    fIrebasePostAdapter = new FIrebasePostAdapter(options, getContext());
                    fIrebasePostAdapter.startListening();
                    fIrebasePostAdapter.updateOptions(options);
                    recpost.setAdapter(fIrebasePostAdapter);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shimmerChanger();
                        }
                    }, 500);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });

        ViewCompat.setNestedScrollingEnabled(recpost, false);
    }

    private void fetchTrending() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        Query trendingQuery = FirebaseDatabase.getInstance().getReference().child("trending");
        trendingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    options1 = new FirebaseRecyclerOptions.Builder<TrendingFirebaseModel>()
                            .setQuery(trendingQuery, TrendingFirebaseModel.class)
                            .build();

                    trendingAdapter = new TrendingAdapter(options1);
                    trendingAdapter.startListening();
                    trendingAdapter.updateOptions(options1);
                    recyclerView.setAdapter(trendingAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }


    @Override
    public void onStop() {
        super.onStop();
        if(fIrebasePostAdapter != null)fIrebasePostAdapter.stopListening();
        if(trendingAdapter != null)trendingAdapter.stopListening();
    }

    private void loadImages() {
        database.getReference().child("promo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot snap :snapshot.getChildren()){
                    SliderData sliderData = snap.getValue(SliderData.class);
                    sliderDataArrayList.add(sliderData);
                }
                adapter = new SliderAdapter(getActivity(),sliderDataArrayList);
                sliderView.setSliderAdapter(adapter);
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                sliderView.setScrollTimeInSec(4);
                sliderView.setAutoCycle(true);
                sliderView.startAutoCycle();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void shimmerChanger() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
        storyrc.setVisibility(View.VISIBLE);
        blogrc.setVisibility(View.VISIBLE);
    }

}