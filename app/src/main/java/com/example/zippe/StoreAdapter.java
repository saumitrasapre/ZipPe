package com.example.zippe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.ModelStore;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private List<ModelStore> mlist;
    private List<ModelStore> mlistfull;
    private ImageSlider imageSlider;
    private List<String> images;
    private ImageButton getDirection;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button visit;
    private List<DocumentSnapshot> intentList;
    private ProgressDialog pd;

    StoreAdapter(Context context, List<ModelStore> list) {
        mContext = context;
        mlist = list;
        mlistfull = new ArrayList<>(mlist);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.store_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelStore store = mlist.get(position);
        ImageView store_image = holder.store_image;
        store_image.setClipToOutline(true);
        TextView store_name;
        TextView store_category;
        TextView store_rating;
        TextView store_dist;
        store_name = holder.store_name;
        store_category = holder.store_category;
        store_rating = holder.store_rating;
        store_dist = holder.store_distance;
        Picasso.get()
                .load(store.getMainImageUrl())
                .placeholder(R.drawable.ic_baseline_local_grocery_store_24)
                .into(store_image);
        store_name.setText(store.getName());
        store_category.setText(store.getCategory());
        store_rating.setText(store.getRating());
        store_image.setClipToOutline(true);

        if (ActivityCompat.checkSelfPermission((Activity) mContext, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(mContext);
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Location storeLoc = new Location("");
                        storeLoc.setLatitude(store.getLocation().getLatitude());
                        storeLoc.setLongitude(store.getLocation().getLongitude());
                        double distance = location.distanceTo(storeLoc);
                        distance /= 1000;
                        double roundDistance = round(distance, 2);
                        store_dist.setText(Double.toString(roundDistance) + "km");

                    }
                }
            });
        }


        holder.store_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.store_image.getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(holder.store_image.getContext()).inflate(R.layout.layout_bottom_sheet, (LinearLayout) v.findViewById(R.id.bottomSheetContainer));
                bottomSheetDialog.setContentView(bottomSheetView);
                imageSlider = bottomSheetView.findViewById(R.id.image_slider);
                pd=new ProgressDialog(bottomSheetView.getContext());
                pd.setMessage("Loading...");
                pd.setCancelable(true);
                pd.setCanceledOnTouchOutside(false);

                List<SlideModel> slideModels = new ArrayList<>();
                images = new ArrayList<>(store.getOtherImages());
                for (String item : images) {
                    slideModels.add(new SlideModel(item));
                }
                //GeoPoint storeLoc=store.getLocation();
                imageSlider.setImageList(slideModels, true);
                getDirection = bottomSheetView.findViewById(R.id.get_direction);
                getDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //String geoUri = "http://maps.google.com/maps?q=loc:" + storeLoc.getLatitude() + "," + storeLoc.getLongitude() + " (" + "Dmart" + ")";
                        //String geoUri = "https://maps.app.goo.gl/UYC3igVovFPEtLyv6";
                        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(store.getAddress()));
                        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
                        bottomSheetDialog.getContext().startActivity(intent);

                    }
                });
                TextView bottomname = bottomSheetView.findViewById(R.id.bottom_name);
                bottomname.setText(store.getName());
                TextView bottomrating = bottomSheetView.findViewById(R.id.bottom_item_rating);
                bottomrating.setText(store.getRating());
                TextView bottomAddress = bottomSheetView.findViewById(R.id.bottom_address);
                bottomAddress.setText(store.getAddress());
                bottomSheetDialog.show();

                visit = bottomSheetView.findViewById(R.id.visit);

                db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart").limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty())
                        {
                            db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart").whereEqualTo("storeId",store.getStoreId()).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.isEmpty())
                                    {
                                        visit.setEnabled(false);
                                        visit.setBackground(mContext.getResources().getDrawable(R.drawable.button_background_red));
                                        visit.setText("Not Allowed");
                                    }

                                    //Log.d("Stores", "onSuccess: "+queryDocumentSnapshots.size());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }
                });



                visit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (ActivityCompat.checkSelfPermission((Activity) mContext, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(mContext);
                            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        Location storeLoc = new Location("");
                                        float[] results = new float[1];
                                        Location.distanceBetween(storeLoc.getLatitude(), storeLoc.getLongitude(), store.getLocation().getLatitude(), store.getLocation().getLongitude(), results);

                                        if (results[0] > 11100)//make sign reverse here
                                        {

                                            pd.show();
                                            db.collection("Stores").whereEqualTo("name", store.getName()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    Intent intent = new Intent(bottomSheetView.getContext(), barcode.class);
                                                    intentList = queryDocumentSnapshots.getDocuments();
                                                    String id = intentList.get(0).getId();
                                                    System.out.println("Store id is: " + id);
                                                    intent.putExtra("Store_id", id);
                                                    bottomSheetView.getContext().startActivity(intent);
                                                    pd.dismiss();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(bottomSheetView.getContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
                                                    pd.dismiss();
                                                }
                                            });


                                        } else {
                                            Toast.makeText(bottomSheetView.getContext(), "Away from store... Please get in the vicinity of the store", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            });
                        }


                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView store_image;
        TextView store_name, store_category, store_rating, store_distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store_image = itemView.findViewById(R.id.item_image);
            store_name = itemView.findViewById(R.id.item_name);
            store_category = itemView.findViewById(R.id.item_category);
            store_rating = itemView.findViewById(R.id.item_rating);
            store_distance = itemView.findViewById(R.id.item_dist);
        }
    }

    public void update(List<ModelStore> mlist) {
        this.mlist = mlist;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ModelStore> filteredList = new ArrayList<>();

            if (constraint.equals(null) || constraint.length() == 0) {
                filteredList.addAll(mlistfull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ModelStore item : mlistfull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mlist.clear();
            mlist.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
