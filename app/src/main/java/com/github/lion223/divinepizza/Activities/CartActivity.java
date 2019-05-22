package com.github.lion223.divinepizza.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.lion223.divinepizza.Adapters.CartProductAdapter;
import com.github.lion223.divinepizza.Adapters.PizzaProductAdapter;
import com.github.lion223.divinepizza.Models.CartProductModel;
import com.github.lion223.divinepizza.Models.PizzaProductModel;
import com.github.lion223.divinepizza.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TextView title;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private CartProductAdapter adapter;
    private FirebaseUser currentUser;
    private CollectionReference cartRef;

    private RelativeLayout pricePanelLayout;
    private Button continueBtn;
    private TextView totalPriceTV;

    private ArrayList<String> order;

    private final String FB_TAG = "Firebase: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        pricePanelLayout = findViewById(R.id.price_panel);
        continueBtn = findViewById(R.id.continue_btn);
        totalPriceTV = findViewById(R.id.total_price);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        setToolbar();
        getCartReference();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, OrderDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCartReference(){
        usersRef.whereEqualTo("phone_number",
                currentUser.getPhoneNumber()).get().addOnCompleteListener(CartActivity.this,
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot qs = task.getResult();
                            if(!qs.getDocuments().isEmpty()){
                                DocumentReference docRef = qs.getDocuments().get(0).getReference();
                                cartRef = docRef.collection("CurrentCart");
                                setUpRecyclerView();
                            }
                        }
                    }
                });
    }

    private void setUpRecyclerView(){
        Query query = cartRef.orderBy("price", Query.Direction.ASCENDING);
        cartRef.addSnapshotListener(CartActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots.getDocuments().isEmpty()){
                    pricePanelLayout.setVisibility(View.GONE);
                    continueBtn.setVisibility(View.GONE);
                }
                else{
                    pricePanelLayout.setVisibility(View.VISIBLE);
                    continueBtn.setVisibility(View.VISIBLE);
                    order = new ArrayList<String>();
                    Integer totalPrice = 0;
                    for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                        try{
                            CartProductModel item = ds.toObject(CartProductModel.class);
                            String price = item.getTotal_price();
                            order.add(item.getName() + ", " + item.getPrice());
                            totalPrice += Integer.parseInt(price);
                        } catch(NullPointerException exp){
                            Log.d(FB_TAG, "Got a Null exception getting \"total_price\" field value - " + exp.getMessage());
                        }
                    }
                    order.add(Integer.toString(totalPrice));
                    totalPriceTV.setText(getResources().getString(R.string.price, Integer.toString(totalPrice)));
                }
            }
        });

        FirestoreRecyclerOptions<CartProductModel> options = new FirestoreRecyclerOptions.Builder<CartProductModel>()
                .setQuery(query, CartProductModel.class)
                .build();

        adapter = new CartProductAdapter(options);
        adapter.startListening();
        RecyclerView recyclerView = findViewById(R.id.cart_recycler_view);
        if(recyclerView == null){
            return;
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new CartProductAdapter.OnItemClickListener() {
            @Override
            public void OnImageClick(DocumentSnapshot documentSnapshot, int position) {

            }

            @Override
            public void OnBtnClick(DocumentSnapshot documentSnapshot, String quantity, String totalPrice) {
                DocumentReference docRef = documentSnapshot.getReference();
                docRef.update("quantity", quantity);
                docRef.update("total_price", totalPrice);
            }

        });
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.cart_toolbar);
        toolbar.setTitle("");
        title = findViewById(R.id.cart_title);
        title.setText("Кошик");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.shopping_cart) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
