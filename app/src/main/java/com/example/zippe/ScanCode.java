package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.util.List;

import Models.ModelCart;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCode extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView ScannerView;
    private ProgressDialog pd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference cart = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart");

    private List<DocumentSnapshot> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);


        pd = new ProgressDialog(getApplicationContext());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
    }

    @Override
    public void handleResult(Result result) {
        Intent myIntent = getIntent();
        String store_id = myIntent.getStringExtra("Store_id");
        System.out.println("Scanning result is "+store_id);

        System.out.println(result.getText());
       // pd.show();
        CollectionReference product = db.collection("Stores").document(store_id).collection("Items");
        product.whereEqualTo("productCode",result.getText())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                            Log.d("datafetch", "onEvent: Error fetching data " + e.toString());
                            onBackPressed();
                           // pd.dismiss();
                        }
                        else {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Log.d("fetchstores", "onSuccess: List Empty ");
                                onBackPressed();
                               // pd.dismiss();
                                return;
                            } else {
                                itemList=queryDocumentSnapshots.getDocuments();
                                ModelCart cartItem=itemList.get(0).toObject(ModelCart.class);
                                cartItem.setStoreId(store_id);
                                cartItem.setProductQuantity(1);
                                cart.document().set(cartItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Cart Item added", "onSuccess: Cart item added");
                                     //   pd.dismiss();
                                        onBackPressed();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Cart Item added", "onFailure:Failed to add cart item "+e.toString());
                                        Toast.makeText(ScanCode.this, "Failed to add cart item", Toast.LENGTH_SHORT).show();
                                       // pd.dismiss();
                                        onBackPressed();
                                    }
                                });
                            }
                        }
                    }
                });

        //onBackPressed();
        ///MainActivity.resultTextView.setText(result.getText());

    }

    @Override
    protected void onPause() {

        super.onPause();

        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }
}