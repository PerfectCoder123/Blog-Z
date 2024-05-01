package com.example.autoslider.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.autoslider.MainActivity;
import com.example.autoslider.Models.ModelUserReal;
import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OtpFragment extends Fragment {
    TextView phoneno,time;
    EditText e1,e2,e3,e4,e5,e6;
    TextView verifybtn;
    FirebaseAuth mAuth;
    String mVerificationId;
    TextView resend;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseDatabase database;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String phone;
    ImageView backbtn;
    private  int sec = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

           ScreenSizeUtilityClass.fullWhiteScreen(getActivity());
            View view = inflater.inflate(R.layout.otp_layout,container,false);
            mAuth = FirebaseAuth.getInstance();
            phoneno = view.findViewById(R.id.otp_layout_phoneno);
            e1 = view.findViewById(R.id.otp_layout_otp1);
            e2 = view.findViewById(R.id.otp_layout_otp2);
            e3 = view.findViewById(R.id.otp_layout_otp3);
            e4 = view.findViewById(R.id.otp_layout_otp4);
            e5 = view.findViewById(R.id.otp_layout_otp5);
            e6 = view.findViewById(R.id.otp_layout_otp6);
            time = view.findViewById(R.id.otp_layout_time);
            backbtn = view.findViewById(R.id.otp_layout_backbtn);

            database = FirebaseDatabase.getInstance();
            verifybtn = view.findViewById(R.id.otp_layout_verifybtn);
              resend = view.findViewById(R.id.otp_layout_resend);
             phone = (String)getArguments().getString("phone");
            phoneno.setText("+91-"+ phone);

            verifybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVerify();
                }
            });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;
            }
        };



        sendOtp();
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(sec ==0) sendOtp();
               else Toast.makeText(getContext(), "please wait for few seconds", Toast.LENGTH_SHORT).show();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.landing_container,new PhoneFragment());
                transaction.commit();
            }
        });

        return view;
    }

    private void sendOtp(){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phone)       // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        sec = 60;
        startTimer();

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
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
                                    if(!flag) updateUID(user);
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    private void onVerify(){
        if(e1.getText().toString().equals("") ||e2.getText().toString().equals("") || e3.getText().toString().equals("") ||e4.getText().toString().equals("") ||e5.getText().toString().equals("") ||e6.getText().toString().equals(""))
            Toast.makeText(getContext(), " OTP field empty", Toast.LENGTH_SHORT).show();
        else {

            String otp = e1.getText().toString() + e2.getText().toString() + e3.getText().toString() +e4.getText().toString() +e5.getText().toString() +e6.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            signInWithPhoneAuthCredential(credential);
        }
    }
    private void updateUID(FirebaseUser user) {

        ModelUserReal userReal = new ModelUserReal();
        userReal.setEmail(user.getEmail());
        userReal.setUsername("unknown");
        userReal.setFollowerCount("0");
        userReal.setTrendingCount("0");
        userReal.setBio("bio");
        userReal.setUserId(user.getUid());
        userReal.setPostCount("0");
        userReal.setProfileImage("https://cdn-icons-png.flaticon.com/128/149/149071.png");
        database.getReference().child("users").child(user.getUid()).setValue(userReal);

    }

    private void startTimer(){
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                if(sec > 0){
                 sec--;
                 time.setText(""+ sec);
                 handler.postDelayed(this,1000);
                }else{

                }
            }
        },1000);

    }
}
