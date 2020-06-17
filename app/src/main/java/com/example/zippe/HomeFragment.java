package com.example.zippe;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.ModelStore;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoreAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private ChipGroup sortChipGroup;
    private TextView noItems;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<ModelStore> mStores;
    private List<ModelStore> mStoreslistfull;
    private CollectionReference storedb = db.collection("Stores");
    private SwipeRefreshLayout swipeRefreshLayout;
    private FusedLocationProviderClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.stores);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noItems = view.findViewById(R.id.no_items);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        sortChipGroup = view.findViewById(R.id.sort_chip_group);

        client = LocationServices.getFusedLocationProviderClient(getContext());

        requestPermission();
        getMylocation();

        mProgressCircle = view.findViewById(R.id.progress_circle);
        setHasOptionsMenu(true);

        mStores = new ArrayList<ModelStore>();

        storedb.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    Log.d("datafetch", "onEvent: Error fetching data " + e.toString());
                    mProgressCircle.setVisibility(View.GONE);
                } else {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("fetchstores", "onSuccess:Store List Empty ");
                        mProgressCircle.setVisibility(View.GONE);
                        return;
                    } else {
                        List<ModelStore> temp = queryDocumentSnapshots.toObjects(ModelStore.class);
                        mStores.addAll(temp);
                        mStoreslistfull = new ArrayList<>(mStores);
                        // System.out.println(mStores.get(0).getName());
                        Log.d("fetchstores", "onSuccess: Store List Fetched ");
                        mAdapter = new StoreAdapter(getContext(), mStores);
                        //adapter.update(mStores)
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);
                        mProgressCircle.setVisibility(View.GONE);


                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                storedb.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d("fetchstores", "onSuccess:Store List Empty ");
                            mProgressCircle.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            return;
                        } else {
                            List<ModelStore> temp = queryDocumentSnapshots.toObjects(ModelStore.class);
                            mStores.addAll(temp);
                            mStoreslistfull = new ArrayList<>(mStores);
                            // System.out.println(mStores.get(0).getName());
                            Log.d("fetchstores", "onSuccess: Store List Fetched ");
                            mAdapter = new StoreAdapter(getContext(), mStores);
                            //adapter.update(mStores)
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mAdapter);
                            mProgressCircle.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                        mProgressCircle.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });


            }
        });

        sortChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO your background code

                        String filterPattern;
                        mStores.clear();
                        if (checkedId == R.id.supermarket_chip) {
                            filterPattern = "Super Market";
                            System.out.println("Supermarket chip checked");
                            for (ModelStore item : mStoreslistfull) {
                                if (item.getCategory().contains(filterPattern)) {
                                    mStores.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else if (checkedId == R.id.shoppingmall_chip) {
                            filterPattern = "Shopping Mall";
                            System.out.println("Shopping Mall chip checked");
                            for (ModelStore item : mStoreslistfull) {
                                if (item.getCategory().contains(filterPattern)) {
                                    mStores.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else if (checkedId == R.id.electronics_chip) {
                            filterPattern = "Electronics";
                            System.out.println("Electronics chip checked");

                            for (ModelStore item : mStoreslistfull) {
                                if (item.getCategory().contains(filterPattern)) {
                                    mStores.add(item);
                                }
                            }

                            mAdapter.notifyDataSetChanged();
                        } else if (checkedId == R.id.clothing_chip) {
                            filterPattern = "Clothing";
                            System.out.println("Clothing chip checked");

                            for (ModelStore item : mStoreslistfull) {
                                if (item.getCategory().contains(filterPattern)) {
                                    mStores.add(item);
                                }
                            }

                            mAdapter.notifyDataSetChanged();


                        } else if (checkedId == R.id.fruit_veggies_chip) {
                            filterPattern = "Fruit Market";
                            String filterPattern2 = "Vegetable Market";
                            System.out.println("Fruits chip checked");

                            for (ModelStore item : mStoreslistfull) {
                                if (item.getCategory().contains(filterPattern) || (item.getCategory().contains(filterPattern2))) {
                                    mStores.add(item);
                                }
                            }

                            mAdapter.notifyDataSetChanged();

                        } else if (checkedId == R.id.groceries_chip) {
                            filterPattern = "Groceries";
                            System.out.println("Groceries chip checked");

                            for (ModelStore item : mStoreslistfull) {
                                if (item.getCategory().contains(filterPattern)) {
                                    mStores.add(item);
                                }
                            }

                            mAdapter.notifyDataSetChanged();
                        } else if (checkedId == R.id.medical_chip) {
                            filterPattern = "Medical";
                            System.out.println("Medical chip checked");

                            for (ModelStore item : mStoreslistfull) {
                                if (item.getCategory().contains(filterPattern)) {
                                    mStores.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();

                        } else if (checkedId == -1) {
                            noItems.setVisibility(View.GONE);
                            mStores.addAll(mStoreslistfull);
                            mAdapter.notifyDataSetChanged();
                            System.out.println("No chip checked");
                        }


                        if (mStores.isEmpty()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    noItems.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    }
                });


            }
        });

        return view;

    }

    private void getMylocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!= null)
                {
//
//                    Location dmart=new Location("");
//                    dmart.setLatitude(18.566758);
//                    dmart.setLongitude(73.807233);
//                    double distance = location.distanceTo(dmart);
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        Log.d("location", "onSuccess: PinCode is "+addresses.get(0).getPostalCode());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(getActivity(),new String[]{ACCESS_FINE_LOCATION},1);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.appbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Nearby Stores");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
