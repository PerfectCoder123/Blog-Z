package com.example.autoslider.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.autoslider.MainActivity;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpFragement extends Fragment {
    EditText password,email,username;
    TextView signupbtn,singinChange;
    ProgressBar bar;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ImageView goolgebtn;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    CardView phonebtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_layout,container,false);
        signupbtn = view.findViewById(R.id.signup_layout_signupbtn);
        bar = view.findViewById(R.id.login_progressbar);
        singinChange = view.findViewById(R.id.signup_change);
        email = view.findViewById(R.id.signup_layout_email);
        password = view.findViewById(R.id.signup_layout_password);
        username = view.findViewById(R.id.signup_layout_username);
        database = FirebaseDatabase.getInstance();
        goolgebtn = view.findViewById(R.id.signup_googlebtn);
         phonebtn = view.findViewById(R.id.signup_layout_phonebtn);

         mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                signup();

            }
        });

        phonebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.landing_container, new PhoneFragment());
                transaction.commit();
            }
        });

        goolgebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);


            }
        });

        singinChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.landing_container, new LoginFragment());
                transaction.commit();
            }
        });
        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean flag = false;
                                    for(DataSnapshot snap : snapshot.getChildren()) {
                                        ModelUserReal userReal = snap.getValue(ModelUserReal.class);
                                        if (userReal.getUserId().equals(user.getUid())) {
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if(!flag) updateGoogleUid(user);
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateGoogleUid(null);
                        }
                    }
                });
    }


    private void updateGoogleUid(FirebaseUser  account) {

        ModelUserReal userReal = new ModelUserReal();
        userReal.setEmail(account.getEmail());
        userReal.setUsername(account.getDisplayName());
        userReal.setFollowerCount("0");
        userReal.setTrendingCount("0");
        userReal.setBio("bio");
        userReal.setUserId(account.getUid());
        userReal.setPostCount("0");
        userReal.setProfileImage("https://cdn-icons-png.flaticon.com/128/149/149071.png");
        database.getReference().child("users").child(account.getUid()).setValue(userReal);

    }

    public void signup(){
        String mPass = password.getText().toString();
        String mEmail = email.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(mEmail,mPass)
                .addOnCompleteListener( getActivity() , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        bar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUID(user);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUID(FirebaseUser user) {

        ModelUserReal userReal = new ModelUserReal();
        userReal.setEmail(email.getText().toString());
        userReal.setUsername(username.getText().toString());
        userReal.setFollowerCount("0");
        userReal.setTrendingCount("0");
        userReal.setBio("bio");
        userReal.setUserId(user.getUid());
        userReal.setPostCount("0");
        userReal.setProfileImage("https://cdn-icons-png.flaticon.com/128/149/149071.png");
        database.getReference().child("users").child(user.getUid()).setValue(userReal);

    }

}
