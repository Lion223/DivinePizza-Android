package com.github.lion223.divinepizza.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.github.lion223.divinepizza.Models.SaladProductModel;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import javax.annotation.Nullable;

public class SaladProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

   private Toolbar toolbar;
   private String productId;
   private FirebaseFirestore db = FirebaseFirestore.getInstance();;
   private CollectionReference saladsRef = db.collection("salads");
   private FirebaseUser currentUser;
   private CollectionReference currentCart;

   private TextView productName, productWeight, productDesc, productPrice, productRating, productQuantity;
   private Button productAdd, productMinus, productBuy;
   private ImageView productPic;
   private LinearLayout productMeat, productFruit, productVeg, productSea;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_salad_product);
      currentUser = FirebaseAuth.getInstance().getCurrentUser();

      productId = getIntent().getStringExtra("pid");

      productName = findViewById(R.id.salad_product_title);
      productWeight = findViewById(R.id.salad_product_weight);
      productDesc = findViewById(R.id.salad_item_desc);
      productPic = findViewById(R.id.salad_product_pic);
      productPrice = findViewById(R.id.salad_item_price);
      productMeat = findViewById(R.id.salad_item_meat_type);
      productFruit = findViewById(R.id.salad_item_fruit_type);
      productVeg = findViewById(R.id.salad_item_vegetable_type);
      productSea = findViewById(R.id.salad_item_seafood_type);
      productRating = findViewById(R.id.salad_product_rating_mark);
      productAdd = findViewById(R.id.salad_item_add_button);
      productMinus = findViewById(R.id.salad_item_minus_button);
      productQuantity = findViewById(R.id.salad_item_quantity_button);
      productBuy = findViewById(R.id.salad_product_buy_button);

      setToolbar();
      getProductDetails(productId);

      productDesc.setMovementMethod(new ScrollingMovementMethod());

      productAdd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            int quantity = Integer.parseInt(productQuantity.getText().toString());
            if(quantity < 99){
               quantity++;
               int prev_price = Integer.parseInt(productPrice.getText().toString().substring(1));
               productQuantity.setText(Integer.toString(quantity));
               productPrice.setText(App.getContext().getResources().getString(R.string.price,
                       Integer.toString(prev_price + prev_price / (quantity - 1))));
            }
         }
      });

      productMinus.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            int quantity = Integer.parseInt(productQuantity.getText().toString());
            if(quantity > 1){
               quantity--;
               int prev_price = Integer.parseInt(productPrice.getText().toString().substring(1));
               productPrice.setText(App.getContext().getResources().getString(R.string.price,
                       Integer.toString(prev_price - prev_price / (quantity + 1))));
               productQuantity.setText(Integer.toString(quantity));
            }
         }
      });

      productBuy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            int quantity = Integer.parseInt(productQuantity.getText().toString());
            int itemPrice = Integer.parseInt(productPrice.getText().toString().substring(1)) / quantity;

            final HashMap<String, Object> product = new HashMap<>();
            product.put("name", productName.getText());
            product.put("price", Integer.toString(itemPrice));
            product.put("quantity", String.valueOf(quantity));
            product.put("total_price", productPrice.getText().toString().substring(1));
            if (currentUser != null) {
               db.collection("users").whereEqualTo("phone_number",
                       currentUser.getPhoneNumber()).get().addOnCompleteListener(SaladProductActivity.this,
                       new OnCompleteListener<QuerySnapshot>() {
                          @Override
                          public void onComplete(@NonNull Task<QuerySnapshot> task) {
                             if (task.isSuccessful()) {
                                QuerySnapshot qs = task.getResult();
                                DocumentReference docRef = qs.getDocuments().get(0).getReference();
                                currentCart = docRef.collection("CurrentCart");
                                currentCart.document("item_" + productId).set(product, SetOptions.merge());
                             }
                          }
                       });
            }

         }
      });
   }

   private void getProductDetails(String productId){
      saladsRef.document(productId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
         @Override
         public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            if(documentSnapshot.exists()){
               SaladProductModel product = documentSnapshot.toObject(SaladProductModel.class);
               Picasso.get().load(product.getImage()).into(productPic);
               productName.setText(product.getName());
               productWeight.setText(getResources().getString(R.string.weight, product.getWeight()));
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
                  productFruit.setVisibility(View.GONE);
               }
               else{
                  productFruit.setVisibility(View.VISIBLE);
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
      toolbar = findViewById(R.id.salad_product_toolbar);
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
         Intent intent = new Intent(SaladProductActivity.this, CartActivity.class);
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
