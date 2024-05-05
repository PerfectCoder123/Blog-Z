package com.example.autoslider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.autoslider.Fragments.LoginFragment;
import com.example.autoslider.Fragments.SplashFragment;


public class UserVerification extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);
        FragmentTransaction trans =getSupportFragmentManager().beginTransaction();
        if(getIntent().getStringExtra("splash") == null) trans.replace(R.id.landing_container,new SplashFragment());
        else trans.replace(R.id.landing_container,new LoginFragment());
        trans.commit();

    }

}
