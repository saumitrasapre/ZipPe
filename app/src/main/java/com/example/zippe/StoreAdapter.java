package com.example.zippe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.ModelStore;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> implements Filterable {
   private Context mContext;
   private List<ModelStore> mlist;
   private List<ModelStore>mlistfull;
   private ImageSlider imageSlider;
   private List<String>images;
   private ImageButton getDirection;
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
        store_image.setClipToOutline(true);
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
        store_image.setClipToOutline(true);


        holder.store_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(holder.store_image.getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(holder.store_image.getContext()).inflate(R.layout.layout_bottom_sheet,(LinearLayout)v.findViewById(R.id.bottomSheetContainer));
                bottomSheetDialog.setContentView(bottomSheetView);
                imageSlider=bottomSheetView.findViewById(R.id.image_slider);

                List<SlideModel> slideModels=new ArrayList<>();
                images=new ArrayList<>(store.getOtherImages());
                for(String item:images)
                {
                    slideModels.add(new SlideModel(item));
                }
                GeoPoint storeLoc=store.getLocation();
                imageSlider.setImageList(slideModels,true);
                getDirection=bottomSheetView.findViewById(R.id.get_direction);
                getDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //String geoUri = "http://maps.google.com/maps?q=loc:" + storeLoc.getLatitude() + "," + storeLoc.getLongitude() + " (" + "Dmart" + ")";
                        String geoUri = "https://maps.app.goo.gl/UYC3igVovFPEtLyv6";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        bottomSheetDialog.getContext().startActivity(intent);

                    }
                });
                TextView bottomname=bottomSheetView.findViewById(R.id.bottom_name);
                bottomname.setText(store.getName());
                TextView bottomrating=bottomSheetView.findViewById(R.id.bottom_item_rating);
                bottomrating.setText(store.getRating());
                TextView bottomAddress=bottomSheetView.findViewById(R.id.bottom_address);
                bottomAddress.setText(store.getAddress());
                bottomSheetDialog.show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        ImageView store_image;
        TextView store_name,store_category,store_rating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store_image=itemView.findViewById(R.id.item_image);
            store_name=itemView.findViewById(R.id.item_name);
            store_category=itemView.findViewById(R.id.item_category);
            store_rating=itemView.findViewById(R.id.item_rating);//
//            itemView.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v) {
//
//
//                    final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(itemView.getContext(),R.style.BottomSheetDialogTheme);
//                    View bottomSheetView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_bottom_sheet,(LinearLayout)v.findViewById(R.id.bottomSheetContainer));
//                    bottomSheetDialog.setContentView(bottomSheetView);
//                    bottomSheetDialog.show();
//
//
//                }
//            });
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
