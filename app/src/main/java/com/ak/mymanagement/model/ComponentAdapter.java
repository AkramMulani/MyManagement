package com.ak.mymanagement.model;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.mymanagement.FullScreenImageActivity;
import com.ak.mymanagement.R;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ViewHolder> {

    private List<Component> components;

    public ComponentAdapter(List<Component> components) {
        this.components = components;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_info_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Component component = components.get(position);
        // Bind data to views in the ViewHolder
        holder.bind(component);
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Declare views in the item layout
        private final TextView nameTextView;
        private final TextView quantityTextView;
        private final TextView qualityTextView;
        private final TextView priceTextView;
        private final ImageView imageView;
        private final Button increase;
        private final Button decrease;
        private final FloatingActionButton edit;
        private Component component;
        private FirebaseDatabase db;
        private DatabaseReference refComp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            db = FirebaseDatabase.getInstance();
            refComp = db.getReference("Components");
            // Initialize views
            nameTextView = itemView.findViewById(R.id.component_name);
            quantityTextView = itemView.findViewById(R.id.quantity);
            qualityTextView = itemView.findViewById(R.id.quality);
            priceTextView = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.component_pic);
            increase = itemView.findViewById(R.id.action_increase);
            decrease = itemView.findViewById(R.id.action_decrease);
            edit = itemView.findViewById(R.id.action_edit_component);

            increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(component.getQuantity());
                    refComp.child(component.getName()).child("quantity").setValue(String.valueOf(quantity+1));
                }
            });

            decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(component.getQuantity());
                    refComp.child(component.getName()).child("quantity").setValue(String.valueOf(quantity-1));
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog.showDialog(itemView.getContext(), new CustomDialog.DialogListener() {
                        @Override
                        public void onDialogPositiveClick(String name, String quantity, String quality, String price) {
                            String imageUrl = component.getImage();

                            // then add new version
                            if (!name.isEmpty()) {
                                refComp.child(component.getName()).removeValue();
                                component.setName(name);
                                refComp.child(name).child("name").setValue(name);
                                refComp.child(name).child("quantity").setValue(component.getQuantity());
                                refComp.child(name).child("quality").setValue(component.getQuality());
                                refComp.child(name).child("price").setValue(component.getPrice());
                                refComp.child(name).child("image").setValue(imageUrl);
                            }
                            if (!quantity.isEmpty())
                                refComp.child(component.getName()).child("quantity").setValue(quantity);
                            if (!quality.isEmpty())
                                refComp.child(component.getName()).child("quality").setValue(quality);
                            if (!price.isEmpty())
                                refComp.child(component.getName()).child("price").setValue(price);
                            if (!imageUrl.isEmpty())
                                refComp.child(component.getName()).child("image").setValue(imageUrl);
                            refComp.child(component.getName()).child("updatedBy").setValue(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        }
                    });
                }
            });
        }

        public void bind(Component component) {
            this.component = component;
            // Bind data to views
            nameTextView.setText(component.getName());
            quantityTextView.setText(component.getQuantity());
            qualityTextView.setText(component.getQuality());
            priceTextView.setText(component.getPrice());
            // Load image using Glide
            Glide.with(itemView.getContext())
                    .load(component.getImage())
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open FullScreenImageActivity with the image URI
                    Intent fullScreenIntent = new Intent(itemView.getContext(), FullScreenImageActivity.class);
                    fullScreenIntent.putExtra("imageUri", Uri.parse(component.getImage()));
                    itemView.getContext().startActivity(fullScreenIntent);
                }
            });
        }
    }
}
