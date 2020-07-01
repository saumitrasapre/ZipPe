package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private Toolbar toolbar;
    private FloatingActionButton scan_new;
    private CartAdapter mCartAdapter;
    private SwipeRefreshLayout swipeRefreshCart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference cart = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart");
    private List<ModelCart> mCart = new ArrayList<>();
    private List<ModelCart> mCartlistfull;
    private TextView emptyCart;
    private boolean cartEmpty=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        emptyCart = findViewById(R.id.emptyCart);
        Intent intent = getIntent();
        String store_id = intent.getStringExtra("Store_id");
        System.out.println("Barcode result is " + store_id);
        swipeRefreshCart = findViewById(R.id.swipeRefreshCart);
        toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler_cart = (RecyclerView) findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        scan_new = (FloatingActionButton) findViewById(R.id.scan_new);
        scan_new = (FloatingActionButton) findViewById(R.id.scan_new);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler_cart);

        loadCartItems();

        scan_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent id = new Intent(getApplicationContext(), ScanCode.class);
                id.putExtra("Store_id", store_id);
                startActivity(id);
            }
        });

        swipeRefreshCart.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refeshCartItems();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cart_appbar_menu, menu);
        MenuItem item = menu.findItem(R.id.checkout);
        if(cartEmpty==false)
        {
            item.setEnabled(true);
            Drawable resIcon = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24);
            item.setIcon(resIcon);

        }
        else if(cartEmpty==true)
        {
            item.setEnabled(false);
            Drawable resIcon = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24);
            resIcon.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            item.setIcon(resIcon);
            //Toast.makeText(getApplicationContext(),"Cart is empty- Cannot checkout",Toast.LENGTH_SHORT).show();
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.checkout:

                if(cartEmpty==false)
                {
                    Intent intent = new Intent(getApplicationContext(), Checkout.class);
                    startActivity(intent);
                    return true;
                }
                else if(cartEmpty==true)
                {
                    Toast.makeText(getApplicationContext(),"Cart is empty- Cannot checkout",Toast.LENGTH_SHORT).show();
                    return true;
                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refeshCartItems() {

        cart.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mCart.clear();

                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("fetchcart", "onSuccess:Cart List Empty ");
                    emptyCart.setVisibility(View.VISIBLE);
                    swipeRefreshCart.setRefreshing(false);
                    cartEmpty=true;
                    invalidateOptionsMenu();
                    return;
                } else {
                    emptyCart.setVisibility(View.GONE);
                    List<ModelCart> temp = queryDocumentSnapshots.toObjects(ModelCart.class);
                    mCart.addAll(temp);
                    mCartlistfull = new ArrayList<>(mCart);
                    cartEmpty=false;
                    Log.d("fetchcart", "onSuccess: Cart List Fetched ");
                    mCartAdapter = new CartAdapter(getApplicationContext(), mCart, barcode.this);
                    mCartAdapter.notifyDataSetChanged();
                    recycler_cart.setAdapter(mCartAdapter);
                    invalidateOptionsMenu();
                    swipeRefreshCart.setRefreshing(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error fetching cart", Toast.LENGTH_SHORT).show();
                Log.d("cartfetch", "onEvent: Error fetching cart " + e.toString());
                swipeRefreshCart.setRefreshing(false);
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
                        Log.d("fetchCart", "onSuccess:Cart List Empty ");
                        emptyCart.setVisibility(View.VISIBLE);
                        cartEmpty=true;
                        invalidateOptionsMenu();
                        return;
                    } else {
                        emptyCart.setVisibility(View.GONE);
                        List<ModelCart> temp = queryDocumentSnapshots.toObjects(ModelCart.class);
                        mCart.addAll(temp);
                        mCartlistfull = new ArrayList<>(mCart);
                        Log.d("fetchcart", "onSuccess: Cart List Fetched ");
                        mCartAdapter = new CartAdapter(getApplicationContext(), mCart, barcode.this);
                        mCartAdapter.notifyDataSetChanged();
                        recycler_cart.setAdapter(mCartAdapter);
                        invalidateOptionsMenu();
                        cartEmpty=false;
                    }
                }

            }
        });


    }


    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
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