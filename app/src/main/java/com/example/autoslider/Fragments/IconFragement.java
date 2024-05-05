package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.Adapters.IconAdapter;
import com.example.autoslider.Icons;
import com.example.autoslider.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IconFragement extends Fragment {
    RecyclerView recyclerView;
    IconAdapter iconAdapter;
    ArrayList<Icons> iconsList;
    TextView nextpage;
    int key = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_layout_icon_selecter,container,false);
        recyclerView = view.findViewById(R.id.profile_icon_rc);
        nextpage = view.findViewById(R.id.profile_layout_nextpage);
        fetchIcons();
        nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchIcons();
            }
        });
        return view;
    }


    public static boolean isCorrect(String key) {
        return key.contains("128");
    }

    public void fetchIcons()
    {

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Set<Icons> list = new HashSet<>();
            Document uri;
            try {
                    uri = Jsoup.connect("https://www.flaticon.com/free-icons/avatar/" + ++key).get();
                    Elements element = uri.select("img[src]"); // tag
                    for (Element link : element) {
                        String url = link.attr("data-src"); // attribute
                        String name = link.attr("title");
                        if (isCorrect(url)) {
                            list.add(new Icons(url, name));
                        }
                    }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
            }
            iconsList = new ArrayList<>(list);
            iconAdapter = new IconAdapter(iconsList,getContext());
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(iconAdapter);
        }
    }
}
