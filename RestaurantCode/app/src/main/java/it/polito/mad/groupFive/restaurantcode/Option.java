package it.polito.mad.groupFive.restaurantcode;

import java.util.ArrayList;

/**
 * Created by MacBookRetina on 14/04/16.
 */
public class Option {
    private ArrayList<String> dishes;

    public ArrayList<String> getDishes() {
        return dishes;
    }

    public Option(){
        dishes=new ArrayList<String>();
        dishes.add("torta");
        dishes.add("mela");
        dishes.add("carota");
        dishes.add("salame");
        dishes.add("zucca");
        dishes.add("pizza");

    }
    public void add_dish(String dish){
        dishes.add(dish);
    }
}
