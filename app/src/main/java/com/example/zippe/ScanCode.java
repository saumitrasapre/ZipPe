package com.example.zippe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Time;
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
    ZXingScannerView mScannerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        mScannerView =  (ZXingScannerView)findViewById(R.id.zxscan);
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
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(mScannerView.getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout_2, (LinearLayout)findViewById(R.id.bottomSheetContainer));
        bottomSheetDialog.setCancelable(false);
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
                        } else {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Log.d("fetchstores", "onSuccess: List Empty ");
                                Toast.makeText(ScanCode.this, "Invalid Barcode", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                                mScannerView.stopCamera();
                                onBackPressed();
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

                                                TextView productname=bottomSheetView.findViewById(R.id.productname);
                                                productname.setText(cartItem.getProductName());
                                                ImageView productimage=bottomSheetView.findViewById(R.id.product_image);
                                                Picasso.get()
                                                        .load(cartItem.getProductImage())
                                                        .placeholder(R.color.lightgrey)
                                                        .into(productimage);
                                                TextView productWeight=bottomSheetView.findViewById(R.id.weight);
                                                productWeight.setText(cartItem.getProductWeight());
                                                TextView price=bottomSheetView.findViewById(R.id.price);
                                                price.setText("₹ "+cartItem.getProductPrice()+" /-");
                                                pd.dismiss();
                                                bottomSheetDialog.setContentView(bottomSheetView);
                                                bottomSheetDialog.show();
                                                bottomSheetView.findViewById(R.id.bottom_add).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Log.d("TAG","CLICKED");
                                                        pd.show();
                                                        cart.document(document.getId()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("Cart Item added", "onSuccess: Cart item added");
                                                                pd.dismiss();
                                                                mScannerView.stopCamera();
                                                                bottomSheetDialog.dismiss();
                                                                onBackPressed();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("Cart Item added", "onFailure:Failed to add cart item " + e.toString());
                                                                Toast.makeText(ScanCode.this, "Failed to add cart item", Toast.LENGTH_SHORT).show();
                                                                pd.dismiss();
                                                                mScannerView.stopCamera();
                                                                bottomSheetDialog.dismiss();
                                                                onBackPressed();
                                                            }
                                                        });
                                                    }
                                                });
                                                bottomSheetView.findViewById(R.id.close_bottom_sheet).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        mScannerView.stopCamera();
                                                        bottomSheetDialog.dismiss();
                                                        pd.dismiss();
                                                        onBackPressed();
                                                    }
                                                });

                                            } else {
                                                Log.d("ItemExistence", "Document does not exist!");
                                                TextView productname=bottomSheetView.findViewById(R.id.productname);
                                                productname.setText(cartItem.getProductName());
                                                ImageView productimage=bottomSheetView.findViewById(R.id.product_image);
                                                Picasso.get()
                                                        .load(cartItem.getProductImage())
                                                        .placeholder(R.color.lightgrey)
                                                        .into(productimage);

                                                TextView price=bottomSheetView.findViewById(R.id.price);
                                                price.setText("₹ "+cartItem.getProductPrice()+" /-");
                                                TextView productWeight=bottomSheetView.findViewById(R.id.weight);
                                                productWeight.setText(cartItem.getProductWeight());
                                                pd.dismiss();
                                                bottomSheetDialog.setContentView(bottomSheetView);
                                                bottomSheetDialog.show();
                                                bottomSheetView.findViewById(R.id.bottom_add).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Log.d("TAG","CLICKED");
                                                        pd.show();
                                                        cart.document(result.getText()).set(cartItem, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("Cart Item added", "onSuccess: Cart item added");
                                                                pd.dismiss();
                                                                mScannerView.stopCamera();
                                                                bottomSheetDialog.dismiss();
                                                                onBackPressed();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("Cart Item added", "onFailure:Failed to add cart item " + e.toString());
                                                                Toast.makeText(ScanCode.this, "Failed to add cart item", Toast.LENGTH_SHORT).show();
                                                                pd.dismiss();
                                                                mScannerView.stopCamera();
                                                                bottomSheetDialog.dismiss();
                                                                onBackPressed();
                                                            }
                                                        });

                                                    }
                                                });
                                                bottomSheetView.findViewById(R.id.close_bottom_sheet).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        mScannerView.stopCamera();
                                                        bottomSheetDialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                });

                                            }
                                        } else {
                                            Log.d("ItemExistence", "Failed with: ", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                    }
                });


    }

    @Override
    protected void onPause() {

        super.onPause();
       // bottomSheetDialog.dismiss();
       mScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }
}