package com.example.autoslider.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.autoslider.MainActivity;
import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;

public class SearchFragment extends Fragment {
  TextView searchView;
  ImageView searchbtn;
  ImageView backbtn;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScreenSizeUtilityClass.fullWhiteScreen(getActivity());
        View view = inflater.inflate(R.layout.search_layout, container, false);
        searchView = view.findViewById(R.id.search_layout_search);
        searchbtn = view.findViewById(R.id.search_layout_searchbutton);
        backbtn = view.findViewById(R.id.search_layout_backbtn);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.search_container, new TopUserFragment());
        transaction.commit();

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("keyword", searchView.getText().toString());
                SearchedUserFragment fragment = new SearchedUserFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.search_container, fragment);
                transaction.commit();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
