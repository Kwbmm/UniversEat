package it.polito.mad.groupFive.restaurantcode;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class NavigationDrawer extends AppCompatActivity {
    ActionBarDrawerToggle mDrawerToggle;
    ListView dList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // not a real activity, it's used to extend toolbar and navigation drawer to all activity created
        super.onCreate(savedInstanceState);
        String[] options;
        options= getResources().getStringArray(R.array.drawer_option_logged_manager);
        setContentView(R.layout.drawer);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ImageView imageView= (ImageView)findViewById(R.id.iw);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_picture));
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(intent);
            }
        });
        dList= (ListView)findViewById(R.id.left_drawer);
        dList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, options));
        dList.setOnItemClickListener( new DrawerListener());


        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        {

            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                view.bringToFront();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

            public void onDrawerOpened(View drawerView)
            {

                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                drawerView.bringToFront();
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);



    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
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
    private class DrawerListener implements ListView.OnItemClickListener{


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position==2){
                Intent intent= new Intent(view.getContext(),Restaurant_management.class);
                startActivity(intent);
            }
            if(position==1){
                Intent intent= new Intent(view.getContext(),Order_management.class);
                startActivity(intent);
            }
        }
    }
}
