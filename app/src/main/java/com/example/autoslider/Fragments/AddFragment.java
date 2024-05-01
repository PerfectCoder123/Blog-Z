package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class AddFragment extends Fragment {

    EditText title,description,tag,location;
    TextView preview,clear;
    ArrayList<String> tagList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScreenSizeUtilityClass.fullWhiteScreen(getActivity());
        View  view = inflater.inflate(R.layout.add_postlayout,container,false);
        title = view.findViewById(R.id.add_postlayout_title);
        description = view.findViewById(R.id.add_postlayout_blog);
        tag = view.findViewById(R.id.add_postlayout_tags);
        clear = view.findViewById(R.id.add_layout_clear);
        location = view.findViewById(R.id.add_postlayout_location);
        preview = view.findViewById(R.id.add_layout_preview);
        tagList = new ArrayList<>();
          clear.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
           title.getText().clear();
           location.getText().clear();
           description.getText().clear();
           tag.getText().clear();

             }
          });

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(onVerify()) {

                   Bundle bundle = new Bundle();
                   bundle.putSerializable("taglist",tagList);
                   bundle.putString("title",title.getText().toString());
                   bundle.putString("desc",description.getText().toString());
                   bundle.putString("location",location.getText().toString());
                   bundle.putString("tags",tag.getText().toString());
                   FragmentManager manager = getParentFragmentManager();
                   FragmentTransaction transaction = manager.beginTransaction();
                   AddPreview addPreview = new AddPreview();
                   addPreview.setArguments(bundle);
                   transaction.replace(R.id.add_container,addPreview);
                   transaction.commit();
               }
            }
        });

        return view;
    }

    private boolean tagTokens(){

        StringTokenizer str = new StringTokenizer(tag.getText().toString());
        while (str.hasMoreElements()){
            String tagKey = str.nextToken();
            if(tagKey.charAt(0) != '#') {
                Toast.makeText(getContext(),"Invalid Tag missing #",Toast.LENGTH_SHORT).show();
                return false;
            }
            tagList.add(tagKey.substring(1));
        }
        return true;

    }

    private boolean onVerify() {
        if(title.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Title missing", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(description.getText().toString().equals("")) {
            Toast.makeText(getContext(), "content missing", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(location.getText().toString().equals("")) {
            Toast.makeText(getContext(), "location missing", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tag.getText().toString().equals("")) {
            Toast.makeText(getContext(), "tag missing", Toast.LENGTH_SHORT).show();
            return false;
        }
      return tagTokens();

    }
}

