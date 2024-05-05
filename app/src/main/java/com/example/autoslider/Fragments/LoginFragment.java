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
import com.example.autoslider.ScreenSizeUtilityClass;
import com.flaviofaria.kenburnsview.KenBurnsView;
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

public class LoginFragment extends Fragment {
    EditText password,email;
    TextView loginbtn,signupchange;
    ProgressBar bar;
    private int currentApiVersion;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ImageView goolgebtn;
    CardView phonebtn;
    TextView forgot;
    FirebaseDatabase database;
    KenBurnsView kenBurnsView;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ScreenSizeUtilityClass.fullScreen(getActivity());

        View view = inflater.inflate(R.layout.login,container,false);
        password = view.findViewById(R.id.signup_layout_password);
        email = view.findViewById(R.id.signup_layout_email);
        loginbtn = view.findViewById(R.id.login_siginbutton);
        bar = view.findViewById(R.id.login_progressbar);
        forgot = view.findViewById(R.id.login_layout_forgot);
        signupchange = view.findViewById(R.id.signin_change);
        mAuth = FirebaseAuth.getInstance();
        user =  mAuth.getCurrentUser();
        goolgebtn  = view.findViewById(R.id.login_googlebtn);
        database  =FirebaseDatabase.getInstance();
        phonebtn = view.findViewById(R.id.login_phonebtn);
        kenBurnsView = view.findViewById(R.id.login_layout_kenburns);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        goolgebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("")) Toast.makeText(getContext(), "email field empty", Toast.LENGTH_SHORT).show();
                else if(!(email.getText().toString().contains("@"))) Toast.makeText(getContext(), "invalid email", Toast.LENGTH_SHORT).show();
                else{
                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "check your email", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

       signupchange.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               FragmentManager manager = getFragmentManager();
               FragmentTransaction transaction = manager.beginTransaction();
               transaction.replace(R.id.landing_container, new SignUpFragement());
               transaction.commit();
           }
       });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                onVerify();
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
        return  view;
    }

    public void onVerify() {
     String mPass = password.getText().toString();
     String mEmail = email.getText().toString();

        mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                bar.setVisibility(View.INVISIBLE);
                 user = mAuth.getCurrentUser();
               if(user != null ) {
                   Intent intent = new Intent(getActivity(), MainActivity.class);
                   startActivity(intent);
               }
               else Toast.makeText( getContext(), "Credentials Mismatch", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            ScreenSizeUtilityClass.fullScreen(getActivity());
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
        kenBurnsView.pause();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();

                            bar.setVisibility(View.VISIBLE);
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

                            Log.w(TAG, "signInWithCredential:failure", task.getException());

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
        userReal.setBio("");
        userReal.setUserId(account.getUid());
        userReal.setPostCount("0");
        userReal.setProfileImage("https://cdn-icons-png.flaticon.com/128/149/149071.png");
        database.getReference().child("users").child(account.getUid()).setValue(userReal);

    }
}
