package it.polito.mad.groupFive.restaurantcode.CreateMenu;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Random;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OptionException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class Create_menu extends NavigationDrawer implements Create_menu_frag.OnFragmentInteractionListener,Create_menu_frag2.OnFragmentInteractionListener,Create_menu_frag2.shareDish,Add_dish.OnFragmentInteractionListener,Add_dish.shareDish,Delete_dish.OnFragmentInteractionListener,Delete_dish.shareDish{
    private Restaurant restaurant=null;
    private RestaurantOwner user=null;
    private Menu menu=null;

    Option option;
    int dimension;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME =this.getClass().getName()+" - OnCreate";
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_menu, mlay);
        Create_menu_frag fragment= new Create_menu_frag();
        getSupportFragmentManager().beginTransaction().add(R.id.acm_1,fragment).commit();




        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref), Create_menu.MODE_PRIVATE);

        try {
            restaurant= new Restaurant(getBaseContext(),sp.getString("rid",null));
            restaurant.getData();
            this.menu=new Menu(restaurant);
        } catch (RestaurantException e) {
            e.printStackTrace();
        } catch (MenuException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void onChangeFrag(Menu m) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag";
        this.menu.setName(m.getName());
        this.menu.setDescription(m.getDescription());
        this.menu.setType(m.getType());
        this.menu.setChoiceAmount(m.getChoiceAmount());
        ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> ops=menu.getOptions();
        int count=0;
        while (count<menu.getChoiceAmount()){
            try {
                ops.add(new it.polito.mad.groupFive.restaurantcode.datastructures.Option(restaurant));
                count++;
            } catch (OptionException e) {
                e.printStackTrace();
            }
        }


        menu.setOptions(ops);
        option=new Option(this.menu,this.restaurant);
        Log.d("menu",menu.getChoiceAmount()+"");
        //this.menu.setImage64(m.getImage64());
        Log.d(METHOD_NAME,"Name: "+menu.getName());
        Log.d(METHOD_NAME,"Description: "+menu.getDescription());



        Create_menu_frag2 frag2 = new Create_menu_frag2();
        getSupportFragmentManager().beginTransaction().replace(R.id.acm_1,frag2).addToBackStack(null).commit();

    }
    public void onChangeFrag1(Menu m){
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag1";
        this.menu.setPrice(m.getPrice());
        Log.d(METHOD_NAME,"Price: "+ String.valueOf(menu.getPrice()));
        this.menu.setBeverage(m.isBeverage());
        this.menu.setServiceFee(m.isServiceFee());

        try {
            restaurant.addMenu(this.menu);
            restaurant.saveData();
            finish();
        } catch (RestaurantException e) {
            e.printStackTrace();
        }


//TODO return to menu view of the restaurant
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public Option getOption() {
        return option;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
    public static int randInt() {
        Random rand= new Random();

        return rand.nextInt(Integer.MAX_VALUE -1 );
    }

}
