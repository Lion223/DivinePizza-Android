package com.github.lion223.divinepizza.Activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lion223.divinepizza.App;
import com.github.lion223.divinepizza.Models.PizzaProductModel;
import com.github.lion223.divinepizza.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class PizzaProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private String productId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private CollectionReference pizzasRef = db.collection("pizzas");

    private TextView productName, productWeight, productDiam, productDesc, productPrice, productRating, productBuy;
    private Button productAdd, productMinus;
    private ImageView productPic;
    private LinearLayout productMeat, productCheese, productVeg, productSea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_product);
        productId = getIntent().getStringExtra("pid");

        productName = findViewById(R.id.pizza_product_title);
        productWeight = findViewById(R.id.pizza_product_weight);
        productDiam = findViewById(R.id.pizza_product_diameter);
        productDesc = findViewById(R.id.pizza_product_desc);
        productPic = findViewById(R.id.pizza_product_pic);
        productPrice = findViewById(R.id.cart_product_price);
        productMeat = findViewById(R.id.pizza_product_meat_type);
        productCheese = findViewById(R.id.pizza_product_cheese_type);
        productVeg = findViewById(R.id.pizza_product_vegetable_type);
        productSea = findViewById(R.id.pizza_product_seafood_type);
        productRating = findViewById(R.id.pizza_product_rating_mark);
        productAdd = findViewById(R.id.cart_product_add_button);
        productMinus = findViewById(R.id.cart_product_minus_button);
        productBuy = findViewById(R.id.cart_product_quantity_button);

        setToolbar();
        getProductDetails(productId);

        productDesc.setMovementMethod(new ScrollingMovementMethod());

        productAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(productBuy.getText().toString());
                if(quantity < 99){
                    quantity++;
                    int prev_price = Integer.parseInt(productPrice.getText().toString().substring(1));
                    productBuy.setText(Integer.toString(quantity));
                    productPrice.setText(App.getContext().getResources().getString(R.string.price,
                            Integer.toString(prev_price + prev_price / (quantity - 1))));
                }
            }
        });

        productMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(productBuy.getText().toString());
                if(quantity > 1){
                    quantity--;
                    int prev_price = Integer.parseInt(productPrice.getText().toString().substring(1));
                    productPrice.setText(App.getContext().getResources().getString(R.string.price,
                            Integer.toString(prev_price - prev_price / (quantity + 1))));
                    productBuy.setText(Integer.toString(quantity));
                }
            }
        });
    }

    private void getProductDetails(String productId){
        pizzasRef.document(productId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    PizzaProductModel product = documentSnapshot.toObject(PizzaProductModel.class);
                    Picasso.get().load(product.getImg_full_url()).into(productPic);
                    productName.setText(product.getName());
                    productWeight.setText(getResources().getString(R.string.weight, product.getWeight()));
                    productDiam.setText(getResources().getString(R.string.diameter, product.getDiameter()));
                    productDesc.setText(product.getDescription());
                    productPrice.setText(getResources().getString(R.string.price, product.getPrice()));
                    productRating.setText(product.getRating());


                    if(product.getTypes().get(0) == false){
                        productMeat.setVisibility(View.GONE);
                    }
                    else{
                        productMeat.setVisibility(View.VISIBLE);
                    }
                    if(product.getTypes().get(1) == false){
                        productCheese.setVisibility(View.GONE);
                    }
                    else{
                        productCheese.setVisibility(View.VISIBLE);
                    }
                    if(product.getTypes().get(2) == false){
                        productVeg.setVisibility(View.GONE);
                    }
                    else{
                        productVeg.setVisibility(View.VISIBLE);
                    }
                    if(product.getTypes().get(3) == false){
                        productSea.setVisibility(View.GONE);
                    }
                    else{
                        productSea.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.pizza_product_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.shopping_cart) {
            Intent intent = new Intent(PizzaProductActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        }

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
}
