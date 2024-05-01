package com.example.autoslider.Fragments;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.autoslider.MainActivity;
import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseUser currentuser;
    LottieAnimationView animationView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScreenSizeUtilityClass.fullScreen(getActivity());
        View view = inflater.inflate(R.layout.splash_screen,container,false);
        auth = FirebaseAuth.getInstance();
        currentuser= auth.getCurrentUser();
        animationView = view.findViewById(R.id.splash_screen_animation_view);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            switchLogin();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return  view;
    }

    public void switchLogin(){
        if(currentuser!=null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
        else{
            FragmentTransaction trans =getActivity().getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.landing_container,new LoginFragment());
            trans.commit();
        }
    }


}
