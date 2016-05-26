package it.polito.mad.groupFive.restaurantcode;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import it.polito.mad.groupFive.restaurantcode.CreateRestaurant.CreateRestaurant;
import it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu.Create_simple_menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

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
    private static final int CREATE_RESTAURANT = 1;
    private RestaurantOwner user;
    private Restaurant restaurant;
    private SharedPreferences sharedPreferences;
    private Boolean visible=false;
    private View v;
    private MenuItem plus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        v=mlay.inflate(this, R.layout.restaurant_view_edit, mlay);

        showresturant();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(visible==false){
            showresturant();}
        if (requestCode==3){
            update();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_add, menu);
        int rid;
        MenuItem item=menu.findItem(R.id.add_ab);
        plus=item;
        if ((rid=sharedPreferences.getInt("rid",-1))!=-1){
            item.setEnabled(false);
            item.setVisible(false);
        }
        return true;
    }

    private boolean showresturant() {
        String uid,rid;
        final SharedPreferences sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        if ((rid=sharedPreferences.getString("rid",null))!=null){
            uid=sharedPreferences.getString("uid",null);
            try {
                restaurant=new Restaurant(rid);
            } catch (RestaurantException e) {
                e.printStackTrace();
            }

            LinearLayout rview= (LinearLayout) findViewById(R.id.fl_redit);
            v=rview.inflate(this,R.layout.resturant_view_edit_fragment,rview);
            ImageButton modify = (ImageButton) findViewById(R.id.restaurant_edit);
            visible=true;
            RelativeLayout rl=(RelativeLayout) findViewById(R.id.restaurant_view_layout);
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
            Intent intent=new Intent(getApplicationContext(),CreateRestaurant.class);
            intent.putExtra("rid",-1);
            item.setVisible(false);
            startActivityForResult(intent,CREATE_RESTAURANT);

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean getResaurantdata(){
        final String METHOD_NAME = this.getClass().getName()+" - readFiles";


            //this.restaurant.getData();

        TextView rname= (TextView)findViewById(R.id.restaurant_name);
        TextView raddress= (TextView)findViewById(R.id.restaurant_address);
        RatingBar rbar=(RatingBar)findViewById(R.id.restaurant_rating);
        ImageView rmimw = (ImageView) findViewById(R.id.restaurant_image);
        ImageButton edit =(ImageButton) findViewById(R.id.restaurant_edit);
        rname.setText(restaurant.getName());
        raddress.setText(restaurant.getAddress());
        rbar.setRating(restaurant.getRating());
        edit.setOnClickListener( new onEditclick(1));

        try {
          //  rmimw.setImageBitmap(restaurant.getImageBitmap());
        } catch (NullPointerException e){
            Log.e("immagine non caricata"," ");
        }
//            rmimw.setImageDrawable(restaurant.getImageDrawable());


        return true;

    }
    private void update()  {


           // this.restaurant.getData();

        TextView rname= (TextView)findViewById(R.id.restaurant_name);
        TextView raddress= (TextView)findViewById(R.id.restaurant_address);
        RatingBar rbar=(RatingBar)findViewById(R.id.restaurant_rating);
        ImageView rmimw = (ImageView) findViewById(R.id.restaurant_image);
        rname.setText(restaurant.getName());
        raddress.setText(restaurant.getAddress());
        rbar.setRating(restaurant.getRating());
       // rmimw.setImageBitmap(restaurant.getImageBitmap());

    }

    public class onEditclick implements View.OnClickListener{
        private int position;
        public onEditclick(int position){
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialog=new AlertDialog.Builder(Restaurant_management.this);
            final CharSequence[] items = { "Edit", "Delete","Cancel" };
            dialog.setTitle("Edit Options");
            dialog.setItems(items,new onPositionClickDialog(position));
            dialog.show();

        }
    }

    public class onPositionClickDialog implements DialogInterface.OnClickListener{
        private int position;

        public onPositionClickDialog(int position){
            this.position=position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:{
                    //TODO:edit intent
                    Intent edit_menu=new Intent(getBaseContext(),CreateRestaurant.class);
                    edit_menu.putExtra("rid",restaurant.getRid());
                    startActivityForResult(edit_menu,3);
                    break;
                }
                case 1:{
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.remove("rid");
                    editor.commit();
                    v.setVisibility(View.INVISIBLE);
                    plus.setVisible(true);
                    break;
                }
                default:{
                    dialog.cancel();
                }
            }}
}}
