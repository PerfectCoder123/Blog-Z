package com.example.autoslider.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.autoslider.ImageFetchingService;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.Models.ModelPost;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddPreview extends Fragment {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    TextView publish,title,description,location,reload,authorname,currentDate;
    ImageView blogimage,author,bookmark;
    List<String> imgUrl;
    ProgressBar bar;
    ModelUserReal userReal;
    private int AI = -1;
    ImageFetchingService mservice;
    ServiceConnection connection;
    ArrayList<String> imgTags;
    boolean isbounded  = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_previewlayout,container,false);
        publish = view.findViewById(R.id.add_layout_publish);
        title = view.findViewById(R.id.bloglayout_title);
        reload = view.findViewById(R.id.add_preview_reload);
        blogimage = view.findViewById(R.id.bloglayout_blogimage);
        description = view.findViewById(R.id.description);
        location = view.findViewById(R.id.bloglayout_location);
        author = view.findViewById(R.id.profileimagemain);
        authorname = view.findViewById(R.id.bloglayout_profilename);
        currentDate = view.findViewById(R.id.bloglayout_date);
        bar = view.findViewById(R.id.add_preview_bar);
        bookmark = view.findViewById(R.id.bloglayout_bookmark);
        imgUrl  = new ArrayList<>();

        Thread.currentThread().setName("I am main thread");


        location.setText((String)getArguments().getString("location"));
        description.setText((String)getArguments().getString("desc"));
        title.setText((String)getArguments().getString("title"));
        String tags =  (String)getArguments().getString("tags");
        imgTags = (ArrayList<String>)getArguments().getSerializable("taglist");

        // making connection
        auth  = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userReal = new ModelUserReal();

        //calling image fectching service
        establishConnection();
        bindImageFetchingService();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callService(imgTags);

            }
        },1000);

        // fetching user information
        databaseReference.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userReal = snapshot.getValue(ModelUserReal.class);
                Picasso.get().load(userReal.getProfileImage()).into(author);
                authorname.setText(userReal.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bookmark.setVisibility(View.GONE);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        currentDate.setText(date);


        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageAi();
            }
        });

        // Object create so that value can be set on it
        ModelPost post = new ModelPost();

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().equals("")) Toast.makeText(getContext(), "Please select title", Toast.LENGTH_SHORT).show();
                else if(description.getText().toString().equals(""))Toast.makeText(getContext(), "Add description to your blog", Toast.LENGTH_SHORT).show();
                else {
                    post.setTags(tags);
                    post.setPostImage(imgUrl.get(AI%imgUrl.size()));
                    post.setDescription(description.getText().toString());
                    post.setTitlepeek(title.getText().toString());
                    post.setLocation(location.getText().toString());
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    post.setDate(date);
                    post.setUserId(auth.getCurrentUser().getUid());
                    post.setTime(currentTime);
                    postData(post);
                }
            }
        });

        return view;
    }

   private void imageAi(){
       Glide.with(getContext()).load(imgUrl.get(RandomNum())).into(blogimage);
   }

   private int RandomNum(){
        return (++AI)%imgUrl.size();
   }

   // method for posting data
    private void postData(ModelPost post){
        String key;
        key = databaseReference.child("posts").push().getKey();
        post.setPostId(key);
        databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child("posts").child(key).setValue(post);
                IdModel model = new IdModel(key);
                databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("posts").child(key).setValue(model);
                String postCt = (Integer.valueOf(userReal.getPostCount()) + 1) + "";
                databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("postCount").setValue(postCt);
                Toast.makeText(getContext(), "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
    }

    private void bindImageFetchingService(){
        Intent intent = new Intent(getContext(), ImageFetchingService.class);
        getActivity().bindService(intent,connection, Context.BIND_AUTO_CREATE);

    }

    private void establishConnection(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ImageFetchingService.ImageServiceBinder binder =(ImageFetchingService.ImageServiceBinder) service;
                mservice = binder.getService();
                isbounded  = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    private void callService(List<String> tags){

               Thread fetch =  new Thread(new Runnable() {
                     @Override
                     public void run() {
                         Log.d("tagger", "service is called");
                         while (true) {
                             if(isbounded) {
                                 for (String tag : tags) {
                                     List<String> list = mservice.fetchImage(tag);
                                     imgUrl.addAll(list);
                                     if(imgUrl.size() > 50) break;

                                 }
                                 break;
                             }
                         }
                         getActivity().runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 imageAi();
                                 bar.setVisibility(View.INVISIBLE);
                                 Log.d("tagger", "Thread size is " + imgUrl.size());
                             }
                         });

                     }
                 });
               fetch.start();

              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      Log.d("tagger", "Fetch Thread is : " + fetch.isAlive());
                  }
              },10000);


    }

    private void unBindImageFetchingService(ServiceConnection connection){
        getActivity().unbindService(this.connection);
    }


    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (isbounded) {
            unBindImageFetchingService(connection);
            isbounded = false;
        }
    }
}
