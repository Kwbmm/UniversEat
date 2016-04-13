package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

public class Restaurant_management extends NavigationDrawer {


    /* Shared preference note

    File name shared in string.xml
    can be used with:-> getString(R.string.user_pref)
    file contains:
    int uid (user id)
    int rid (restaurant id)

    Example:
    SharedPreferences sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putInt("uid",1);
            editor.commit();

    */
    private User user;
    private Restaurant restaurant;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.restaurant_view_edit, mlay);
        showresturant();






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_add, menu);
        int rid;
        MenuItem item=menu.findItem(R.id.add_ab);
        if ((rid=sharedPreferences.getInt("rid",-1))!=-1){
            item.setEnabled(false);
            item.setVisible(false);
        }
        return true;
    }

    private boolean showresturant() {
        int uid=2,rid=2;
        SharedPreferences sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        if ((rid=sharedPreferences.getInt("rid",-1))!=-1){
            uid=sharedPreferences.getInt("uid",-1);
            try {
                user=new User(this,rid,uid);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RestaurantException e) {
                e.printStackTrace();
            } catch (UserException e) {
                e.printStackTrace();
            }
            restaurant=user.getRestaurant();

            FrameLayout rview= (FrameLayout) findViewById(R.id.fl_redit);
            rview.inflate(this,R.layout.resturant_view_edit_fragment,rview);
            ImageButton modify = (ImageButton) findViewById(R.id.rved_modify);
            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("ciao","ciao");
                }
            });
            RelativeLayout rl=(RelativeLayout) findViewById(R.id.rvef_rectangle);
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent menuview =new Intent(v.getContext(),Menu_view_edit.class);
                    startActivity(menuview);
                }
            });


            getResaurantdata();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor= sharedPreferences.edit();
        if(item.getItemId()==R.id.add_ab){
            Log.v("intent","newRest");

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean getResaurantdata(){

        TextView rname= (TextView)findViewById(R.id.rs_name);
        TextView raddress= (TextView)findViewById(R.id.rwef_address);
        RatingBar rbar=(RatingBar)findViewById(R.id.rwef_rate);
        ImageView rmimw = (ImageView) findViewById(R.id.iwr);
        try {
            restaurant.getData();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        rname.setText(restaurant.getName());
        raddress.setText(restaurant.getAddress());
        rbar.setRating(restaurant.getRating());
        //rmimw.setImageBitmap(restaurant.getImageBitmap());


        return true;

    }
}
