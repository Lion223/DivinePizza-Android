package com.github.lion223.divinepizza.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.lion223.divinepizza.Adapters.OrderDetailsTabsPagerAdapter;
import com.github.lion223.divinepizza.CustomToast;
import com.github.lion223.divinepizza.FirebaseCallback;
import com.github.lion223.divinepizza.Fragments.CourierFragment;
import com.github.lion223.divinepizza.Fragments.DrinksFragment;
import com.github.lion223.divinepizza.Fragments.PickupFragment;
import com.github.lion223.divinepizza.Fragments.PizzasFragment;
import com.github.lion223.divinepizza.R;
import com.github.lion223.divinepizza.Fragments.SaladsFragment;
import com.github.lion223.divinepizza.Adapters.MainTabsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class OrderDetailsActivity extends AppCompatActivity
        implements PickupFragment.OnFragmentInteractionListener,
        CourierFragment.OnFragmentInteractionListener {

   private Toolbar toolbar;
   private DrawerLayout drawerLayout;
   private NavigationView navView;
   private String userName;
   private String userPhoneNumber;

   private ListenerRegistration listener;

   private FirebaseUser currentUser;

   private CustomToast cToast;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_order_details);


      TabLayout tabLayout = findViewById(R.id.orderdetails_tab_layout);
      ViewPager viewPager = findViewById(R.id.orderdetails_view_pager);

      OrderDetailsTabsPagerAdapter orderDetailsTabsPagerAdapter = new OrderDetailsTabsPagerAdapter(getSupportFragmentManager());
      viewPager.setAdapter(orderDetailsTabsPagerAdapter);
      tabLayout.setupWithViewPager(viewPager);

      setToolbar();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
      return true;
   }

   @Override
   public boolean onSupportNavigateUp() {
      onBackPressed();
      return true;
   }


   @Override
   public void onFragmentInteraction(Uri uri){
      //you can leave it empty
   }

   private void setToolbar() {
      toolbar = findViewById(R.id.orderdetails_toolbar);
      toolbar.setTitle("Спосіб оплати");
      setSupportActionBar(toolbar);
   }

   @Override
   public void onStop() {
      super.onStop();
      listener.remove();
   }
}
