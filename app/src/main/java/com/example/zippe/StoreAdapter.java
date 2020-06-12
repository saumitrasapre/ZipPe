package com.example.zippe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
   private Context mContext;
   private ArrayList<ModelStore> mlist;
    StoreAdapter(Context context, ArrayList<ModelStore> list)
   {
       mContext=context;
       mlist=list;
   }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.store_items,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelStore store=mlist.get(position);
        ImageView image=holder.item_image;
        TextView name,place,price;
        name=holder.item_name;
        place=holder.item_place;
        price=holder.item_price;
        image.setImageResource(store.getImage());
        name.setText(store.getName());
        place.setText(store.getPlace());
        price.setText(store.getPrice());
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView item_image;
        TextView item_name,item_place,item_price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image=itemView.findViewById(R.id.item_image);
            item_name=itemView.findViewById(R.id.item_name);
            item_place=itemView.findViewById(R.id.item_place);
            item_price=itemView.findViewById(R.id.item_price);
        }
    }

}
