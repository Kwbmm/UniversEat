package it.polito.mad.groupFive.restaurantcode;

import java.util.ArrayList;

/**
 * Created by MacBookRetina on 14/04/16.
 */
public class Option {
    private ArrayList<String> dishes;
    private int opt_number;
    public ArrayList<String> getDishes() {
        return dishes;
    }

    public int getOpt_number() {
        return opt_number;
    }

    public void setOpt_number(int opt_number) {
        this.opt_number = opt_number;
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
