package com.example.humayan;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = "ActivityLifecycle";
    private Handler handler = new Handler();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Set up NavigationView and its item selection listener
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load the default fragment (if needed)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentDashboard())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentDashboard()).commit();
        }

        // Updated method to set the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();
                if (id == R.id.nav_dashboard) {
                    selectedFragment = new FragmentDashboard(); // Assuming FragmentTwo is another fragment
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });

//        Log.d(TAG, "onCreate called");
//
//        // Display a toast with 3 seconds delay
//        handler.postDelayed(() -> {
//            Toast.makeText(MainActivity.this, "onCreate called", Toast.LENGTH_LONG).show();
//        }, 3000);
    }





    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        // Determine which fragment to show based on the menu item selected
        if (id == R.id.nav_fragment_dashboard) {
            fragment = new FragmentDashboard();
        }
//        else if (id == R.id.nav_fragment_two) {
//            fragment = new FragmentTwo();  // Add more fragments as needed
//        } else if(id == R.id.nav_fragment_three){
//            fragment = new FragmentThree();
//        } else if(id == R.id.nav_fragment_four){
//            fragment = new FragmentFour();
//        } else if (id == R.id.nav_fragment_five){
//            fragment = new FragmentFive();
//        }
        else if (id == R.id.nav_fragment_settings){
            fragment = new FragmentSettings();
        }

        // Replace the fragment if it exists
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }

        // Close the navigation drawer after an item is selected
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart called");
//
//        handler.postDelayed(() -> {
//            Toast.makeText(MainActivity.this, "onStart called", Toast.LENGTH_LONG).show();
//        }, 3000);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume called");
//
//        handler.postDelayed(() -> {
//            Toast.makeText(MainActivity.this, "onResume called", Toast.LENGTH_LONG).show();
//        }, 3000);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause called");
//
//        handler.postDelayed(() -> {
//            Toast.makeText(MainActivity.this, "onPause called", Toast.LENGTH_LONG).show();
//        }, 3000);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop called");
//
//        handler.postDelayed(() -> {
//            Toast.makeText(MainActivity.this, "onStop called", Toast.LENGTH_LONG).show();
//        }, 3000);
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d(TAG, "onRestart called");
//
//        handler.postDelayed(() -> {
//            Toast.makeText(MainActivity.this, "onRestart called", Toast.LENGTH_LONG).show();
//        }, 3000);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy called");
//
//        handler.postDelayed(() -> {
//            Toast.makeText(MainActivity.this, "onDestroy called", Toast.LENGTH_LONG).show();
//        }, 3000);
//    }
}