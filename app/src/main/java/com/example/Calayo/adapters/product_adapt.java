package com.example.Calayo.adapters;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.Calayo.R;
import com.example.Calayo.entities.Item;
import com.example.Calayo.fragments.order_Details;
import com.example.Calayo.fragments.userLoginAct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class product_adapt extends RecyclerView.Adapter<product_adapt.ViewHolder> {
    private ArrayList<Item> items;
    private ArrayList<Item.addOn> addOns;
    private FragmentActivity fragmentActivity;

    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public product_adapt(ArrayList<Item> items, FragmentActivity fragmentActivity) {
        this.items = items;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        // Set text values
        holder.price.setText(" " + item.getPrice());
        holder.name.setText(item.getName());

        // Clear previous background to avoid incorrect images during recycling
        holder.pic.setBackground(null);

        // Load image into background using Glide
        Glide.with(holder.pic.getContext())
                .load(item.getImage())
//                .placeholder(ContextCompat.getDrawable(fragmentActivity, R.drawable.loading_placeholder)) // Optional placeholder
//                .error(ContextCompat.getDrawable(fragmentActivity, R.drawable.error_placeholder))         // Optional error fallback
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.pic.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Optional: set fallback background when image is cleared
                        holder.pic.setBackground(placeholder);
                    }
                });

        // Handle item click to open product management dialog
        holder.itemView.setOnClickListener(view -> {
            if(myAuth.getCurrentUser() == null) {
                Intent order = new Intent(fragmentActivity, userLoginAct.class);
                fragmentActivity.startActivity(order);
            }else {
                Intent intent = new Intent(fragmentActivity, order_Details.class);
                intent.putExtra("image", item.getImage());
                intent.putExtra("name", item.getName());
                intent.putExtra("description",item.getDescription());
                intent.putExtra("price",String.valueOf( item.getPrice()));
                fragmentActivity.startActivity(intent);
            }
        });

        // Handle favorite button click
        if (item.isFavorite()){
            holder.favorite.setVisibility(VISIBLE);
        }else {
            holder.favorite.setVisibility(INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder for product items
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView price, name;
        public ImageView favorite;
        public GridLayout pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            favorite = itemView.findViewById(R.id.favorite);
            pic = itemView.findViewById(R.id.itemHolder); // Layout used for background
        }
    }
}
