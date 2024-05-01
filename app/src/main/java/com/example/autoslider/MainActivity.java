package com.example.autoslider;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.autoslider.Fragments.AddMainPageFragement;
import com.example.autoslider.Fragments.ExploreVideoFragment;
import com.example.autoslider.Fragments.HomeFragment;
import com.example.autoslider.Fragments.ProfileFragment;
import com.example.autoslider.Fragments.SearchFragment;
import nl.joery.animatedbottombar.AnimatedBottomBar;


public class MainActivity extends AppCompatActivity {
    AnimatedBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bottomBar = findViewById(R.id.bottomnav);


        FragmentTransaction trans =getSupportFragmentManager().beginTransaction();
        if(getIntent().getStringExtra("key") != null && !getIntent().getStringExtra("key").equals("profile")) {
            bottomBar.selectTabAt(4,true);
            trans.replace(R.id.explore_frame, new ProfileFragment());

        }
        else {
            trans.replace(R.id.explore_frame, new HomeFragment());
            bottomBar.selectTabAt(0, true);
        }
        trans.commit();

        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                FragmentTransaction trans =getSupportFragmentManager().beginTransaction();
                switch (i1){
                    case 0: trans.replace(R.id.explore_frame,new HomeFragment());
                        break;
                    case 1 : trans.replace(R.id.explore_frame,new SearchFragment());
                        break;
                    case 2 : trans.replace(R.id.explore_frame,new AddMainPageFragement());
                         break;
                    case 3: trans.replace(R.id.explore_frame,new ExploreVideoFragment());
                        break;
                    case 4 : trans.replace(R.id.explore_frame,new ProfileFragment());
                    break;
                }
                trans.commit();

            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

    }


    @Override
    public void onBackPressed() {

    }


}