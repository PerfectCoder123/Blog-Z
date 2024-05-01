package com.example.autoslider.Adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.Fragments.CategoryViewFragment;
import com.example.autoslider.Models.CategoryModel;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.R;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CategoryAdapter extends  RecyclerView.Adapter<CategoryAdapter.ExploreViewHolder>  {

    private List<CategoryModel> exploreModels;
    private FragmentManager fragmentManager;
    public CategoryAdapter(List<CategoryModel> exploreModels, FragmentManager fragmentManager) {
        this.exploreModels = exploreModels;
        this.fragmentManager = fragmentManager;

    }

    @NonNull
    
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExploreViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.category_single_item,
                        parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        int pos= position;
        holder.setExploreData(exploreModels.get(position));
        holder.kenBurnsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collection<IdModel> l = exploreModels.get(pos).getPosts().values();
                ArrayList<String> list = new ArrayList<>();
                for(IdModel idModel : l) list.add(idModel.getPostId());
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("category_posts",list);
                bundle.putString("category_type",exploreModels.get(pos).name);
                Fragment fragment = new CategoryViewFragment();
                fragment.setArguments(bundle);
                transaction.replace(R.id.explore_frame, fragment);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return exploreModels.size();
    }

    static  class  ExploreViewHolder extends RecyclerView.ViewHolder{
        private KenBurnsView kenBurnsView;
        private TextView ExploreText;


        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            kenBurnsView = itemView.findViewById(R.id.ken);
            ExploreText = itemView.findViewById(R.id.location);
        }

        void setExploreData(CategoryModel exploreModel){
            Picasso.get().load(exploreModel.imageUrl).into(kenBurnsView);
            ExploreText.setText(exploreModel.name);
        }
    }
}

