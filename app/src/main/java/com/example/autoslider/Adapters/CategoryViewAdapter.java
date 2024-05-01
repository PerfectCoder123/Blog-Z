package com.example.autoslider.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoslider.MainBlogReading;
import com.example.autoslider.Models.CategoryViewModel;
import com.example.autoslider.Models.IdModel;
import com.example.autoslider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.holder> {
    List<CategoryViewModel> categoryViewModels;
    ArrayList<IdModel> bookmarkList;
    public CategoryViewAdapter(List<CategoryViewModel> categoryViewModels, ArrayList<IdModel> bookmarkList) {
        this.categoryViewModels = categoryViewModels;
        this.bookmarkList = bookmarkList;

    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.category_view_single_element,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        int pos = position;
        Picasso.get().load(categoryViewModels.get(position).getImgUrl()).into(holder.image);
        holder.title.setText(categoryViewModels.get(position).getBlogTitle());
        holder.date.setText(categoryViewModels.get(position).getDate());
        if(isBookmarked(categoryViewModels.get(pos).getPostId())) holder.bookmark.setImageResource(R.drawable.bookmarkblack);
        holder.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainBlogReading.class);
                intent.putExtra("postId",categoryViewModels.get(pos).getPostId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isBookmarked(categoryViewModels.get(pos).getPostId())){
                    holder.bookmark.setImageResource(R.drawable.bookmarkwhite);
                    Toast.makeText(v.getContext(), "removed bookmark", Toast.LENGTH_SHORT).show();
                    for(int i = 0 ; i< bookmarkList.size(); i++){
                        if(bookmarkList.get(i).getPostId().equals(categoryViewModels.get(pos).getPostId())) bookmarkList.remove(i);
                    }
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("bookmark").child(categoryViewModels.get(pos).getPostId()).setValue(null);
                }
                else {
                    holder.bookmark.setImageResource(R.drawable.bookmarkblack);
                    Toast.makeText(v.getContext(), "post bookmarked", Toast.LENGTH_SHORT).show();
                    IdModel id = new IdModel(categoryViewModels.get(pos).getPostId());
                    bookmarkList.add(id);
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("bookmark").child(categoryViewModels.get(pos).getPostId()).setValue(id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryViewModels.size();
    }

    public boolean isBookmarked(String id){
        for(IdModel idmodel : bookmarkList){
            if(idmodel.getPostId().equals(id)) return true;
        }
        return false;
    }

    public class holder extends RecyclerView.ViewHolder{
     ImageView image,bookmark;
     TextView date,title,click;
        public holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.category_single_view_img);
            title = itemView.findViewById(R.id.category_single_view_title);
            date = itemView.findViewById(R.id.category_single_view_date);
            click = itemView.findViewById(R.id.category_view_single_element_click);
            bookmark = itemView.findViewById(R.id.category_view_single_element_bookmark);
        }
    }
}
