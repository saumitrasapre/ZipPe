package com.example.zippe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.ModelCart;
import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    List<ModelCart> cartList;
    Activity activity;
    Dialog itemDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference cart = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart");

    public CartAdapter(Context context, List<ModelCart> cartList, Activity activity) {
        this.context = context;
        this.cartList = cartList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_items, parent, false);
        CartViewHolder viewHolder = new CartViewHolder(itemView);

        itemDialog = new Dialog(activity);
        itemDialog.setContentView(R.layout.dialog_product);
        itemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        viewHolder.cart_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView dialog_product_name = itemDialog.findViewById(R.id.dialog_product_name);
                TextView dialog_product_price = itemDialog.findViewById(R.id.dialog_product_price);
                TextView dialog_product_code = itemDialog.findViewById(R.id.dialog_product_code);
                TextView dialog_product_quantity = itemDialog.findViewById(R.id.dialog_product_quantity);
                TextView dialog_product_weight = itemDialog.findViewById(R.id.dialog_product_weight);
                CircleImageView dialog_product_image = itemDialog.findViewById(R.id.dialog_product_image);

                dialog_product_name.setText(cartList.get(viewHolder.getAdapterPosition()).getProductName());
                dialog_product_price.setText("₹ " + String.valueOf(Integer.parseInt(cartList.get(viewHolder.getAdapterPosition()).getProductPrice())*cartList.get(viewHolder.getAdapterPosition()).getProductQuantity()));
                dialog_product_code.setText(cartList.get(viewHolder.getAdapterPosition()).getProductCode());
                dialog_product_quantity.setText(String.valueOf(cartList.get(viewHolder.getAdapterPosition()).getProductQuantity()));
                dialog_product_weight.setText(cartList.get(viewHolder.getAdapterPosition()).getProductWeight());
                Picasso.get().load(cartList.get(viewHolder.getAdapterPosition())
                        .getProductImage())
                        .placeholder(R.color.lightgrey)
                        .into(dialog_product_image);

                itemDialog.show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Picasso.get()
                .load(cartList.get(position).getProductImage())
                .placeholder(R.color.lightgrey)
                .into(holder.img_product);

        holder.quantity_product.setNumber(String.valueOf(cartList.get(position).getProductQuantity()));
        holder.name_product.setText(cartList.get(position).getProductName());
        holder.price_product.setText(new StringBuilder("₹").append(String.valueOf(Integer.parseInt(cartList.get(position).getProductPrice())*cartList.get(position).getProductQuantity())));
        holder.weight_product.setText(cartList.get(position).getProductWeight());


        holder.quantity_product.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                cartList.get(position).setProductQuantity(newValue);
                notifyDataSetChanged();
                cart.whereEqualTo("productCode", cartList.get(position).getProductCode()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<Object, Integer> map = new HashMap<>();
                            map.put("productQuantity", newValue);
                            cart.document(document.getId()).set(map, SetOptions.merge());
                        }

                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_product;
        TextView name_product, weight_product, price_product;
        ElegantNumberButton quantity_product;
        CardView cart_item;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.img_product);
            name_product = itemView.findViewById(R.id.name_product);
            weight_product = itemView.findViewById(R.id.weight_product);
            price_product = itemView.findViewById(R.id.price_product);
            quantity_product = itemView.findViewById(R.id.quantity_product);
            cart_item = itemView.findViewById(R.id.cart_item);

        }
    }
}
