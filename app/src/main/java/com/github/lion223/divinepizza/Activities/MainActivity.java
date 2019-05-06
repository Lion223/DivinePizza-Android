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

import com.github.lion223.divinepizza.CustomToast;
import com.github.lion223.divinepizza.FirebaseCallback;
import com.github.lion223.divinepizza.Fragments.DrinksFragment;
import com.github.lion223.divinepizza.Fragments.PizzasFragment;
import com.github.lion223.divinepizza.R;
import com.github.lion223.divinepizza.Fragments.SaladsFragment;
import com.github.lion223.divinepizza.Adapters.TabsPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PizzasFragment.OnFragmentInteractionListener,
        SaladsFragment.OnFragmentInteractionListener,
        DrinksFragment.OnFragmentInteractionListener {

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
        setContentView(R.layout.activity_main);


        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        ViewPager viewPager = findViewById(R.id.main_view_pager);

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setToolbar();
        getUserName(new FirebaseCallback() {
            @Override
            public void onCallback(Object data) {
                userName = (String) data;
                configureNavigationDrawer();

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.favorite_orders) {
            // Handle the camera action
        } else if (id == R.id.order_history) {

        } else if (id == R.id.location) {

        } else if (id == R.id.settings) {

        } else if (id == R.id.log_out) {

        } else if (id == R.id.about) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.pizza_product_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void getUserName(@NonNull final FirebaseCallback firebaseCallback){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            CollectionReference users = FirebaseFirestore.getInstance().collection("users");
            userPhoneNumber = currentUser.getPhoneNumber();
            listener = users.whereEqualTo("phone_number", userPhoneNumber).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(!queryDocumentSnapshots.isEmpty()){
                        userName = queryDocumentSnapshots.getDocuments().get(0).get("name").toString();
                        configureNavigationDrawer();
                    }
                }
            });


            /*
            users.whereEqualTo("phone_number", userPhoneNumber).get().
            users.whereEqualTo("phone_number", userPhoneNumber)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot qs = task.getResult();
                                firebaseCallback.onCallback(qs.getDocuments().get(0).get("name").toString());
                            }
                        }
                    });
                    */
        }
    }

    private void configureNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView = findViewById(R.id.main_nav_view);
        navView.setNavigationItemSelectedListener(this);
        View headerView = navView.getHeaderView(0);

        TextView TV_userName = headerView.findViewById(R.id.nav_header_user_name);
        TextView TV_userPhoneNumber = headerView.findViewById(R.id.nav_header_user_phone_number);
        TV_userName.setText(userName);
        TV_userPhoneNumber.setText(userPhoneNumber);
    }


    @Override
    public void onStop() {
        super.onStop();
        listener.remove();
    }
}
