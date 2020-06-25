package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Models.ModelCart;
import Models.ModelStore;

public class barcode extends AppCompatActivity {

    private RecyclerView recycler_cart;
    private FloatingActionButton scan_new;
    private CartAdapter mCartAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference cart = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart");
    private List<ModelCart> mCart=new ArrayList<>();
    private List<ModelCart> mCartlistfull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        Intent intent = getIntent();
        String store_id = intent.getStringExtra("Store_id");
        System.out.println("Barcode result is "+store_id);

        recycler_cart = (RecyclerView) findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        scan_new = (FloatingActionButton) findViewById(R.id.scan_new);
        scan_new = (FloatingActionButton) findViewById(R.id.scan_new);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler_cart);

        loadCartItems();

        scan_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent id = new Intent(getApplicationContext(),ScanCode.class);
                id.putExtra("Store_id", store_id);
                startActivity(id);
            }
        });

    }


    private void loadCartItems() {


        cart.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                mCart.clear();
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Error fetching cart", Toast.LENGTH_SHORT).show();
                    Log.d("cartfetch", "onEvent: Error fetching cart " + e.toString());
                } else {

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("fetchstores", "onSuccess:Store List Empty ");
                        return;
                    } else {
                        List<ModelCart> temp = queryDocumentSnapshots.toObjects(ModelCart.class);
                        mCart.addAll(temp);
                        mCartlistfull = new ArrayList<>(mCart);
                        Log.d("fetchcart", "onSuccess: Cart List Fetched ");
                        mCartAdapter = new CartAdapter(getApplicationContext(), mCart);
                        mCartAdapter.notifyDataSetChanged();
                        recycler_cart.setAdapter(mCartAdapter);
                    }
                }

            }
        });


    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            cart.document(mCart.get(viewHolder.getAdapterPosition()).getProductCode()).delete();
            mCart.remove(viewHolder.getAdapterPosition());
            mCartAdapter.notifyDataSetChanged();
        }
    };
}