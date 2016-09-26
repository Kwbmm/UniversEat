package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.SortedMap;

import it.polito.mad.groupFive.restaurantcode.Home.Home;
import it.polito.mad.groupFive.restaurantcode.Login.CreateLogin;
import it.polito.mad.groupFive.restaurantcode.Login.Login_view;
import it.polito.mad.groupFive.restaurantcode.SearchRestaurants.SearchRestaurants;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

public class NavigationDrawer extends AppCompatActivity implements Login_view.OnFragmentInteractionListener {
    private int phase;//0 Logged out 1 logged in
    private boolean usertype;// false user true restaurant manager
    private User user;
    private DrawerAdapter adapter;
    HashMap<Integer,String> optionsname;
    HashMap<Integer,Integer> optionsicon;
    private DrawerLayout drawerLayout;
    private static int REGISTRATION=1;
    SharedPreferences sharedPreferences;
    Handler handler;
    private Notification_Listener notify;
    private static boolean isDBPersistanceEnabled = false;
    int startActivityDelay=250;

    private ActionBarDrawerToggle mDrawerToggle;
    private ListView dList;

    @Override
    protected void onStart() {
        //notify.setAlive(false);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // not a real activity, it's used to extend toolbar and navigation drawer to all activity created
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(4);
        handler = new Handler();
        getUserinfo();
        checkUser();
        createDrawer();
    }

    public DrawerAdapter createAdapter() {
        DrawerAdapter adp;
        optionsname = new HashMap<>();
        optionsicon = new HashMap<>();
        if (phase != 0) {
            if (usertype){
                optionsname.put(0,getResources().getStringArray(R.array.drawer_option_logged_manager)[0]);
                optionsicon.put(0,R.drawable.ic_home_black);
                optionsname.put(1,getResources().getStringArray(R.array.drawer_option_logged_manager)[1]);
                optionsicon.put(1,R.drawable.ic_search_black);
                optionsname.put(2,getResources().getStringArray(R.array.drawer_option_logged_manager)[2]);
                optionsicon.put(2,R.drawable.ic_user_black);
                optionsname.put(3,getResources().getStringArray(R.array.drawer_option_logged_manager)[3]);
                optionsicon.put(3,R.drawable.ic_waiter_black);
                optionsname.put(4,getResources().getStringArray(R.array.drawer_option_logged_manager)[4]);
                optionsicon.put(4,R.drawable.ic_settings_black);
                optionsname.put(5,getResources().getStringArray(R.array.drawer_option_logged_manager)[5]);
                optionsicon.put(5,R.drawable.ic_reservations_black);
                optionsname.put(6,getResources().getStringArray(R.array.drawer_option_logged_manager)[6]);
                optionsicon.put(6,R.drawable.ic_logout_black);
            }
            else{
                optionsname.put(0,getResources().getStringArray(R.array.drawer_option_logged_user)[0]);
                optionsicon.put(0,R.drawable.ic_home_black);
                optionsname.put(1,getResources().getStringArray(R.array.drawer_option_logged_user)[1]);
                optionsicon.put(1,R.drawable.ic_search_black);
                optionsname.put(2,getResources().getStringArray(R.array.drawer_option_logged_user)[2]);
                optionsicon.put(2,R.drawable.ic_user_black);
                optionsname.put(3,getResources().getStringArray(R.array.drawer_option_logged_user)[3]);
                optionsicon.put(3,R.drawable.ic_waiter_black);
                optionsname.put(4,getResources().getStringArray(R.array.drawer_option_logged_user)[4]);
                optionsicon.put(4,R.drawable.ic_logout_black);
            }
        } else {
            optionsname.put(0,getResources().getStringArray(R.array.drawer_option_login)[0]);
            optionsicon.put(0,R.drawable.ic_home_black);
            optionsname.put(1,getResources().getStringArray(R.array.drawer_option_login)[1]);
            optionsicon.put(1,R.drawable.ic_search_black);
            optionsname.put(2,getResources().getStringArray(R.array.drawer_option_login)[2]);
            optionsicon.put(2,R.drawable.ic_login_black);
            optionsname.put(3,getResources().getStringArray(R.array.drawer_option_login)[3]);
            optionsicon.put(3,R.drawable.ic_register_black);
        }
        adp = new DrawerAdapter();
        return adp;
    }

