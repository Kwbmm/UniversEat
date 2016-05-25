package it.polito.mad.groupFive.restaurantcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import it.polito.mad.groupFive.restaurantcode.Login.CreateLogin;
import it.polito.mad.groupFive.restaurantcode.Login.Login_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Customer;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CustomerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;

public class NavigationDrawer extends AppCompatActivity implements Login_view.OnFragmentInteractionListener {
    private int phase;//0 Logged out 1 logged in
    private boolean usertype;// false user true restaurant manager
    private User user;
    private ArrayAdapter<String> adapter;
    private DrawerLayout drawerLayout;
    private static int REGISTRATION=1;
    private ImageView imageView;
    SharedPreferences sharedPreferences;

    private ActionBarDrawerToggle mDrawerToggle;
    private ListView dList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // not a real activity, it's used to extend toolbar and navigation drawer to all activity created
        super.onCreate(savedInstanceState);
        getUserinfo();
        checkUser();
        createDrawer();
        //checkPic();
    }

    private void checkPic(){
        final String METHOD_NAME = this.getClass().getName()+" - checkPic";
        if(phase==1){
            if (usertype){
                //TODO Fix
                sharedPreferences.getString("uid",null);
                    user=new User ();
                    //this.imageView.setImageBitmap(user.getImageBitmap());

            }

        }}

    public ArrayAdapter<String> createAdapter() {
        ArrayAdapter<String> adp;
        String[] options;
        if (phase != 0) {
            if (usertype){
                options = getResources().getStringArray(R.array.drawer_option_logged_manager);}
            else{
                options=getResources().getStringArray(R.array.drawer_option_logged_user);
            }

        } else {
            options = getResources().getStringArray(R.array.drawer_option_login);

        }
        adp = new ArrayAdapter<String>(this, R.layout.list_item, options);
        return adp;
    }

    @Override
    protected void onStop() {
        drawerLayout.closeDrawers();
        super.onStop();
    }

    private void checkUser() {
        getUserinfo();
        if(phase==1){
            SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
            usertype=sharedPreferences.getBoolean("owner",false);
        }
    }

    public void createDrawer() {
        adapter = createAdapter();
        setContentView(R.layout.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        this.imageView = (ImageView) findViewById(R.id.iw);
        this.imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_picture));
        dList = (ListView) findViewById(R.id.left_drawer);
        dList.setAdapter(adapter);
        dList.setOnItemClickListener(new DrawerListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                view.bringToFront();
                hideSoftKeyboard();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                hideSoftKeyboard();
                drawerView.bringToFront();
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);
    }

    public void getUserinfo() {
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("logged", false)) {
            phase = 1;
        } else {
            phase = 0;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    @Override
    public void onFragmentInteraction() {

        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("logged",true);
        editor.apply();
        getUserinfo();
        checkUser();
        adapter.notifyDataSetChanged();
        dList.setAdapter(createAdapter());
        dList.deferNotifyDataSetChanged();
        Toast toast = Toast.makeText(getBaseContext(), "Login Completed", Toast.LENGTH_SHORT);
        toast.show();
        //checkPic();
        hideSoftKeyboard();
    }

    private class DrawerListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (phase == 0) {
                if (position == 0) {
                    Intent home = new Intent(getBaseContext(), Home.class);
                    startActivity(home);
                }
                if (position == 1) {
                    dList.setAdapter(createAdapter());
                    dList.deferNotifyDataSetChanged();
                    drawerLayout.closeDrawers();
                    Login_view lw=new Login_view();
                    // FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.frame,lw).commit();
                    //Todo intent create profile
                }
                if (position==2){
                    Intent registration=new Intent(getBaseContext(), CreateLogin.class);
                    startActivityForResult(registration,REGISTRATION);
                }

            } else {
                if(usertype){
                    if (position == 0) {
                        Intent home = new Intent(getBaseContext(), Home.class);
                        startActivity(home);
                    }
                    if (position == 1) {
                        Intent profile = new Intent(getBaseContext(), Profile.class);
                        startActivity(profile);

                    }
                    if (position == 3) {
                        Intent intent = new Intent(view.getContext(), RestaurantManagement.class);
                        startActivity(intent);
                    }
                    if (position == 2) {
                        Intent intent = new Intent(view.getContext(), Order_management.class);
                        startActivity(intent);
                    }
                    if (position == 4) {
                        phase = 0;
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean("logged",false);
                        editor.commit();
                        FirebaseAuth.getInstance().signOut();
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_picture));
                        imageView.invalidate();
                        dList.setAdapter(createAdapter());
                        dList.deferNotifyDataSetChanged();
                        //todo remove user from preferences
                    }
                }else{
                    if (position == 0) {
                        Intent home = new Intent(getBaseContext(), Home.class);
                        startActivity(home);
                    }
                    if (position == 1) {

                        Intent profile = new Intent(getBaseContext(), Profile.class);
                        startActivity(profile);

                    }

                    if (position == 2) {
                        phase = 0;
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean("logged",false);
                        FirebaseAuth.getInstance().signOut();
                        editor.commit();
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_picture));
                        imageView.invalidate();
                        dList.setAdapter(createAdapter());
                        dList.deferNotifyDataSetChanged();
                        //todo remove user from preferences
                    }
                }
            }
        }
    }
}
