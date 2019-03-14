package com.github.lion223.divinepizza;

import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private LinearLayout layout;
    private ActionBarDrawerToggle dToggle;

    private CustomToast cToast;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(dToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()) {
            case R.id.shoppingcart:
                cToast.show("kek");
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        layout = findViewById(R.id.layout);

        cToast = new CustomToast(Color.rgb(28,28,28), Color.WHITE, this, new Toast(getApplicationContext()));
        setToolbar();
        configureNavigationDrawer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*
            case R.id.sign_out:
                signOut();
                break;

            case R.id.get_user:
                getInfo();
                break;

            default:
                break;
                        */
        }

    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        toolbar.bringToFront();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

    private void configureNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        dToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(dToggle);
        dToggle.syncState();
        navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        TextView kek = headerView.findViewById(R.id.name);
        kek.setText("Hello");
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                /*
                Fragment f = null;
                int itemId = menuItem.getItemId();
                if (itemId == R.id.refresh) {
                    f = new RefreshFragment();
                } else if (itemId == R.id.stop) {
                    f = new StopFragment();
                }
                if (f != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, f);
                    transaction.commit();
                    drawerLayout.closeDrawers();
                    return true;
                }
                return false;
            */
                return true;
            }

        });
    }

    private void getInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            cToast.show("User is in");
        } else {
            cToast.show("User is none");
        }
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        cToast.show("Signed out");
    }

}