    private class DrawerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return optionsname.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.drawer_item, null);
            RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.drawer_layout);
            TextView textView = (TextView) convertView.findViewById(R.id.drawer_text);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.drawer_icon);
            textView.setText(optionsname.get(position));
            imageView.setImageDrawable(getResources().getDrawable(optionsicon.get(position)));
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REGISTRATION){
            this.onFragmentInteraction();
        }
    }


    @Override
    protected void onStop() {
        drawerLayout.closeDrawers();
        super.onStop();
    }

    private void checkUser() {
        getUserinfo();
        if(phase==1){
            usertype=sharedPreferences.getBoolean("owner",false);
        }
    }

    public void createDrawer() {
        adapter = createAdapter();
        setContentView(R.layout.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
        hideSoftKeyboard();
    }

    private class DrawerListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (phase == 0) {
                if (position == 0) {
                    drawerLayout.closeDrawers();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent home = new Intent(getBaseContext(), Home.class);
                            startActivity(home);
                            overridePendingTransition(0,0);
                        }
                    },startActivityDelay);
                }
                if(position == 1){
                    drawerLayout.closeDrawers();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent searchRestaurants = new Intent(getBaseContext(), SearchRestaurants.class);
                            startActivity(searchRestaurants);
                            overridePendingTransition(0,0);
                        }
                    },startActivityDelay);
                }
                if (position == 2) {
                    dList.setAdapter(createAdapter());
                    dList.deferNotifyDataSetChanged();
                    drawerLayout.closeDrawers();
                    Login_view lw=new Login_view();
                    // FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_up,R.anim.slide_down,R.anim.slide_up,R.anim.slide_down)
                            .addToBackStack(null).add(R.id.frame,lw).commit();
                    //Todo intent create profile
                }
                if (position==3){
                    drawerLayout.closeDrawers();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent registration=new Intent(getBaseContext(), CreateLogin.class);
                            startActivityForResult(registration,REGISTRATION);
                            overridePendingTransition(0,0);
                        }
                    },startActivityDelay);
                }

            } else {
                if(usertype){
                    if (position == 0) {
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent home = new Intent(getBaseContext(), Home.class);
                                startActivity(home);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }
                    if(position == 1){
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent searchRestaurants = new Intent(getBaseContext(), SearchRestaurants.class);
                                startActivity(searchRestaurants);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }

                    if (position == 2) {
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent profile = new Intent(getBaseContext(), Profile.class);
                                startActivity(profile);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }

                    if (position == 3) {
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent orders = new Intent(getBaseContext(), Orders_User.class);
                                startActivity(orders);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }

                    if (position == 4) {
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getBaseContext(), RestaurantManagement.class);
                                startActivity(intent);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }
                    if (position == 5) {
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getBaseContext(), Order_management.class);
                                startActivity(intent);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }
                    if (position == 6) {
                        phase = 0;
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        editor.putBoolean("logged",false);
                        editor.commit();
                        FirebaseAuth.getInstance().signOut();
                        if (notify!=null){
                        notify.stopSelf();}
                        dList.setAdapter(createAdapter());
                        dList.deferNotifyDataSetChanged();
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent home = new Intent(getBaseContext(), Home.class);
                                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(home);
                            }
                        },startActivityDelay);
                    }
                }else{
                    if (position == 0) {
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent home = new Intent(getBaseContext(), Home.class);
                                startActivity(home);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }
                    if(position == 1){
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent searchRestaurants = new Intent(getBaseContext(), SearchRestaurants.class);
                                startActivity(searchRestaurants);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }

                    if (position == 2) {
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent profile = new Intent(getBaseContext(), Profile.class);
                                startActivity(profile);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }

                    if (position == 3) {
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent orders = new Intent(getBaseContext(), Orders_User.class);
                                startActivity(orders);
                                overridePendingTransition(0,0);
                            }
                        },startActivityDelay);
                    }

                    if (position == 4) {
                        phase = 0;
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        editor.putBoolean("logged",false);
                        FirebaseAuth.getInstance().signOut();
                        editor.commit();
                        dList.setAdapter(createAdapter());
                        dList.deferNotifyDataSetChanged();
                        drawerLayout.closeDrawers();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent home = new Intent(getBaseContext(), Home.class);
                                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(home);
                            }
                        },startActivityDelay);
                    }
                }
            }
        }
    }
    private void getFromNetwork(StorageReference storageRoot, final String id, final ImageView imView) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        final File dir = cw.getDir("images", Context.MODE_PRIVATE);
        File filePath = new File(dir,id);
        storageRoot.child(id).getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File img = new File(dir, id);
                Uri imgPath = Uri.fromFile(img);
                try {
                    Bitmap b = new Picture(imgPath,getContentResolver()).getBitmap();
                    imView.setImageBitmap(b);
                } catch (IOException e) {
                    //Log.e("getFromNet",e.getMessage());
                }catch (NullPointerException e){

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setup()  {
        FirebaseDatabase db;
        db = FirebaseDatabase.getInstance();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        String ridc=sharedPreferences.getString("rid",null);
        editor.apply();
        String mail =sharedPreferences.getString("email",null);
        String psw =sharedPreferences.getString("psw",null);
        if (mail!=null&&psw!=null){
            auth.signInWithEmailAndPassword(mail,psw);
            editor.putString("uid",auth.getCurrentUser().getUid());}
        if(ridc!=null){
            notify.startActionOrder(getBaseContext(),ridc);
        }
        if(auth.getCurrentUser().getUid()!=null){
            notify.startActionFavourite(getBaseContext(),auth.getCurrentUser().getUid());
        }}
}
