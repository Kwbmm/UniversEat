package it.polito.mad.groupFive.restaurantcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class NavigationDrawer extends AppCompatActivity {
    private int phase;//0 Logged out 1 logged in
    private int usertype;// 0 user 1 restaurant manager
    private ArrayAdapter<String> adapter;
    private DrawerLayout drawerLayout;

    ActionBarDrawerToggle mDrawerToggle;
    ListView dList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // not a real activity, it's used to extend toolbar and navigation drawer to all activity created
        super.onCreate(savedInstanceState);
        getUserinfo();
        createDrawer();
    }

    public ArrayAdapter<String> createAdapter() {
        ArrayAdapter<String> adp;
        String[] options;
        if (phase != 0) {
            options = getResources().getStringArray(R.array.drawer_option_logged_manager);

        } else {
            options = getResources().getStringArray(R.array.drawer_option_login);

        }
        adp = new ArrayAdapter<String>(this, R.layout.list_item, options);
        return adp;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        drawerLayout.closeDrawers();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    public void createDrawer() {
        adapter = createAdapter();
        setContentView(R.layout.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ImageView imageView = (ImageView) findViewById(R.id.iw);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_picture));
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(intent);
            }
        });
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
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
        if (sharedPreferences.getInt("uid", -1) > 0) {
            phase = 1;
        } else {
            phase = 0;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
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
        Log.v("click", "here");
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

    private class DrawerListener implements ListView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (phase == 0) {
                if (position == 0) {
                    phase = 1;
                    dList.setAdapter(createAdapter());
                    dList.deferNotifyDataSetChanged();
                    //Todo intent create profile
                }

            } else {
                if (position == 0) {
                    Intent home = new Intent(getBaseContext(), Home.class);
                    startActivity(home);
                }
                if (position == 1) {
                    Intent menu = new Intent(getBaseContext(), Menu_details.class);
                    menu.putExtra("rid",2);
                    menu.putExtra("mid",69);
                    startActivity(menu);
                }
                if (position == 3) {
                    Intent intent = new Intent(view.getContext(), Restaurant_management.class);
                    startActivity(intent);
                }
                if (position == 2) {
                    Intent intent = new Intent(view.getContext(), Order_management.class);
                    startActivity(intent);
                }
                if (position == 4) {
                    phase = 0;
                    dList.setAdapter(createAdapter());
                    dList.deferNotifyDataSetChanged();
                    //todo remove user from preferences
                }
            }
        }
    }
}
