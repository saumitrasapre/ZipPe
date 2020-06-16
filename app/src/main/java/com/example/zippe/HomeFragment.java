package com.example.zippe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Models.ModelStore;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoreAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private ChipGroup sortChipGroup;
    private TextView noItems;
    private Chip superMarket_chip, shoppingMall_chip, electronics_chip, clothing_chip, fruitsVeggies_chip, groceries_chip, medical_chip;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<ModelStore> mStores;
    private List<ModelStore> mStoreslistfull;
    private CollectionReference storedb = db.collection("Stores");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.stores);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noItems = view.findViewById(R.id.no_items);

        sortChipGroup = view.findViewById(R.id.sort_chip_group);

       /* superMarket_chip = view.findViewById(R.id.supermarket_chip);
        shoppingMall_chip = view.findViewById(R.id.shoppingmall_chip);
        electronics_chip = view.findViewById(R.id.electronics_chip);
        clothing_chip = view.findViewById(R.id.clothing_chip);
        fruitsVeggies_chip = view.findViewById(R.id.fruit_veggies_chip);
        groceries_chip = view.findViewById(R.id.groceries_chip);
        medical_chip = view.findViewById(R.id.medical_chip);*/

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
                        }
                        else if (checkedId == R.id.medical_chip) {
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

        /*superMarket_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    System.out.println("Supermarket chip checked");
                }
                else
                {
                    System.out.println("Supermarket chip unchecked");
                }

            }
        });

        shoppingMall_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked==true)
                {
                    System.out.println("Shopping Mall chip checked");
                }
                else
                {
                    System.out.println("Shopping Mall chip unchecked");
                }

            }
        });

        electronics_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    System.out.println("Electronics chip checked");
                }
                else
                {
                    System.out.println("Electronics chip unchecked");
                }
            }
        });

        clothing_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    System.out.println("Clothing chip checked");
                }
                else
                {
                    System.out.println("Clothing chip unchecked");
                }
            }
        });

        fruitsVeggies_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked==true)
                {
                    System.out.println("Fruits chip checked");
                }
                else
                {
                    System.out.println("Fruits chip unchecked");
                }
            }
        });

        groceries_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    System.out.println("Groceries chip checked");
                }
                else
                {
                    System.out.println("Groceries chip unchecked");
                }
            }
        });

        medical_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    System.out.println("Medical chip checked");
                }
                else
                {
                    System.out.println("Medical chip unchecked");
                }
            }
        });*/

        return view;

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
