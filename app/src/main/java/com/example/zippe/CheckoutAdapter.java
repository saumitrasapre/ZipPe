package com.example.zippe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.ModelCheckout;
import de.hdodenhof.circleimageview.CircleImageView;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {
    private List<ModelCheckout> mList;
    private Context context;

    public CheckoutAdapter(List<ModelCheckout> mList,Context context) {
        this.mList=mList;
        this.context=context;
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView checkoutProductImage;
        public TextView checkoutProductName,checkoutProductWeight,checkoutProductQuantity,checkoutProductPrice;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            checkoutProductImage=itemView.findViewById(R.id.checkout_product_image);
            checkoutProductName =itemView.findViewById(R.id.checkout_product_name);
            checkoutProductWeight=itemView.findViewById(R.id.checkout_product_weight);
            checkoutProductQuantity=itemView.findViewById(R.id.checkout_product_quantity);
            checkoutProductPrice=itemView.findViewById(R.id.checkout_product_price);
        }
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item,parent,false);
        CheckoutViewHolder viewHolder=new CheckoutViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {

        ModelCheckout currentItem=mList.get(position);
        Picasso.get()
                .load(currentItem.getProductImage())
                .placeholder(R.color.lightgrey)
                .into(holder.checkoutProductImage);
        holder.checkoutProductName.setText(currentItem.getProductName());
        holder.checkoutProductPrice.setText("₹ "+currentItem.getResultPrice());
        holder.checkoutProductQuantity.setText("x"+String.valueOf(currentItem.getProductQuantity()));
        holder.checkoutProductWeight.setText(currentItem.getProductWeight());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
