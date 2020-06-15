package com.example.zippe;

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
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Models.ModelStore;

public  class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoreAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private List<ModelStore> mStores;
    private CollectionReference storedb=db.collection("Stores");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home,container,false);
        recyclerView=view.findViewById(R.id.stores);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mProgressCircle=view.findViewById(R.id.progress_circle);
        setHasOptionsMenu(true);

        mStores=new ArrayList<ModelStore>();
        storedb.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null)
                {
                    Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    Log.d("datafetch", "onEvent: Error fetching data "+e.toString());
                    mProgressCircle.setVisibility(View.GONE);
                }
                else
                {
                    if(queryDocumentSnapshots.isEmpty())
                    {
                        Log.d("fetchstores", "onSuccess:Store List Empty ");
                        mProgressCircle.setVisibility(View.GONE);
                        return;
                    }
                    else
                    {
                        List<ModelStore> temp =queryDocumentSnapshots.toObjects(ModelStore.class);
                        mStores.addAll(temp);
                       // System.out.println(mStores.get(0).getName());
                        Log.d("fetchstores", "onSuccess: Store List Fetched ");
                        mAdapter=new StoreAdapter(getContext(),mStores);
                        //adapter.update(mStores)
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);
                        mProgressCircle.setVisibility(View.GONE);

                    }
                }
            }
        });

        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.appbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView=(SearchView) searchItem.getActionView();
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
