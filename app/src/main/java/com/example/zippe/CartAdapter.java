package com.example.zippe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    List<ModelCart> cartList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference cart = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart");

    public CartAdapter(Context context, List<ModelCart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_items,parent,false);

        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Picasso.get()
                .load(cartList.get(position).getProductImage())
                .placeholder(R.color.lightgrey)
                .into(holder.img_product);

        holder.quantity_product.setNumber(String.valueOf(cartList.get(position).getProductQuantity()));
        holder.name_product.setText(cartList.get(position).getProductName());
        holder.price_product.setText(new StringBuilder("₹").append(cartList.get(position).getProductPrice()));
        holder.code_product.setText(cartList.get(position).getProductCode());


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

    class CartViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img_product;
        TextView name_product, code_product,price_product;
        ElegantNumberButton quantity_product;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product=itemView.findViewById(R.id.img_product);
            name_product=itemView.findViewById(R.id.name_product);
            code_product=itemView.findViewById(R.id.code_product);
            price_product=itemView.findViewById(R.id.price_product);
            quantity_product=itemView.findViewById(R.id.quantity_product);

        }
    }
}
