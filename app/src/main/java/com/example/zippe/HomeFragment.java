package com.example.zippe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public  class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ModelStore>storelist=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home,container,false);
        recyclerView=view.findViewById(R.id.stores);
        storelist.add(new ModelStore(R.drawable.dmart,"DMart","Hell","2 rupees"));
        storelist.add(new ModelStore(R.drawable.dmart,"Bhaji Mandi","PICT","2 rupees"));
        storelist.add(new ModelStore(R.drawable.dmart,"More","Budhwar Galli","2 rupees"));
        storelist.add(new ModelStore(R.drawable.dmart,"Reliance Fresh","Heaven","2 rupees"));
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager manager=layoutManager;
        recyclerView.setLayoutManager(manager);
        StoreAdapter adapter=new StoreAdapter(getContext(),storelist);
        recyclerView.setAdapter(adapter);
        return view;

    }
}
