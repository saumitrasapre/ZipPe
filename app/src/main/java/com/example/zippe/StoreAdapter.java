package com.example.zippe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.ModelStore;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> implements Filterable {
   private Context mContext;
   private List<ModelStore> mlist;
   private List<ModelStore>mlistfull;
    StoreAdapter(Context context, List<ModelStore> list)
   {
       mContext=context;
       mlist=list;
       mlistfull=new ArrayList<>(mlist);
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
        ImageView store_image=holder.store_image;
        TextView store_name;
        TextView store_category;
        TextView store_rating;
        store_name=holder.store_name;
        store_category=holder.store_category;
        store_rating=holder.store_rating;
        Picasso.get()
                .load(store.getMainImageUrl())
                .placeholder(R.drawable.ic_baseline_local_grocery_store_24)
                .into(store_image);
        store_name.setText(store.getName());
        store_category.setText(store.getCategory());
        store_rating.setText(store.getRating());
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView store_image;
        TextView store_name,store_category,store_rating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store_image=itemView.findViewById(R.id.item_image);
            store_name=itemView.findViewById(R.id.item_name);
            store_category=itemView.findViewById(R.id.item_category);
            store_rating=itemView.findViewById(R.id.item_rating);
        }
    }

    public void update(List<ModelStore> mlist){
        this.mlist=mlist;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private Filter mFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ModelStore> filteredList=new ArrayList<>();

            if(constraint.equals(null) || constraint.length()==0)
            {
                filteredList.addAll(mlistfull);
            }
            else {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(ModelStore item:mlistfull)
                {
                    if(item.getName().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results =new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mlist.clear();
            mlist.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
