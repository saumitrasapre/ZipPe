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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
    }

    @Override
    public void handleResult(Result result) {
        Intent myIntent = getIntent();
        String store_id = myIntent.getStringExtra("Store_id");
        System.out.println("Scanning result is " + store_id);

        System.out.println(result.getText());
        pd.show();
        CollectionReference product = db.collection("Stores").document(store_id).collection("Items");
        product.whereEqualTo("productCode", result.getText())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                            Log.d("datafetch", "onEvent: Error fetching data " + e.toString());
                            onBackPressed();
                            pd.dismiss();
                        } else {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Log.d("fetchstores", "onSuccess: List Empty ");
                                onBackPressed();
                                pd.dismiss();
                                return;
                            } else {
                                itemList = queryDocumentSnapshots.getDocuments();
                                ModelCart cartItem = itemList.get(0).toObject(ModelCart.class);
                                cartItem.setProductQuantity(1);
                                cartItem.setStoreId(store_id);
                                cart.document(result.getText()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d("ItemExistence", "Document exists!");
                                                Map<Object, Long> map = new HashMap<>();
                                                map.put("productQuantity", (Long) document.get("productQuantity")+1);
                                                cart.document(document.getId()).set(map, SetOptions.merge());
                                                pd.dismiss();
                                                onBackPressed();

                                            } else {
                                                Log.d("ItemExistence", "Document does not exist!");
                                                cart.document(result.getText()).set(cartItem, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Cart Item added", "onSuccess: Cart item added");
                                                        pd.dismiss();
                                                        onBackPressed();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Cart Item added", "onFailure:Failed to add cart item " + e.toString());
                                                        Toast.makeText(ScanCode.this, "Failed to add cart item", Toast.LENGTH_SHORT).show();
                                                        pd.dismiss();
                                                        onBackPressed();
                                                    }
                                                });
                                            }
                                        } else {
                                            Log.d("ItemExistence", "Failed with: ", task.getException());
                                        }
                                    }
                                });
                                /**/
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